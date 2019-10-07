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
package mobius.engine.impl.bpmn.parser.factory;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import mobius.bpmn.model.Activity;
import mobius.bpmn.model.BoundaryEvent;
import mobius.bpmn.model.BpmnModel;
import mobius.bpmn.model.BusinessRuleTask;
import mobius.bpmn.model.CallActivity;
import mobius.bpmn.model.CancelEventDefinition;
import mobius.bpmn.model.CaseServiceTask;
import mobius.bpmn.model.CompensateEventDefinition;
import mobius.bpmn.model.ConditionalEventDefinition;
import mobius.bpmn.model.EndEvent;
import mobius.bpmn.model.ErrorEventDefinition;
import mobius.bpmn.model.Escalation;
import mobius.bpmn.model.EscalationEventDefinition;
import mobius.bpmn.model.EventGateway;
import mobius.bpmn.model.EventSubProcess;
import mobius.bpmn.model.ExclusiveGateway;
import mobius.bpmn.model.FieldExtension;
import mobius.bpmn.model.InclusiveGateway;
import mobius.bpmn.model.IntermediateCatchEvent;
import mobius.bpmn.model.ManualTask;
import mobius.bpmn.model.MapExceptionEntry;
import mobius.bpmn.model.MessageEventDefinition;
import mobius.bpmn.model.ParallelGateway;
import mobius.bpmn.model.ReceiveTask;
import mobius.bpmn.model.ScriptTask;
import mobius.bpmn.model.SendTask;
import mobius.bpmn.model.ServiceTask;
import mobius.bpmn.model.Signal;
import mobius.bpmn.model.SignalEventDefinition;
import mobius.bpmn.model.StartEvent;
import mobius.bpmn.model.SubProcess;
import mobius.bpmn.model.Task;
import mobius.bpmn.model.TaskWithFieldExtensions;
import mobius.bpmn.model.TerminateEventDefinition;
import mobius.bpmn.model.ThrowEvent;
import mobius.bpmn.model.TimerEventDefinition;
import mobius.bpmn.model.Transaction;
import mobius.bpmn.model.UserTask;
import mobius.common.engine.api.FlowableException;
import mobius.common.engine.api.delegate.Expression;
import mobius.common.engine.impl.scripting.ScriptingEngines;
import mobius.engine.delegate.BusinessRuleTaskDelegate;
import mobius.engine.impl.bpmn.behavior.AbstractBpmnActivityBehavior;
import mobius.engine.impl.bpmn.behavior.AdhocSubProcessActivityBehavior;
import mobius.engine.impl.bpmn.behavior.BoundaryCancelEventActivityBehavior;
import mobius.engine.impl.bpmn.behavior.BoundaryCompensateEventActivityBehavior;
import mobius.engine.impl.bpmn.behavior.BoundaryConditionalEventActivityBehavior;
import mobius.engine.impl.bpmn.behavior.BoundaryEscalationEventActivityBehavior;
import mobius.engine.impl.bpmn.behavior.BoundaryEventActivityBehavior;
import mobius.engine.impl.bpmn.behavior.BoundaryMessageEventActivityBehavior;
import mobius.engine.impl.bpmn.behavior.BoundarySignalEventActivityBehavior;
import mobius.engine.impl.bpmn.behavior.BoundaryTimerEventActivityBehavior;
import mobius.engine.impl.bpmn.behavior.BusinessRuleTaskActivityBehavior;
import mobius.engine.impl.bpmn.behavior.CallActivityBehavior;
import mobius.engine.impl.bpmn.behavior.CancelEndEventActivityBehavior;
import mobius.engine.impl.bpmn.behavior.CaseTaskActivityBehavior;
import mobius.engine.impl.bpmn.behavior.DmnActivityBehavior;
import mobius.engine.impl.bpmn.behavior.ErrorEndEventActivityBehavior;
import mobius.engine.impl.bpmn.behavior.EscalationEndEventActivityBehavior;
import mobius.engine.impl.bpmn.behavior.EventBasedGatewayActivityBehavior;
import mobius.engine.impl.bpmn.behavior.EventSubProcessActivityBehavior;
import mobius.engine.impl.bpmn.behavior.EventSubProcessConditionalStartEventActivityBehavior;
import mobius.engine.impl.bpmn.behavior.EventSubProcessErrorStartEventActivityBehavior;
import mobius.engine.impl.bpmn.behavior.EventSubProcessEscalationStartEventActivityBehavior;
import mobius.engine.impl.bpmn.behavior.EventSubProcessMessageStartEventActivityBehavior;
import mobius.engine.impl.bpmn.behavior.EventSubProcessSignalStartEventActivityBehavior;
import mobius.engine.impl.bpmn.behavior.EventSubProcessTimerStartEventActivityBehavior;
import mobius.engine.impl.bpmn.behavior.ExclusiveGatewayActivityBehavior;
import mobius.engine.impl.bpmn.behavior.InclusiveGatewayActivityBehavior;
import mobius.engine.impl.bpmn.behavior.IntermediateCatchConditionalEventActivityBehavior;
import mobius.engine.impl.bpmn.behavior.IntermediateCatchEventActivityBehavior;
import mobius.engine.impl.bpmn.behavior.IntermediateCatchMessageEventActivityBehavior;
import mobius.engine.impl.bpmn.behavior.IntermediateCatchSignalEventActivityBehavior;
import mobius.engine.impl.bpmn.behavior.IntermediateCatchTimerEventActivityBehavior;
import mobius.engine.impl.bpmn.behavior.IntermediateThrowCompensationEventActivityBehavior;
import mobius.engine.impl.bpmn.behavior.IntermediateThrowEscalationEventActivityBehavior;
import mobius.engine.impl.bpmn.behavior.IntermediateThrowNoneEventActivityBehavior;
import mobius.engine.impl.bpmn.behavior.IntermediateThrowSignalEventActivityBehavior;
import mobius.engine.impl.bpmn.behavior.MailActivityBehavior;
import mobius.engine.impl.bpmn.behavior.ManualTaskActivityBehavior;
import mobius.engine.impl.bpmn.behavior.NoneEndEventActivityBehavior;
import mobius.engine.impl.bpmn.behavior.NoneStartEventActivityBehavior;
import mobius.engine.impl.bpmn.behavior.ParallelGatewayActivityBehavior;
import mobius.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;
import mobius.engine.impl.bpmn.behavior.ReceiveTaskActivityBehavior;
import mobius.engine.impl.bpmn.behavior.ScriptTaskActivityBehavior;
import mobius.engine.impl.bpmn.behavior.SequentialMultiInstanceBehavior;
import mobius.engine.impl.bpmn.behavior.ServiceTaskDelegateExpressionActivityBehavior;
import mobius.engine.impl.bpmn.behavior.ServiceTaskExpressionActivityBehavior;
import mobius.engine.impl.bpmn.behavior.ShellActivityBehavior;
import mobius.engine.impl.bpmn.behavior.SubProcessActivityBehavior;
import mobius.engine.impl.bpmn.behavior.TaskActivityBehavior;
import mobius.engine.impl.bpmn.behavior.TerminateEndEventActivityBehavior;
import mobius.engine.impl.bpmn.behavior.TransactionActivityBehavior;
import mobius.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import mobius.engine.impl.bpmn.behavior.WebServiceActivityBehavior;
import mobius.engine.impl.bpmn.helper.ClassDelegate;
import mobius.engine.impl.bpmn.helper.ClassDelegateFactory;
import mobius.engine.impl.bpmn.helper.DefaultClassDelegateFactory;
import mobius.engine.impl.bpmn.parser.FieldDeclaration;
import mobius.engine.impl.cfg.ProcessEngineConfigurationImpl;
import mobius.engine.impl.delegate.ActivityBehavior;

