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
package mobius.variable.service.impl;

import mobius.common.engine.api.delegate.event.FlowableEngineEventType;
import mobius.common.engine.api.delegate.event.FlowableEntityEvent;
import mobius.common.engine.api.delegate.event.FlowableEvent;
import mobius.common.engine.impl.event.FlowableEntityEventImpl;
import mobius.variable.api.event.FlowableVariableEvent;
import mobius.variable.api.types.VariableType;

/**
 * Builder class used to create {@link FlowableEvent} implementations.
 *
 *
 */
public class FlowableVariableEventBuilder {

    /**
     * @param type
     *            type of event
     * @param entity
     *            the entity this event targets
     * @return an {@link FlowableEntityEvent}. In case an ExecutionContext is active, the execution related event fields will be populated. If not, execution details will be retrieved from the
     *         {@link Object} if possible.
     */
    public static FlowableEntityEvent createEntityEvent(FlowableEngineEventType type, Object entity) {
        FlowableEntityEventImpl newEvent = new FlowableEntityEventImpl(entity, type);

        return newEvent;
    }

    public static FlowableVariableEvent createVariableEvent(FlowableEngineEventType type, String variableName, Object variableValue, VariableType variableType, String taskId, String executionId,
            String processInstanceId, String processDefinitionId, String scopeId, String scopeType) {

        FlowableVariableEventImpl newEvent = new FlowableVariableEventImpl(type);
        newEvent.setVariableName(variableName);
        newEvent.setVariableValue(variableValue);
        newEvent.setVariableType(variableType);
        newEvent.setTaskId(taskId);
        newEvent.setExecutionId(executionId);
        newEvent.setProcessDefinitionId(processDefinitionId);
        newEvent.setProcessInstanceId(processInstanceId);
        newEvent.setScopeId(scopeId);
        newEvent.setScopeType(scopeType);
        return newEvent;
    }
}
