/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mobius.engine.impl.bpmn.behavior;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import mobius.bpmn.model.CallActivity;
import mobius.bpmn.model.FlowElement;
import mobius.bpmn.model.IOParameter;
import mobius.bpmn.model.MapExceptionEntry;
import mobius.bpmn.model.Process;
import mobius.bpmn.model.ValuedDataObject;
import mobius.common.engine.api.FlowableException;
import mobius.common.engine.api.FlowableObjectNotFoundException;
import mobius.common.engine.api.delegate.Expression;
import mobius.common.engine.api.delegate.event.FlowableEngineEventType;
import mobius.common.engine.api.delegate.event.FlowableEventDispatcher;
import mobius.common.engine.api.scope.ScopeTypes;
import mobius.common.engine.impl.el.ExpressionManager;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.engine.ProcessEngineConfiguration;
import mobius.engine.delegate.DelegateExecution;
import mobius.engine.delegate.event.impl.FlowableEventBuilder;
import mobius.engine.impl.cfg.ProcessEngineConfigurationImpl;
import mobius.engine.impl.context.Context;
import mobius.engine.impl.delegate.SubProcessActivityBehavior;
import mobius.engine.impl.persistence.entity.ExecutionEntity;
import mobius.engine.impl.persistence.entity.ExecutionEntityManager;
import mobius.engine.impl.persistence.entity.ProcessDefinitionEntity;
import mobius.engine.impl.persistence.entity.ProcessDefinitionEntityManager;
import mobius.engine.impl.util.CommandContextUtil;
import mobius.engine.impl.util.EntityLinkUtil;
import mobius.engine.impl.util.ProcessDefinitionUtil;
import mobius.engine.interceptor.StartSubProcessInstanceAfterContext;
import mobius.engine.interceptor.StartSubProcessInstanceBeforeContext;
import mobius.engine.repository.ProcessDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the BPMN 2.0 call activity (limited currently to calling a subprocess and not (yet) a global task).
 *
 * @author Joram Barrez
 * @author Tijs Rademakers
 */
public class CallActivityBehavior extends AbstractBpmnActivityBehavior implements SubProcessActivityBehavior {

    private static final Logger LOGGER = LoggerFactory.getLogger(CallActivityBehavior.class);

    private static final long serialVersionUID = 1L;

    public static final String CALLED_ELEMENT_TYPE_KEY = "key";
    public static final String CALLED_ELEMENT_TYPE_ID = "id";

    protected String calledElement;
    protected String calledElementType;
    protected Expression calledElementExpression;
    protected Boolean fallbackToDefaultTenant;
    protected List<MapExceptionEntry> mapExceptions;

    public CallActivityBehavior(String processDefinitionKey, String calledElementType, Boolean fallbackToDefaultTenant, List<MapExceptionEntry> mapExceptions) {
        this.calledElement = processDefinitionKey;
        this.calledElementType = calledElementType;
        this.mapExceptions = mapExceptions;
        this.fallbackToDefaultTenant = fallbackToDefaultTenant;
    }

    public CallActivityBehavior(Expression processDefinitionExpression, String calledElementType, List<MapExceptionEntry> mapExceptions, Boolean fallbackToDefaultTenant) {
        this.calledElementExpression = processDefinitionExpression;
        this.calledElementType = calledElementType;
        this.mapExceptions = mapExceptions;
        this.fallbackToDefaultTenant = fallbackToDefaultTenant;
    }

