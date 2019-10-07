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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mobius.common.engine.api.FlowableException;
import mobius.common.engine.api.FlowableObjectNotFoundException;
import mobius.common.engine.api.delegate.event.FlowableEngineEventType;
import mobius.common.engine.impl.interceptor.Command;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.engine.compatibility.Flowable5CompatibilityHandler;
import mobius.engine.delegate.event.impl.FlowableEventBuilder;
import mobius.engine.impl.persistence.entity.ExecutionEntity;
import mobius.engine.impl.util.CommandContextUtil;
import mobius.engine.impl.util.EventSubscriptionUtil;
import mobius.engine.impl.util.Flowable5Util;
import mobius.engine.runtime.Execution;
import mobius.eventsubscription.service.EventSubscriptionService;
import mobius.eventsubscription.service.impl.persistence.entity.SignalEventSubscriptionEntity;

/**
 *
 * @author Tijs Rademakers
 */
public class SignalEventReceivedCmd implements Command<Void> {

    protected final String eventName;
    protected final String executionId;
    protected final Map<String, Object> payload;
    protected final boolean async;
    protected String tenantId;

    public SignalEventReceivedCmd(String eventName, String executionId, Map<String, Object> processVariables, String tenantId) {
        this.eventName = eventName;
        this.executionId = executionId;
        if (processVariables != null) {
            this.payload = new HashMap<>(processVariables);

        } else {
            this.payload = null;
        }
        this.async = false;
        this.tenantId = tenantId;
    }

    public SignalEventReceivedCmd(String eventName, String executionId, boolean async, String tenantId) {
        this.eventName = eventName;
        this.executionId = executionId;
        this.async = async;
        this.payload = null;
        this.tenantId = tenantId;
    }

    @Override
    public Void execute(CommandContext commandContext) {

        List<SignalEventSubscriptionEntity> signalEvents = null;

        EventSubscriptionService eventSubscriptionService = CommandContextUtil.getEventSubscriptionService(commandContext);
        if (executionId == null) {
            signalEvents = eventSubscriptionService.findSignalEventSubscriptionsByEventName(eventName, tenantId);
        } else {

            ExecutionEntity execution = CommandContextUtil.getExecutionEntityManager(commandContext).findById(executionId);

            if (execution == null) {
                throw new FlowableObjectNotFoundException("Cannot find execution with id '" + executionId + "'", Execution.class);
            }

            if (execution.isSuspended()) {
                throw new FlowableException("Cannot throw signal event '" + eventName + "' because execution '" + executionId + "' is suspended");
            }

            if (Flowable5Util.isFlowable5ProcessDefinitionId(commandContext, execution.getProcessDefinitionId())) {
                Flowable5CompatibilityHandler compatibilityHandler = Flowable5Util.getFlowable5CompatibilityHandler();
                compatibilityHandler.signalEventReceived(eventName, executionId, payload, async, tenantId);
                return null;
            }

            signalEvents = eventSubscriptionService.findSignalEventSubscriptionsByNameAndExecution(eventName, executionId);

            if (signalEvents.isEmpty()) {
                throw new FlowableException("Execution '" + executionId + "' has not subscribed to a signal event with name '" + eventName + "'.");
            }
        }

        for (SignalEventSubscriptionEntity signalEventSubscriptionEntity : signalEvents) {
            // We only throw the event to globally scoped signals.
            // Process instance scoped signals must be thrown within the process itself
            if (signalEventSubscriptionEntity.isGlobalScoped()) {

                if (executionId == null && Flowable5Util.isFlowable5ProcessDefinitionId(commandContext, signalEventSubscriptionEntity.getProcessDefinitionId())) {
                    Flowable5CompatibilityHandler compatibilityHandler = Flowable5Util.getFlowable5CompatibilityHandler();
                    compatibilityHandler.signalEventReceived(signalEventSubscriptionEntity, payload, async);

                } else {
                    CommandContextUtil.getProcessEngineConfiguration().getEventDispatcher().dispatchEvent(
                            FlowableEventBuilder.createSignalEvent(FlowableEngineEventType.ACTIVITY_SIGNALED, signalEventSubscriptionEntity.getActivityId(), eventName,
                                    payload, signalEventSubscriptionEntity.getExecutionId(), signalEventSubscriptionEntity.getProcessInstanceId(),
                                    signalEventSubscriptionEntity.getProcessDefinitionId()));

                    EventSubscriptionUtil.eventReceived(signalEventSubscriptionEntity, payload, async);
                }
            }
        }

        return null;
    }

}
