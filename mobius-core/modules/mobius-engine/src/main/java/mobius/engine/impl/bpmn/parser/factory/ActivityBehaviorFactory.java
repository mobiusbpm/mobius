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
import mobius.bpmn.model.InclusiveGateway;
import mobius.bpmn.model.IntermediateCatchEvent;
import mobius.bpmn.model.ManualTask;
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
import mobius.bpmn.model.ThrowEvent;
import mobius.bpmn.model.TimerEventDefinition;
import mobius.bpmn.model.Transaction;
import mobius.bpmn.model.UserTask;
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
import mobius.engine.impl.bpmn.behavior.CallActivityBehavior;
import mobius.engine.impl.bpmn.behavior.CancelEndEventActivityBehavior;
import mobius.engine.impl.bpmn.behavior.CaseTaskActivityBehavior;
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
import mobius.engine.impl.bpmn.parser.BpmnParse;
import mobius.engine.impl.bpmn.parser.BpmnParser;
import mobius.engine.impl.cfg.ProcessEngineConfigurationImpl;
import mobius.engine.impl.delegate.ActivityBehavior;

/**
 * Factory class used by the {@link BpmnParser} and {@link BpmnParse} to instantiate the behaviour classes. For example when parsing an exclusive gateway, this factory will be requested to create a
 * new {@link ActivityBehavior} that will be set on the {@link ActivityImpl} of that step of the process and will implement the spec-compliant behavior of the exclusive gateway.
 * 
 * You can provide your own implementation of this class. This way, you can give different execution semantics to a standard bpmn xml construct. Eg. you could tweak the exclusive gateway to do
 * something completely different if you would want that. Creating your own {@link ActivityBehaviorFactory} is only advisable if you want to change the default behavior of any BPMN default construct.
 * And even then, think twice, because it won't be spec compliant bpmn anymore.
 * 
 * Note that you can always express any custom step as a service task with a class delegation.
 * 
 * The easiest and advisable way to implement your own {@link ActivityBehaviorFactory} is to extend the {@link DefaultActivityBehaviorFactory} class and override the method specific to the
 * {@link ActivityBehavior} you want to change.
 * 
 * An instance of this interface can be injected in the {@link ProcessEngineConfigurationImpl} and its subclasses.
 * 
 *
 */
public interface ActivityBehaviorFactory {

    public abstract NoneStartEventActivityBehavior createNoneStartEventActivityBehavior(StartEvent startEvent);

    public abstract TaskActivityBehavior createTaskActivityBehavior(Task task);

    public abstract ManualTaskActivityBehavior createManualTaskActivityBehavior(ManualTask manualTask);

    public abstract ReceiveTaskActivityBehavior createReceiveTaskActivityBehavior(ReceiveTask receiveTask);

    public abstract UserTaskActivityBehavior createUserTaskActivityBehavior(UserTask userTask);

    public abstract ClassDelegate createClassDelegateServiceTask(ServiceTask serviceTask);

    public abstract ServiceTaskDelegateExpressionActivityBehavior createServiceTaskDelegateExpressionActivityBehavior(ServiceTask serviceTask);

    public abstract ServiceTaskExpressionActivityBehavior createServiceTaskExpressionActivityBehavior(ServiceTask serviceTask);

    public abstract WebServiceActivityBehavior createWebServiceActivityBehavior(ServiceTask serviceTask, BpmnModel bpmnModel);

    public abstract WebServiceActivityBehavior createWebServiceActivityBehavior(SendTask sendTask, BpmnModel bpmnModel);

    public abstract MailActivityBehavior createMailActivityBehavior(ServiceTask serviceTask);

    public abstract MailActivityBehavior createMailActivityBehavior(SendTask sendTask);

    // We do not want a hard dependency on the Mule module, hence we return
    // ActivityBehavior and instantiate the delegate instance using a string instead of the Class itself.
    public abstract ActivityBehavior createMuleActivityBehavior(ServiceTask serviceTask);

