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
package mobius.task.service.impl;

import mobius.common.engine.api.delegate.event.FlowableEngineEventType;
import mobius.common.engine.api.delegate.event.FlowableEntityEvent;
import mobius.common.engine.api.delegate.event.FlowableEvent;
import mobius.common.engine.impl.event.FlowableEntityEventImpl;
import mobius.common.engine.impl.event.FlowableEngineEventImpl;
import mobius.task.api.Task;

/**
 * Builder class used to create {@link FlowableEvent} implementations.
 *
 * @author Frederik Heremans
 */
public class FlowableTaskEventBuilder {

    /**
     * @param type
     *            type of event
     * @param entity
     *            the entity this event targets
     * @return an {@link FlowableEntityEvent}.
     */
    public static FlowableEntityEvent createEntityEvent(FlowableEngineEventType type, Object entity) {
        FlowableEntityEventImpl newEvent = new FlowableEntityEventImpl(entity, type);
        populateEventWithCurrentContext(newEvent);
        return newEvent;
    }
    
    protected static void populateEventWithCurrentContext(FlowableEngineEventImpl event) {
        if (event instanceof FlowableEntityEvent) {
            Object persistedObject = ((FlowableEntityEvent) event).getEntity();
            if (persistedObject instanceof Task) {
                Task taskObject = (Task) persistedObject;
                event.setProcessInstanceId(taskObject.getProcessInstanceId());
                event.setExecutionId(taskObject.getExecutionId());
                event.setProcessDefinitionId(taskObject.getProcessDefinitionId());   
            }
        }
    }
}