    @Override
    public void execute(DelegateExecution execution) {

        ExecutionEntity executionEntity = (ExecutionEntity) execution;
        CallActivity callActivity = (CallActivity) executionEntity.getCurrentFlowElement();
        
        CommandContext commandContext = CommandContextUtil.getCommandContext();

        ProcessEngineConfigurationImpl processEngineConfiguration = CommandContextUtil.getProcessEngineConfiguration(commandContext);

        ProcessDefinition processDefinition = getProcessDefinition(execution, callActivity, processEngineConfiguration);

        // Get model from cache
        Process subProcess = ProcessDefinitionUtil.getProcess(processDefinition.getId());
        if (subProcess == null) {
            throw new FlowableException("Cannot start a sub process instance. Process model " + processDefinition.getName() + " (id = " + processDefinition.getId() + ") could not be found");
        }

        FlowElement initialFlowElement = subProcess.getInitialFlowElement();
        if (initialFlowElement == null) {
            throw new FlowableException("No start element found for process definition " + processDefinition.getId());
        }

        // Do not start a process instance if the process definition is suspended
        if (ProcessDefinitionUtil.isProcessDefinitionSuspended(processDefinition.getId())) {
            throw new FlowableException("Cannot start process instance. Process definition " + processDefinition.getName() + " (id = " + processDefinition.getId() + ") is suspended");
        }

        ExecutionEntityManager executionEntityManager = CommandContextUtil.getExecutionEntityManager(commandContext);
        ExpressionManager expressionManager = processEngineConfiguration.getExpressionManager();

        String businessKey = null;
        if (!StringUtils.isEmpty(callActivity.getBusinessKey())) {
            Expression expression = expressionManager.createExpression(callActivity.getBusinessKey());
            businessKey = expression.getValue(execution).toString();

        } else if (callActivity.isInheritBusinessKey()) {
            ExecutionEntity processInstance = executionEntityManager.findById(execution.getProcessInstanceId());
            businessKey = processInstance.getBusinessKey();
        }
        
        Map<String, Object> variables = new HashMap<>();
        
        StartSubProcessInstanceBeforeContext instanceBeforeContext = new StartSubProcessInstanceBeforeContext(businessKey, callActivity.getProcessInstanceName(), 
                        variables, executionEntity, callActivity.getInParameters(), callActivity.isInheritVariables(), 
                        initialFlowElement.getId(), initialFlowElement, subProcess, processDefinition);
        
        if (processEngineConfiguration.getStartProcessInstanceInterceptor() != null) {
            processEngineConfiguration.getStartProcessInstanceInterceptor().beforeStartSubProcessInstance(instanceBeforeContext);
        }

        ExecutionEntity subProcessInstance = CommandContextUtil.getExecutionEntityManager(commandContext).createSubprocessInstance(
                        instanceBeforeContext.getProcessDefinition(), instanceBeforeContext.getCallActivityExecution(), 
                        instanceBeforeContext.getBusinessKey(), instanceBeforeContext.getInitialActivityId());

        FlowableEventDispatcher eventDispatcher = processEngineConfiguration.getEventDispatcher();
        if (eventDispatcher != null && eventDispatcher.isEnabled()) {
            CommandContextUtil.getProcessEngineConfiguration().getEventDispatcher().dispatchEvent(
                    FlowableEventBuilder.createEntityEvent(FlowableEngineEventType.PROCESS_CREATED, subProcessInstance));
        }

        // process template-defined data objects
        subProcessInstance.setVariables(processDataObjects(subProcess.getDataObjects()));

        if (instanceBeforeContext.isInheritVariables()) {
            Map<String, Object> executionVariables = execution.getVariables();
            for (Map.Entry<String, Object> entry : executionVariables.entrySet()) {
                instanceBeforeContext.getVariables().put(entry.getKey(), entry.getValue());
            }
        }
        
        // copy process variables
        for (IOParameter inParameter : instanceBeforeContext.getInParameters()) {

            Object value = null;
            if (StringUtils.isNotEmpty(inParameter.getSourceExpression())) {
                Expression expression = expressionManager.createExpression(inParameter.getSourceExpression().trim());
                value = expression.getValue(execution);

            } else {
                value = execution.getVariable(inParameter.getSource());
            }

            String variableName = null;
            if (StringUtils.isNotEmpty(inParameter.getTargetExpression())) {
                Expression expression = expressionManager.createExpression(inParameter.getTargetExpression());
                Object variableNameValue = expression.getValue(execution);
                if (variableNameValue != null) {
                    variableName = variableNameValue.toString();
                } else {
                    LOGGER.warn("In parameter target expression {} did not resolve to a variable name, this is most likely a programmatic error",
                        inParameter.getTargetExpression());
                }

            } else if (StringUtils.isNotEmpty(inParameter.getTarget())){
                variableName = inParameter.getTarget();

            }

            instanceBeforeContext.getVariables().put(variableName, value);
        }

        if (!instanceBeforeContext.getVariables().isEmpty()) {
            initializeVariables(subProcessInstance, instanceBeforeContext.getVariables());
        }
        
        // Process instance name is resolved after setting the variables on the process instance, so they can be used in the expression
        String processInstanceName = null;
        if (StringUtils.isNotEmpty(instanceBeforeContext.getProcessInstanceName())) {
            Expression processInstanceNameExpression = expressionManager.createExpression(instanceBeforeContext.getProcessInstanceName());
            processInstanceName = processInstanceNameExpression.getValue(subProcessInstance).toString();
            subProcessInstance.setName(processInstanceName);
        }

        if (eventDispatcher != null && eventDispatcher.isEnabled()) {
            eventDispatcher.dispatchEvent(FlowableEventBuilder.createEntityEvent(FlowableEngineEventType.ENTITY_INITIALIZED, subProcessInstance));
        }
        
        if (processEngineConfiguration.isEnableEntityLinks()) {
            EntityLinkUtil.copyExistingEntityLinks(execution.getProcessInstanceId(), subProcessInstance.getId(), ScopeTypes.BPMN);
            EntityLinkUtil.createNewEntityLink(execution.getProcessInstanceId(), subProcessInstance.getId(), ScopeTypes.BPMN);
        }

        CommandContextUtil.getActivityInstanceEntityManager(commandContext).recordSubProcessInstanceStart(executionEntity, subProcessInstance);

        // Create the first execution that will visit all the process definition elements
        ExecutionEntity subProcessInitialExecution = executionEntityManager.createChildExecution(subProcessInstance);
        subProcessInitialExecution.setCurrentFlowElement(instanceBeforeContext.getInitialFlowElement());

        CommandContextUtil.getAgenda().planContinueProcessOperation(subProcessInitialExecution);

        if (eventDispatcher != null && eventDispatcher.isEnabled()) {
            eventDispatcher.dispatchEvent(FlowableEventBuilder.createProcessStartedEvent(subProcessInitialExecution, instanceBeforeContext.getVariables(), false));
        }
        
        if (processEngineConfiguration.getStartProcessInstanceInterceptor() != null) {
            StartSubProcessInstanceAfterContext instanceAfterContext = new StartSubProcessInstanceAfterContext(subProcessInstance, subProcessInitialExecution, 
                            instanceBeforeContext.getVariables(), instanceBeforeContext.getCallActivityExecution(), instanceBeforeContext.getInParameters(), 
                            instanceBeforeContext.getInitialFlowElement(), instanceBeforeContext.getProcess(), instanceBeforeContext.getProcessDefinition());
            
            processEngineConfiguration.getStartProcessInstanceInterceptor().afterStartSubProcessInstance(instanceAfterContext);
        }
    }

