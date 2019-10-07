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

import java.util.Map;

import mobius.bpmn.model.FlowNode;
import mobius.common.engine.api.FlowableException;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.engine.impl.persistence.entity.ExecutionEntity;
import mobius.engine.impl.util.CommandContextUtil;
import mobius.eventsubscription.service.impl.persistence.entity.EventSubscriptionEntity;

/**
 *
 */
public abstract class AbstractEventHandler implements EventHandler {

    @Override
    public void handleEvent(EventSubscriptionEntity eventSubscription, Object payload, CommandContext commandContext) {
        String executionId = eventSubscription.getExecutionId();
        ExecutionEntity execution = CommandContextUtil.getExecutionEntityManager(commandContext).findById(executionId);
        FlowNode currentFlowElement = (FlowNode) execution.getCurrentFlowElement();

        if (currentFlowElement == null) {
            throw new FlowableException("Error while sending signal for event subscription '" + eventSubscription.getId() + "': " + "no activity associated with event subscription");
        }

        if (payload instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> processVariables = (Map<String, Object>) payload;
            execution.setVariables(processVariables);
        }

        CommandContextUtil.getAgenda().planTriggerExecutionOperation(execution);
    }

}