/**
 * Default implementation of the {@link ActivityBehaviorFactory}. Used when no custom {@link ActivityBehaviorFactory} is injected on the {@link ProcessEngineConfigurationImpl}.
 *
 *
 */
public class DefaultActivityBehaviorFactory extends AbstractBehaviorFactory implements ActivityBehaviorFactory {
    private final ClassDelegateFactory classDelegateFactory;

    public DefaultActivityBehaviorFactory(ClassDelegateFactory classDelegateFactory) {
        this.classDelegateFactory = classDelegateFactory;
    }

    public DefaultActivityBehaviorFactory() {
        this(new DefaultClassDelegateFactory());
    }

    // Start event
    public static final String EXCEPTION_MAP_FIELD = "mapExceptions";

    @Override
    public NoneStartEventActivityBehavior createNoneStartEventActivityBehavior(StartEvent startEvent) {
        return new NoneStartEventActivityBehavior();
    }

    // Task

    @Override
    public TaskActivityBehavior createTaskActivityBehavior(Task task) {
        return new TaskActivityBehavior();
    }

    @Override
    public ManualTaskActivityBehavior createManualTaskActivityBehavior(ManualTask manualTask) {
        return new ManualTaskActivityBehavior();
    }

    @Override
    public ReceiveTaskActivityBehavior createReceiveTaskActivityBehavior(ReceiveTask receiveTask) {
        return new ReceiveTaskActivityBehavior();
    }