    public abstract ActivityBehavior createMuleActivityBehavior(SendTask sendTask);

    public abstract ActivityBehavior createCamelActivityBehavior(ServiceTask serviceTask);

    public abstract ActivityBehavior createCamelActivityBehavior(SendTask sendTask);

    public abstract ActivityBehavior createDmnActivityBehavior(ServiceTask serviceTask);

    public abstract ActivityBehavior createDmnActivityBehavior(SendTask sendTask);

    public abstract ActivityBehavior createHttpActivityBehavior(ServiceTask serviceTask);

    public abstract ShellActivityBehavior createShellActivityBehavior(ServiceTask serviceTask);

    public abstract ActivityBehavior createBusinessRuleTaskActivityBehavior(BusinessRuleTask businessRuleTask);

    public abstract ScriptTaskActivityBehavior createScriptTaskActivityBehavior(ScriptTask scriptTask);

    public abstract ExclusiveGatewayActivityBehavior createExclusiveGatewayActivityBehavior(ExclusiveGateway exclusiveGateway);

    public abstract ParallelGatewayActivityBehavior createParallelGatewayActivityBehavior(ParallelGateway parallelGateway);

    public abstract InclusiveGatewayActivityBehavior createInclusiveGatewayActivityBehavior(InclusiveGateway inclusiveGateway);

    public abstract EventBasedGatewayActivityBehavior createEventBasedGatewayActivityBehavior(EventGateway eventGateway);

    public abstract SequentialMultiInstanceBehavior createSequentialMultiInstanceBehavior(Activity activity, AbstractBpmnActivityBehavior innerActivityBehavior);

    public abstract ParallelMultiInstanceBehavior createParallelMultiInstanceBehavior(Activity activity, AbstractBpmnActivityBehavior innerActivityBehavior);

    public abstract SubProcessActivityBehavior createSubprocessActivityBehavior(SubProcess subProcess);

    public abstract EventSubProcessActivityBehavior createEventSubprocessActivityBehavior(EventSubProcess eventSubProcess); 
    
    public abstract EventSubProcessConditionalStartEventActivityBehavior createEventSubProcessConditionalStartEventActivityBehavior(StartEvent startEvent,
                    ConditionalEventDefinition conditionalEventDefinition, String conditionExpression);

    public abstract EventSubProcessErrorStartEventActivityBehavior createEventSubProcessErrorStartEventActivityBehavior(StartEvent startEvent);
    
    public abstract EventSubProcessEscalationStartEventActivityBehavior createEventSubProcessEscalationStartEventActivityBehavior(StartEvent startEvent);

    public abstract EventSubProcessMessageStartEventActivityBehavior createEventSubProcessMessageStartEventActivityBehavior(StartEvent startEvent, MessageEventDefinition messageEventDefinition);

    public abstract EventSubProcessSignalStartEventActivityBehavior createEventSubProcessSignalStartEventActivityBehavior(StartEvent startEvent, SignalEventDefinition signalEventDefinition, Signal signal);

    public abstract EventSubProcessTimerStartEventActivityBehavior createEventSubProcessTimerStartEventActivityBehavior(StartEvent startEvent, TimerEventDefinition timerEventDefinition);

    public abstract AdhocSubProcessActivityBehavior createAdhocSubprocessActivityBehavior(SubProcess subProcess);

    public abstract CallActivityBehavior createCallActivityBehavior(CallActivity callActivity);
    
    public abstract CaseTaskActivityBehavior createCaseTaskBehavior(CaseServiceTask caseServiceTask);

    public abstract TransactionActivityBehavior createTransactionActivityBehavior(Transaction transaction);

    public abstract IntermediateCatchEventActivityBehavior createIntermediateCatchEventActivityBehavior(IntermediateCatchEvent intermediateCatchEvent);

    public abstract IntermediateCatchMessageEventActivityBehavior createIntermediateCatchMessageEventActivityBehavior(IntermediateCatchEvent intermediateCatchEvent,
            MessageEventDefinition messageEventDefinition);
    
