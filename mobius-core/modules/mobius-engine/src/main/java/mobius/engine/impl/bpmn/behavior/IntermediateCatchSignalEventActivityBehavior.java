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

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import mobius.bpmn.model.Signal;
import mobius.bpmn.model.SignalEventDefinition;
import mobius.common.engine.api.delegate.Expression;
import mobius.common.engine.api.delegate.event.FlowableEngineEventType;
import mobius.common.engine.api.delegate.event.FlowableEventDispatcher;
import mobius.common.engine.impl.context.Context;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.engine.delegate.DelegateExecution;
import mobius.engine.delegate.event.impl.FlowableEventBuilder;
import mobius.engine.history.DeleteReason;
import mobius.engine.impl.persistence.entity.ExecutionEntity;
import mobius.engine.impl.util.CommandContextUtil;
import mobius.engine.impl.util.CountingEntityUtil;
import mobius.eventsubscription.service.EventSubscriptionService;
import mobius.eventsubscription.service.impl.persistence.entity.EventSubscriptionEntity;
import mobius.eventsubscription.service.impl.persistence.entity.SignalEventSubscriptionEntity;

public class IntermediateCatchSignalEventActivityBehavior extends IntermediateCatchEventActivityBehavior {

    private static final long serialVersionUID = 1L;

    protected SignalEventDefinition signalEventDefinition;
    protected Signal signal;

    public IntermediateCatchSignalEventActivityBehavior(SignalEventDefinition signalEventDefinition, Signal signal) {
        this.signalEventDefinition = signalEventDefinition;
        this.signal = signal;
    }

    @Override
    public void execute(DelegateExecution execution) {
        CommandContext commandContext = Context.getCommandContext();
        ExecutionEntity executionEntity = (ExecutionEntity) execution;

        String signalName = null;
        if (StringUtils.isNotEmpty(signalEventDefinition.getSignalRef())) {
            signalName = signalEventDefinition.getSignalRef();
        } else {
            Expression signalExpression = CommandContextUtil.getProcessEngineConfiguration(commandContext).getExpressionManager()
                    .createExpression(signalEventDefinition.getSignalExpression());
            signalName = signalExpression.getValue(execution).toString();
        }

        EventSubscriptionEntity eventSubscription = (EventSubscriptionEntity) CommandContextUtil.getEventSubscriptionService(commandContext).createEventSubscriptionBuilder()
                        .eventType(SignalEventSubscriptionEntity.EVENT_TYPE)
                        .eventName(signalName)
                        .signal(signal)
                        .executionId(executionEntity.getId())
                        .processInstanceId(executionEntity.getProcessInstanceId())
                        .activityId(executionEntity.getCurrentActivityId())
                        .processDefinitionId(executionEntity.getProcessDefinitionId())
                        .tenantId(executionEntity.getTenantId())
                        .create();
        
        CountingEntityUtil.handleInsertEventSubscriptionEntityCount(eventSubscription);
        executionEntity.getEventSubscriptions().add(eventSubscription);

        FlowableEventDispatcher eventDispatcher = CommandContextUtil.getProcessEngineConfiguration(commandContext).getEventDispatcher();
        if (eventDispatcher != null && eventDispatcher.isEnabled()) {
            eventDispatcher
                    .dispatchEvent(FlowableEventBuilder.createSignalEvent(FlowableEngineEventType.ACTIVITY_SIGNAL_WAITING, executionEntity.getActivityId(), signalName,
                            null, executionEntity.getId(), executionEntity.getProcessInstanceId(), executionEntity.getProcessDefinitionId()));
        }
    }

    @Override
    public void trigger(DelegateExecution execution, String triggerName, Object triggerData) {
        ExecutionEntity executionEntity = deleteSignalEventSubscription(execution);
        leaveIntermediateCatchEvent(executionEntity);
    }

    @Override
    public void eventCancelledByEventGateway(DelegateExecution execution) {
        deleteSignalEventSubscription(execution);
        CommandContextUtil.getExecutionEntityManager().deleteExecutionAndRelatedData((ExecutionEntity) execution,
                DeleteReason.EVENT_BASED_GATEWAY_CANCEL, false);
    }

    protected ExecutionEntity deleteSignalEventSubscription(DelegateExecution execution) {
        ExecutionEntity executionEntity = (ExecutionEntity) execution;

        String eventName = null;
        if (signal != null) {
            eventName = signal.getName();
        } else {
            eventName = signalEventDefinition.getSignalRef();
        }

        EventSubscriptionService eventSubscriptionService = CommandContextUtil.getEventSubscriptionService();
        List<EventSubscriptionEntity> eventSubscriptions = executionEntity.getEventSubscriptions();
        for (EventSubscriptionEntity eventSubscription : eventSubscriptions) {
            if (eventSubscription instanceof SignalEventSubscriptionEntity && eventSubscription.getEventName().equals(eventName)) {

                eventSubscriptionService.deleteEventSubscription(eventSubscription);
                CountingEntityUtil.handleDeleteEventSubscriptionEntityCount(eventSubscription);
            }
        }
        return executionEntity;
    }
}