    @Override
    public UserTaskActivityBehavior createUserTaskActivityBehavior(UserTask userTask) {
        return new UserTaskActivityBehavior(userTask);
    }

    // Service task

    protected Expression getSkipExpressionFromServiceTask(ServiceTask serviceTask) {
        Expression result = null;
        if (StringUtils.isNotEmpty(serviceTask.getSkipExpression())) {
            result = expressionManager.createExpression(serviceTask.getSkipExpression());
        }
        return result;
    }

    @Override
    public ClassDelegate createClassDelegateServiceTask(ServiceTask serviceTask) {
        return classDelegateFactory.create(serviceTask.getId(), serviceTask.getImplementation(),
                createFieldDeclarations(serviceTask.getFieldExtensions()),
                serviceTask.isTriggerable(),
                getSkipExpressionFromServiceTask(serviceTask), serviceTask.getMapExceptions());
    }

    @Override
    public ServiceTaskDelegateExpressionActivityBehavior createServiceTaskDelegateExpressionActivityBehavior(ServiceTask serviceTask) {
        Expression delegateExpression = expressionManager.createExpression(serviceTask.getImplementation());
        return new ServiceTaskDelegateExpressionActivityBehavior(serviceTask.getId(), delegateExpression,
                getSkipExpressionFromServiceTask(serviceTask), createFieldDeclarations(serviceTask.getFieldExtensions()),
                serviceTask.getMapExceptions(), serviceTask.isTriggerable());
    }

    @Override
    public ServiceTaskExpressionActivityBehavior createServiceTaskExpressionActivityBehavior(ServiceTask serviceTask) {
        Expression expression = expressionManager.createExpression(serviceTask.getImplementation());
        return new ServiceTaskExpressionActivityBehavior(serviceTask, expression, getSkipExpressionFromServiceTask(serviceTask));
    }

    @Override
    public WebServiceActivityBehavior createWebServiceActivityBehavior(ServiceTask serviceTask, BpmnModel bpmnModel) {
        return new WebServiceActivityBehavior(bpmnModel);
    }

    @Override
    public WebServiceActivityBehavior createWebServiceActivityBehavior(SendTask sendTask, BpmnModel bpmnModel) {
        return new WebServiceActivityBehavior(bpmnModel);
    }

    @Override
    public MailActivityBehavior createMailActivityBehavior(ServiceTask serviceTask) {
        return createMailActivityBehavior(serviceTask.getId(), serviceTask.getFieldExtensions());
    }

    @Override
    public MailActivityBehavior createMailActivityBehavior(SendTask sendTask) {
        return createMailActivityBehavior(sendTask.getId(), sendTask.getFieldExtensions());
    }

    protected MailActivityBehavior createMailActivityBehavior(String taskId, List<FieldExtension> fields) {
        List<FieldDeclaration> fieldDeclarations = createFieldDeclarations(fields);
        return (MailActivityBehavior) ClassDelegate.defaultInstantiateDelegate(
                MailActivityBehavior.class, fieldDeclarations);
    }