    protected ProcessDefinition getProcessDefinition(DelegateExecution execution, CallActivity callActivity, ProcessEngineConfigurationImpl processEngineConfiguration) {
        ProcessDefinition processDefinition;
        switch (StringUtils.isNotEmpty(calledElementType) ? calledElementType : CALLED_ELEMENT_TYPE_KEY) {
            case CALLED_ELEMENT_TYPE_ID:
                processDefinition = getProcessDefinitionById(execution);
                break;
            case CALLED_ELEMENT_TYPE_KEY:
                processDefinition = getProcessDefinitionByKey(execution, callActivity.isSameDeployment(), processEngineConfiguration);
                break;
            default:
                throw new FlowableException("Unrecognized calledElementType [" + calledElementType + "]");
        }
        return processDefinition;
    }

    @Override
    public void completing(DelegateExecution execution, DelegateExecution subProcessInstance) throws Exception {
        // only data. no control flow available on this execution.

        ExpressionManager expressionManager = CommandContextUtil.getProcessEngineConfiguration().getExpressionManager();

        // copy process variables
        ExecutionEntity executionEntity = (ExecutionEntity) execution;
        CallActivity callActivity = (CallActivity) executionEntity.getCurrentFlowElement();

        for (IOParameter outParameter : callActivity.getOutParameters()) {

            Object value = null;
            if (StringUtils.isNotEmpty(outParameter.getSourceExpression())) {
                Expression expression = expressionManager.createExpression(outParameter.getSourceExpression().trim());
                value = expression.getValue(subProcessInstance);

            } else {
                value = subProcessInstance.getVariable(outParameter.getSource());
            }

            String variableName = null;
            if (StringUtils.isNotEmpty(outParameter.getTarget()))  {
                variableName = outParameter.getTarget();

            } else if (StringUtils.isNotEmpty(outParameter.getTargetExpression())) {
                Expression expression = expressionManager.createExpression(outParameter.getTargetExpression());

                Object variableNameValue = expression.getValue(subProcessInstance);
                if (variableNameValue != null) {
                    variableName = variableNameValue.toString();
                } else {
                    LOGGER.warn("Out parameter target expression {} did not resolve to a variable name, this is most likely a programmatic error",
                        outParameter.getTargetExpression());
                }

            }

            if (callActivity.isUseLocalScopeForOutParameters()) {
                execution.setVariableLocal(variableName, value);
            } else {
                execution.setVariable(variableName, value);
            }
        }
    }

