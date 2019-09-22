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

package mobius.engine.impl.event;

import mobius.common.engine.api.delegate.event.FlowableEngineEventType;
import mobius.common.engine.api.delegate.event.FlowableEventDispatcher;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.engine.delegate.event.impl.FlowableEventBuilder;
import mobius.engine.impl.cfg.ProcessEngineConfigurationImpl;
import mobius.engine.impl.persistence.entity.ExecutionEntity;
import mobius.engine.impl.util.CommandContextUtil;
import mobius.eventsubscription.service.impl.persistence.entity.EventSubscriptionEntity;

/**
 * @author Daniel Meyer
 */
public class MessageEventHandler extends AbstractEventHandler {

    public static final String EVENT_HANDLER_TYPE = "message";

    @Override
    public String getEventHandlerType() {
        return EVENT_HANDLER_TYPE;
    }

    @Override
    public void handleEvent(EventSubscriptionEntity eventSubscription, Object payload, CommandContext commandContext) {
        // As stated in the FlowableEventType java-doc, the message-event is
        // thrown before the actual message has been sent
        ProcessEngineConfigurationImpl processEngineConfiguration = CommandContextUtil.getProcessEngineConfiguration(commandContext);
        FlowableEventDispatcher eventDispatcher = processEngineConfiguration.getEventDispatcher();
        if (eventDispatcher != null && eventDispatcher.isEnabled()) {
            String executionId = eventSubscription.getExecutionId();
            ExecutionEntity execution = CommandContextUtil.getExecutionEntityManager(commandContext).findById(executionId);
            eventDispatcher.dispatchEvent(
                            FlowableEventBuilder.createMessageEvent(FlowableEngineEventType.ACTIVITY_MESSAGE_RECEIVED, eventSubscription.getActivityId(), eventSubscription.getEventName(), payload,
                                    eventSubscription.getExecutionId(), eventSubscription.getProcessInstanceId(), execution.getProcessDefinitionId()));
        }

        super.handleEvent(eventSubscription, payload, commandContext);
    }

}