    @Override
    public DmnActivityBehavior createDmnActivityBehavior(ServiceTask serviceTask) {
        return new DmnActivityBehavior(serviceTask);
    }

    @Override
    public DmnActivityBehavior createDmnActivityBehavior(SendTask sendTask) {
        return new DmnActivityBehavior(sendTask);
    }

    // We do not want a hard dependency on Mule, hence we return
    // ActivityBehavior and instantiate the delegate instance using a string instead of the Class itself.
    @Override
    public ActivityBehavior createMuleActivityBehavior(ServiceTask serviceTask) {
        return createMuleActivityBehavior(serviceTask, serviceTask.getFieldExtensions());
    }

    @Override
    public ActivityBehavior createMuleActivityBehavior(SendTask sendTask) {
        return createMuleActivityBehavior(sendTask, sendTask.getFieldExtensions());
    }

    protected ActivityBehavior createMuleActivityBehavior(TaskWithFieldExtensions task, List<FieldExtension> fieldExtensions) {
        try {

            Class<?> theClass = Class.forName("mobius.mule.MuleSendActivityBehavior");
            List<FieldDeclaration> fieldDeclarations = createFieldDeclarations(fieldExtensions);
            return (ActivityBehavior) ClassDelegate.defaultInstantiateDelegate(
                    theClass, fieldDeclarations);

        } catch (ClassNotFoundException e) {
            throw new FlowableException("Could not find mobius.mule.MuleSendActivityBehavior: ", e);
        }
    }

    // We do not want a hard dependency on Camel, hence we return
    // ActivityBehavior and instantiate the delegate instance using a string instead of the Class itself.
    @Override
    public ActivityBehavior createCamelActivityBehavior(ServiceTask serviceTask) {
        return createCamelActivityBehavior(serviceTask, serviceTask.getFieldExtensions());
    }

    @Override
    public ActivityBehavior createCamelActivityBehavior(SendTask sendTask) {
        return createCamelActivityBehavior(sendTask, sendTask.getFieldExtensions());
    }

    protected ActivityBehavior createCamelActivityBehavior(TaskWithFieldExtensions task, List<FieldExtension> fieldExtensions) {
        try {
            Class<?> theClass = null;
            FieldExtension behaviorExtension = null;
            for (FieldExtension fieldExtension : fieldExtensions) {
                if ("camelBehaviorClass".equals(fieldExtension.getFieldName()) && StringUtils.isNotEmpty(fieldExtension.getStringValue())) {
                    theClass = Class.forName(fieldExtension.getStringValue());
                    behaviorExtension = fieldExtension;
                    break;
                }
            }

            if (behaviorExtension != null) {
                fieldExtensions.remove(behaviorExtension);
            }

            if (theClass == null) {
                // Default Camel behavior class
                theClass = Class.forName(getDefaultCamelBehaviorClassName());
            }

            List<FieldDeclaration> fieldDeclarations = createFieldDeclarations(fieldExtensions);
            addExceptionMapAsFieldDeclaration(fieldDeclarations, task.getMapExceptions());
            return (ActivityBehavior) ClassDelegate.defaultInstantiateDelegate(
                    theClass, fieldDeclarations);

        } catch (ClassNotFoundException e) {
            throw new FlowableException("Could not find mobius.camel.CamelBehavior: ", e);
        }
    }

	protected String getDefaultCamelBehaviorClassName() {
		return "mobius.camel.impl.CamelBehaviorDefaultImpl";
	}

    private void addExceptionMapAsFieldDeclaration(List<FieldDeclaration> fieldDeclarations, List<MapExceptionEntry> mapExceptions) {
        FieldDeclaration exceptionMapsFieldDeclaration = new FieldDeclaration(EXCEPTION_MAP_FIELD, mapExceptions.getClass().toString(), mapExceptions);
        fieldDeclarations.add(exceptionMapsFieldDeclaration);

    }

