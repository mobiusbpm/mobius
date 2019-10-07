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
package mobius.engine.impl.cmd;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import mobius.common.engine.api.FlowableIllegalArgumentException;
import mobius.common.engine.api.FlowableObjectNotFoundException;
import mobius.common.engine.impl.interceptor.Command;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.engine.RepositoryService;
import mobius.engine.impl.cfg.ProcessEngineConfigurationImpl;
import mobius.engine.impl.util.CommandContextUtil;
import mobius.engine.repository.Deployment;
import mobius.engine.repository.ProcessDefinition;
import mobius.form.api.FormFieldHandler;
import mobius.form.api.FormInfo;
import mobius.form.api.FormRepositoryService;
import mobius.form.api.FormService;
import mobius.task.api.TaskInfo;
import mobius.task.api.history.HistoricTaskInstance;
import mobius.variable.api.history.HistoricVariableInstance;

/**
 *
 */
public class GetTaskFormModelCmd implements Command<FormInfo>, Serializable {

    private static final long serialVersionUID = 1L;

    protected String taskId;
    protected boolean ignoreVariables;

    public GetTaskFormModelCmd(String taskId, boolean ignoreVariables) {
        this.taskId = taskId;
        this.ignoreVariables = ignoreVariables;
    }

    @Override
    public FormInfo execute(CommandContext commandContext) {
        ProcessEngineConfigurationImpl processEngineConfiguration = CommandContextUtil.getProcessEngineConfiguration(commandContext);
        FormService formService = CommandContextUtil.getFormService();
        if (formService == null) {
            throw new FlowableIllegalArgumentException("Form engine is not initialized");
        }

        TaskInfo task = CommandContextUtil.getTaskService(commandContext).getTask(taskId);
        Date endTime = null;
        if (task == null) {
            task = CommandContextUtil.getHistoricTaskService(commandContext).getHistoricTask(taskId);
            if (task != null) {
                endTime = ((HistoricTaskInstance) task).getEndTime();
            }
        }
        
        if (task == null) {
            throw new FlowableObjectNotFoundException("Task not found with id " + taskId);
        }

        Map<String, Object> variables = new HashMap<>();
        if (!ignoreVariables && task.getProcessInstanceId() != null) {
            List<HistoricVariableInstance> variableInstances = processEngineConfiguration.getHistoryService()
                    .createHistoricVariableInstanceQuery()
                    .processInstanceId(task.getProcessInstanceId())
                    .list();

            for (HistoricVariableInstance historicVariableInstance : variableInstances) {
                variables.put(historicVariableInstance.getVariableName(), historicVariableInstance.getValue());
            }
        }

        String parentDeploymentId = null;
        if (StringUtils.isNotEmpty(task.getProcessDefinitionId())) {
            RepositoryService repositoryService = processEngineConfiguration.getRepositoryService();
            ProcessDefinition processDefinition = repositoryService.getProcessDefinition(task.getProcessDefinitionId());
            Deployment deployment = repositoryService.createDeploymentQuery().deploymentId(processDefinition.getDeploymentId()).singleResult();
            parentDeploymentId = deployment.getParentDeploymentId();
        }
        
        FormInfo formInfo = null;
        if (ignoreVariables) {
            FormRepositoryService formRepositoryService = CommandContextUtil.getFormRepositoryService();
            formInfo = formRepositoryService.getFormModelByKeyAndParentDeploymentId(task.getFormKey(), parentDeploymentId,
                            task.getTenantId(), processEngineConfiguration.isFallbackToDefaultTenant());
            
        } else if (endTime != null) {
            formInfo = formService.getFormInstanceModelByKeyAndParentDeploymentId(task.getFormKey(), parentDeploymentId,
                            taskId, task.getProcessInstanceId(), variables, task.getTenantId(), processEngineConfiguration.isFallbackToDefaultTenant());

        } else {
            formInfo = formService.getFormModelWithVariablesByKeyAndParentDeploymentId(task.getFormKey(), parentDeploymentId,
                            taskId, variables, task.getTenantId(), processEngineConfiguration.isFallbackToDefaultTenant());
        }

        // If form does not exists, we don't want to leak out this info to just anyone
        if (formInfo == null) {
            throw new FlowableObjectNotFoundException("Form model for task " + task.getTaskDefinitionKey() + " cannot be found for form key " + task.getFormKey());
        }

        FormFieldHandler formFieldHandler = CommandContextUtil.getProcessEngineConfiguration(commandContext).getFormFieldHandler();
        formFieldHandler.enrichFormFields(formInfo);

        return formInfo;
    }

}
