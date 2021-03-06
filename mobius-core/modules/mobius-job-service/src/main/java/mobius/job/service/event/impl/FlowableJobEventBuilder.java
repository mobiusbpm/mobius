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
package mobius.job.service.event.impl;

import mobius.common.engine.api.delegate.event.FlowableEngineEventType;
import mobius.common.engine.api.delegate.event.FlowableEntityEvent;
import mobius.common.engine.api.delegate.event.FlowableEvent;
import mobius.common.engine.api.delegate.event.FlowableExceptionEvent;
import mobius.common.engine.impl.event.FlowableEntityEventImpl;
import mobius.common.engine.impl.event.FlowableEntityExceptionEventImpl;
import mobius.common.engine.impl.event.FlowableEngineEventImpl;
import mobius.job.api.Job;

/**
 * Builder class used to create {@link FlowableEvent} implementations.
 *
 *
 */
public class FlowableJobEventBuilder {

    /**
     * @param type
     *            type of event
     * @param entity
     *            the entity this event targets
     * @return an {@link FlowableEntityEvent}.
     */
    public static FlowableEntityEvent createEntityEvent(FlowableEngineEventType type, Object entity) {
        FlowableEntityEventImpl newEvent = new FlowableEntityEventImpl(entity, type);
        
        // In case an execution-context is active, populate the event fields related to the execution
        populateEventWithCurrentContext(newEvent);
        return newEvent;
    }
    
    /**
     * @param type
     *            type of event
     * @param entity
     *            the entity this event targets
     * @param cause
     *            the cause of the event
     * @return an {@link FlowableEntityEvent} that is also instance of {@link FlowableExceptionEvent}. In case an ExecutionContext is active, the execution related event fields will be
     *         populated.
     */
    public static FlowableEntityEvent createEntityExceptionEvent(FlowableEngineEventType type, Object entity, Throwable cause) {
        FlowableEntityExceptionEventImpl newEvent = new FlowableEntityExceptionEventImpl(entity, type, cause);

        // In case an execution-context is active, populate the event fields related to the execution
        populateEventWithCurrentContext(newEvent);
        return newEvent;
    }
    
    protected static void populateEventWithCurrentContext(FlowableEngineEventImpl event) {
        if (event instanceof FlowableEntityEvent) {
            Object persistedObject = ((FlowableEntityEvent) event).getEntity();
            if (persistedObject instanceof Job) {
                event.setExecutionId(((Job) persistedObject).getExecutionId());
                event.setProcessInstanceId(((Job) persistedObject).getProcessInstanceId());
                event.setProcessDefinitionId(((Job) persistedObject).getProcessDefinitionId());   
            }
        }
    }
}