    @Override
    public ShellActivityBehavior createShellActivityBehavior(ServiceTask serviceTask) {
        List<FieldDeclaration> fieldDeclarations = createFieldDeclarations(serviceTask.getFieldExtensions());
        return (ShellActivityBehavior) ClassDelegate.defaultInstantiateDelegate(
                ShellActivityBehavior.class, fieldDeclarations);
    }

    @Override
    public ActivityBehavior createHttpActivityBehavior(ServiceTask serviceTask) {
        try {
            Class<?> theClass = null;
            FieldExtension behaviorExtension = null;
            for (FieldExtension fieldExtension : serviceTask.getFieldExtensions()) {
                if ("httpActivityBehaviorClass".equals(fieldExtension.getFieldName()) && StringUtils.isNotEmpty(fieldExtension.getStringValue())) {
                    theClass = Class.forName(fieldExtension.getStringValue());
                    behaviorExtension = fieldExtension;
                    break;
                }
            }

            if (behaviorExtension != null) {
                serviceTask.getFieldExtensions().remove(behaviorExtension);
            }

            if (theClass == null) {
                // Default Http behavior class
                theClass = Class.forName("mobius.http.bpmn.impl.HttpActivityBehaviorImpl");
            }

            List<FieldDeclaration> fieldDeclarations = createFieldDeclarations(serviceTask.getFieldExtensions());
            addExceptionMapAsFieldDeclaration(fieldDeclarations, serviceTask.getMapExceptions());
            return (ActivityBehavior) ClassDelegate.defaultInstantiateDelegate(theClass, fieldDeclarations, serviceTask);

        } catch (ClassNotFoundException e) {
            throw new FlowableException("Could not find mobius.http.HttpActivityBehavior: ", e);
        }
    }

    @Override
    public ActivityBehavior createBusinessRuleTaskActivityBehavior(BusinessRuleTask businessRuleTask) {
        BusinessRuleTaskDelegate ruleActivity = null;
        if (StringUtils.isNotEmpty(businessRuleTask.getClassName())) {
            try {
                Class<?> clazz = Class.forName(businessRuleTask.getClassName());
                ruleActivity = (BusinessRuleTaskDelegate) clazz.newInstance();
            } catch (Exception e) {
                throw new FlowableException("Could not instantiate businessRuleTask (id:" + businessRuleTask.getId() + ") class: " +
                        businessRuleTask.getClassName(), e);
            }
        } else {
            ruleActivity = new BusinessRuleTaskActivityBehavior();
        }

        for (String ruleVariableInputObject : businessRuleTask.getInputVariables()) {
            ruleActivity.addRuleVariableInputIdExpression(expressionManager.createExpression(ruleVariableInputObject.trim()));
        }

        for (String rule : businessRuleTask.getRuleNames()) {
            ruleActivity.addRuleIdExpression(expressionManager.createExpression(rule.trim()));
        }

        ruleActivity.setExclude(businessRuleTask.isExclude());

        if (businessRuleTask.getResultVariableName() != null && businessRuleTask.getResultVariableName().length() > 0) {
            ruleActivity.setResultVariable(businessRuleTask.getResultVariableName());
        } else {
            ruleActivity.setResultVariable("mobius.engine.rules.OUTPUT");
        }

        return ruleActivity;
    }

    // Script task

    @Override
    public ScriptTaskActivityBehavior createScriptTaskActivityBehavior(ScriptTask scriptTask) {
        String language = scriptTask.getScriptFormat();
        if (language == null) {
            language = ScriptingEngines.DEFAULT_SCRIPTING_LANGUAGE;
        }
        return new ScriptTaskActivityBehavior(scriptTask.getId(), scriptTask.getScript(), language, scriptTask.getResultVariable(), scriptTask.isAutoStoreVariables());
    }

    // Gateways

    @Override
    public ExclusiveGatewayActivityBehavior createExclusiveGatewayActivityBehavior(ExclusiveGateway exclusiveGateway) {
        return new ExclusiveGatewayActivityBehavior();
    }