    public abstract IntermediateCatchConditionalEventActivityBehavior createIntermediateCatchConditionalEventActivityBehavior(IntermediateCatchEvent intermediateCatchEvent,
                    ConditionalEventDefinition conditionalEventDefinition, String conditionExpression);

    public abstract IntermediateCatchTimerEventActivityBehavior createIntermediateCatchTimerEventActivityBehavior(IntermediateCatchEvent intermediateCatchEvent, TimerEventDefinition timerEventDefinition);

    public abstract IntermediateCatchSignalEventActivityBehavior createIntermediateCatchSignalEventActivityBehavior(IntermediateCatchEvent intermediateCatchEvent,
            SignalEventDefinition signalEventDefinition, Signal signal);
    
    public abstract IntermediateThrowNoneEventActivityBehavior createIntermediateThrowNoneEventActivityBehavior(ThrowEvent throwEvent);

    public abstract IntermediateThrowSignalEventActivityBehavior createIntermediateThrowSignalEventActivityBehavior(ThrowEvent throwEvent, SignalEventDefinition signalEventDefinition, Signal signal);
    
    public abstract IntermediateThrowEscalationEventActivityBehavior createIntermediateThrowEscalationEventActivityBehavior(ThrowEvent throwEvent, EscalationEventDefinition escalationEventDefinition, Escalation escalation);

    public abstract IntermediateThrowCompensationEventActivityBehavior createIntermediateThrowCompensationEventActivityBehavior(ThrowEvent throwEvent, CompensateEventDefinition compensateEventDefinition);

    public abstract NoneEndEventActivityBehavior createNoneEndEventActivityBehavior(EndEvent endEvent);

    public abstract ErrorEndEventActivityBehavior createErrorEndEventActivityBehavior(EndEvent endEvent, ErrorEventDefinition errorEventDefinition);
    
    public abstract EscalationEndEventActivityBehavior createEscalationEndEventActivityBehavior(EndEvent endEvent, EscalationEventDefinition escalationEventDefinition, Escalation escalation);

    public abstract CancelEndEventActivityBehavior createCancelEndEventActivityBehavior(EndEvent endEvent);

    public abstract TerminateEndEventActivityBehavior createTerminateEndEventActivityBehavior(EndEvent endEvent);

    public abstract BoundaryEventActivityBehavior createBoundaryEventActivityBehavior(BoundaryEvent boundaryEvent, boolean interrupting);

    public abstract BoundaryCancelEventActivityBehavior createBoundaryCancelEventActivityBehavior(CancelEventDefinition cancelEventDefinition);

    public abstract BoundaryTimerEventActivityBehavior createBoundaryTimerEventActivityBehavior(BoundaryEvent boundaryEvent, TimerEventDefinition timerEventDefinition, boolean interrupting);

    public abstract BoundarySignalEventActivityBehavior createBoundarySignalEventActivityBehavior(BoundaryEvent boundaryEvent, SignalEventDefinition signalEventDefinition, Signal signal, boolean interrupting);

    public abstract BoundaryMessageEventActivityBehavior createBoundaryMessageEventActivityBehavior(BoundaryEvent boundaryEvent, MessageEventDefinition messageEventDefinition, boolean interrupting);
    
    public abstract BoundaryConditionalEventActivityBehavior createBoundaryConditionalEventActivityBehavior(BoundaryEvent boundaryEvent, ConditionalEventDefinition conditionalEventDefinition, 
                    String conditionExpression, boolean interrupting);
    
    public abstract BoundaryEscalationEventActivityBehavior createBoundaryEscalationEventActivityBehavior(BoundaryEvent boundaryEvent, EscalationEventDefinition escalationEventDefinition, Escalation escalation, boolean interrupting);

    public abstract BoundaryCompensateEventActivityBehavior createBoundaryCompensateEventActivityBehavior(BoundaryEvent boundaryEvent, CompensateEventDefinition compensateEventDefinition, boolean interrupting);
}