    @Override
    public void completed(DelegateExecution execution) throws Exception {
        // only control flow. no sub process instance data available
        leave(execution);
    }

    protected ProcessDefinition getProcessDefinitionById(DelegateExecution execution) {
        return CommandContextUtil.getProcessEngineConfiguration().getDeploymentManager()
            .findDeployedProcessDefinitionById(getCalledElementValue(execution));
    }

    protected ProcessDefinition getProcessDefinitionByKey(DelegateExecution execution, boolean isSameDeployment, ProcessEngineConfigurationImpl processEngineConfiguration) {
        String processDefinitionKey = getCalledElementValue(execution);
        String tenantId = execution.getTenantId();

        ProcessDefinitionEntityManager processDefinitionEntityManager = Context.getProcessEngineConfiguration().getProcessDefinitionEntityManager();
        ProcessDefinitionEntity processDefinition;

        if (isSameDeployment) {
            String deploymentId = ProcessDefinitionUtil.getProcessDefinition(execution.getProcessDefinitionId()).getDeploymentId();
            if (tenantId == null || ProcessEngineConfiguration.NO_TENANT_ID.equals(tenantId)) {
                processDefinition = processDefinitionEntityManager.findProcessDefinitionByDeploymentAndKey(deploymentId, processDefinitionKey);
            } else {
                processDefinition = processDefinitionEntityManager.findProcessDefinitionByDeploymentAndKeyAndTenantId(deploymentId, processDefinitionKey, tenantId);
            }

            if (processDefinition != null) {
                return processDefinition;
            }
        }

        if (tenantId == null || ProcessEngineConfiguration.NO_TENANT_ID.equals(tenantId)) {
            processDefinition = processDefinitionEntityManager.findLatestProcessDefinitionByKey(processDefinitionKey);
        } else {
            processDefinition = processDefinitionEntityManager.findLatestProcessDefinitionByKeyAndTenantId(processDefinitionKey, tenantId);
            if (processDefinition == null && ((this.fallbackToDefaultTenant != null && this.fallbackToDefaultTenant) || processEngineConfiguration.isFallbackToDefaultTenant())) {

                String defaultTenant = processEngineConfiguration.getDefaultTenantProvider().getDefaultTenant(tenantId, ScopeTypes.BPMN, processDefinitionKey);
                if (StringUtils.isNotEmpty(defaultTenant)) {
                    processDefinition = processDefinitionEntityManager.findLatestProcessDefinitionByKeyAndTenantId(
                                    processDefinitionKey, defaultTenant);
                } else {
                    processDefinition = processDefinitionEntityManager.findLatestProcessDefinitionByKey(processDefinitionKey);
                }
            }
        }

        if (processDefinition == null) {
            throw new FlowableObjectNotFoundException("Process definition " + processDefinitionKey + " was not found in sameDeployment["+ isSameDeployment +
                "] tenantId["+ tenantId+ "] fallbackToDefaultTenant["+ this.fallbackToDefaultTenant + "]");
        }
        return processDefinition;
    }

    protected String getCalledElementValue(DelegateExecution execution) {
        String calledElementValue;
        if (calledElementExpression != null) {
            calledElementValue = (String) calledElementExpression.getValue(execution);
        } else {
            calledElementValue = calledElement;
        }
        return calledElementValue;
    }

    protected Map<String, Object> processDataObjects(Collection<ValuedDataObject> dataObjects) {
        Map<String, Object> variablesMap = new HashMap<>();
        // convert data objects to process variables
        if (dataObjects != null) {
            variablesMap = new HashMap<>(dataObjects.size());
            for (ValuedDataObject dataObject : dataObjects) {
                variablesMap.put(dataObject.getName(), dataObject.getValue());
            }
        }
        return variablesMap;
    }

    // Allow a subclass to override how variables are initialized.
    protected void initializeVariables(ExecutionEntity subProcessInstance, Map<String, Object> variables) {
        subProcessInstance.setVariables(variables);
    }

    public void setCalledElement(String calledElement) {
        this.calledElement = calledElement;
    }

    public String getCalledElement() {
        return calledElement;
    }
}