    @Override
    public ParallelGatewayActivityBehavior createParallelGatewayActivityBehavior(ParallelGateway parallelGateway) {
        return new ParallelGatewayActivityBehavior();
    }

    @Override
    public InclusiveGatewayActivityBehavior createInclusiveGatewayActivityBehavior(InclusiveGateway inclusiveGateway) {
        return new InclusiveGatewayActivityBehavior();
    }

    @Override
    public EventBasedGatewayActivityBehavior createEventBasedGatewayActivityBehavior(EventGateway eventGateway) {
        return new EventBasedGatewayActivityBehavior();
    }

    // Multi Instance

    @Override
    public SequentialMultiInstanceBehavior createSequentialMultiInstanceBehavior(Activity activity, AbstractBpmnActivityBehavior innerActivityBehavior) {
        return new SequentialMultiInstanceBehavior(activity, innerActivityBehavior);
    }

    @Override
    public ParallelMultiInstanceBehavior createParallelMultiInstanceBehavior(Activity activity, AbstractBpmnActivityBehavior innerActivityBehavior) {
        return new ParallelMultiInstanceBehavior(activity, innerActivityBehavior);
    }

    // Subprocess

    @Override
    public SubProcessActivityBehavior createSubprocessActivityBehavior(SubProcess subProcess) {
        return new SubProcessActivityBehavior();
    }

    @Override
    public EventSubProcessActivityBehavior createEventSubprocessActivityBehavior(EventSubProcess eventSubProcess) {
        return new EventSubProcessActivityBehavior();
    }
    
    @Override
    public EventSubProcessConditionalStartEventActivityBehavior createEventSubProcessConditionalStartEventActivityBehavior(StartEvent startEvent,
                    ConditionalEventDefinition conditionalEventDefinition, String conditionExpression) {
        
        return new EventSubProcessConditionalStartEventActivityBehavior(conditionalEventDefinition, conditionExpression);
    }

    @Override
    public EventSubProcessErrorStartEventActivityBehavior createEventSubProcessErrorStartEventActivityBehavior(StartEvent startEvent) {
        return new EventSubProcessErrorStartEventActivityBehavior();
    }
    
    @Override
    public EventSubProcessEscalationStartEventActivityBehavior createEventSubProcessEscalationStartEventActivityBehavior(StartEvent startEvent) {
        return new EventSubProcessEscalationStartEventActivityBehavior();
    }

    @Override
    public EventSubProcessMessageStartEventActivityBehavior createEventSubProcessMessageStartEventActivityBehavior(StartEvent startEvent, MessageEventDefinition messageEventDefinition) {
        return new EventSubProcessMessageStartEventActivityBehavior(messageEventDefinition);
    }

    @Override
    public EventSubProcessSignalStartEventActivityBehavior createEventSubProcessSignalStartEventActivityBehavior(StartEvent startEvent, SignalEventDefinition signalEventDefinition, Signal signal) {
        return new EventSubProcessSignalStartEventActivityBehavior(signalEventDefinition, signal);
    }

    @Override
    public EventSubProcessTimerStartEventActivityBehavior createEventSubProcessTimerStartEventActivityBehavior(StartEvent startEvent, TimerEventDefinition timerEventDefinition) {
        return new EventSubProcessTimerStartEventActivityBehavior(timerEventDefinition);
    }

    @Override
    public AdhocSubProcessActivityBehavior createAdhocSubprocessActivityBehavior(SubProcess subProcess) {
        return new AdhocSubProcessActivityBehavior();
    }

    // Call activity

    @Override
    public CallActivityBehavior createCallActivityBehavior(CallActivity callActivity) {
        String expressionRegex = "\\$+\\{+.+\\}";

        CallActivityBehavior callActivityBehaviour = null;
        if (StringUtils.isNotEmpty(callActivity.getCalledElement()) && callActivity.getCalledElement().matches(expressionRegex)) {
            callActivityBehaviour = new CallActivityBehavior(expressionManager.createExpression(callActivity.getCalledElement()),
                callActivity.getCalledElementType(), callActivity.getMapExceptions(),
                callActivity.getFallbackToDefaultTenant());
        } else {
            callActivityBehaviour = new CallActivityBehavior(callActivity.getCalledElement(), callActivity.getCalledElementType(),
                callActivity.getFallbackToDefaultTenant(),
                callActivity.getMapExceptions());
        }

        return callActivityBehaviour;
    }
    
    @Override
    public CaseTaskActivityBehavior createCaseTaskBehavior(CaseServiceTask caseServiceTask) {
        return new CaseTaskActivityBehavior();
    }

    // Transaction

    @Override
    public TransactionActivityBehavior createTransactionActivityBehavior(Transaction transaction) {
        return new TransactionActivityBehavior();
    }

    // Intermediate Events

    @Override
    public IntermediateCatchEventActivityBehavior createIntermediateCatchEventActivityBehavior(IntermediateCatchEvent intermediateCatchEvent) {
        return new IntermediateCatchEventActivityBehavior();
    }
    
    @Override
    public IntermediateCatchConditionalEventActivityBehavior createIntermediateCatchConditionalEventActivityBehavior(IntermediateCatchEvent intermediateCatchEvent, 
                    ConditionalEventDefinition conditionalEventDefinition, String conditionExpression) {
        
        return new IntermediateCatchConditionalEventActivityBehavior(conditionalEventDefinition, conditionExpression);
    }

    @Override
    public IntermediateCatchMessageEventActivityBehavior createIntermediateCatchMessageEventActivityBehavior(IntermediateCatchEvent intermediateCatchEvent, MessageEventDefinition messageEventDefinition) {
        return new IntermediateCatchMessageEventActivityBehavior(messageEventDefinition);
    }

    @Override
    public IntermediateCatchTimerEventActivityBehavior createIntermediateCatchTimerEventActivityBehavior(IntermediateCatchEvent intermediateCatchEvent, TimerEventDefinition timerEventDefinition) {
        return new IntermediateCatchTimerEventActivityBehavior(timerEventDefinition);
    }

    @Override
    public IntermediateCatchSignalEventActivityBehavior createIntermediateCatchSignalEventActivityBehavior(IntermediateCatchEvent intermediateCatchEvent, 
                    SignalEventDefinition signalEventDefinition, Signal signal) {

        return new IntermediateCatchSignalEventActivityBehavior(signalEventDefinition, signal);
    }
    
    @Override
    public IntermediateThrowNoneEventActivityBehavior createIntermediateThrowNoneEventActivityBehavior(ThrowEvent throwEvent) {
        return new IntermediateThrowNoneEventActivityBehavior();
    }

    @Override
    public IntermediateThrowSignalEventActivityBehavior createIntermediateThrowSignalEventActivityBehavior(ThrowEvent throwEvent, 
                    SignalEventDefinition signalEventDefinition, Signal signal) {
        
        return new IntermediateThrowSignalEventActivityBehavior(throwEvent, signalEventDefinition, signal);
    }
    
    @Override
    public IntermediateThrowEscalationEventActivityBehavior createIntermediateThrowEscalationEventActivityBehavior(ThrowEvent throwEvent, 
                    EscalationEventDefinition escalationEventDefinition, Escalation escalation) {
        
        return new IntermediateThrowEscalationEventActivityBehavior(throwEvent, escalationEventDefinition, escalation);
    }

    @Override
    public IntermediateThrowCompensationEventActivityBehavior createIntermediateThrowCompensationEventActivityBehavior(ThrowEvent throwEvent, CompensateEventDefinition compensateEventDefinition) {
        return new IntermediateThrowCompensationEventActivityBehavior(compensateEventDefinition);
    }

    // End events

    @Override
    public NoneEndEventActivityBehavior createNoneEndEventActivityBehavior(EndEvent endEvent) {
        return new NoneEndEventActivityBehavior();
    }

    @Override
    public ErrorEndEventActivityBehavior createErrorEndEventActivityBehavior(EndEvent endEvent, ErrorEventDefinition errorEventDefinition) {
        return new ErrorEndEventActivityBehavior(errorEventDefinition.getErrorCode());
    }
    
    @Override
    public EscalationEndEventActivityBehavior createEscalationEndEventActivityBehavior(EndEvent endEvent, EscalationEventDefinition escalationEventDefinition, Escalation escalation) {
        return new EscalationEndEventActivityBehavior(escalationEventDefinition, escalation);
    }

    @Override
    public CancelEndEventActivityBehavior createCancelEndEventActivityBehavior(EndEvent endEvent) {
        return new CancelEndEventActivityBehavior();
    }

    @Override
    public TerminateEndEventActivityBehavior createTerminateEndEventActivityBehavior(EndEvent endEvent) {
        boolean terminateAll = false;
        boolean terminateMultiInstance = false;

        if (endEvent.getEventDefinitions() != null
                && endEvent.getEventDefinitions().size() > 0
                && endEvent.getEventDefinitions().get(0) instanceof TerminateEventDefinition) {
            terminateAll = ((TerminateEventDefinition) endEvent.getEventDefinitions().get(0)).isTerminateAll();
            terminateMultiInstance = ((TerminateEventDefinition) endEvent.getEventDefinitions().get(0)).isTerminateMultiInstance();
        }

        TerminateEndEventActivityBehavior terminateEndEventActivityBehavior = new TerminateEndEventActivityBehavior();
        terminateEndEventActivityBehavior.setTerminateAll(terminateAll);
        terminateEndEventActivityBehavior.setTerminateMultiInstance(terminateMultiInstance);
        return terminateEndEventActivityBehavior;
    }

    // Boundary Events

    @Override
    public BoundaryEventActivityBehavior createBoundaryEventActivityBehavior(BoundaryEvent boundaryEvent, boolean interrupting) {
        return new BoundaryEventActivityBehavior(interrupting);
    }

    @Override
    public BoundaryCancelEventActivityBehavior createBoundaryCancelEventActivityBehavior(CancelEventDefinition cancelEventDefinition) {
        return new BoundaryCancelEventActivityBehavior();
    }

    @Override
    public BoundaryCompensateEventActivityBehavior createBoundaryCompensateEventActivityBehavior(BoundaryEvent boundaryEvent,
            CompensateEventDefinition compensateEventDefinition, boolean interrupting) {

        return new BoundaryCompensateEventActivityBehavior(compensateEventDefinition, interrupting);
    }
    
    @Override
    public BoundaryConditionalEventActivityBehavior createBoundaryConditionalEventActivityBehavior(BoundaryEvent boundaryEvent,
            ConditionalEventDefinition conditionalEventDefinition, String conditionExpression, boolean interrupting) {

        return new BoundaryConditionalEventActivityBehavior(conditionalEventDefinition, conditionExpression, interrupting);
    }

    @Override
    public BoundaryTimerEventActivityBehavior createBoundaryTimerEventActivityBehavior(BoundaryEvent boundaryEvent, TimerEventDefinition timerEventDefinition, boolean interrupting) {
        return new BoundaryTimerEventActivityBehavior(timerEventDefinition, interrupting);
    }

    @Override
    public BoundarySignalEventActivityBehavior createBoundarySignalEventActivityBehavior(BoundaryEvent boundaryEvent, SignalEventDefinition signalEventDefinition, Signal signal, boolean interrupting) {
        return new BoundarySignalEventActivityBehavior(signalEventDefinition, signal, interrupting);
    }

    @Override
    public BoundaryMessageEventActivityBehavior createBoundaryMessageEventActivityBehavior(BoundaryEvent boundaryEvent, MessageEventDefinition messageEventDefinition, boolean interrupting) {
        return new BoundaryMessageEventActivityBehavior(messageEventDefinition, interrupting);
    }
    
    @Override
    public BoundaryEscalationEventActivityBehavior createBoundaryEscalationEventActivityBehavior(BoundaryEvent boundaryEvent, EscalationEventDefinition escalationEventDefinition, Escalation escalation, boolean interrupting) {
        return new BoundaryEscalationEventActivityBehavior(escalationEventDefinition, escalation, interrupting);
    }
}
