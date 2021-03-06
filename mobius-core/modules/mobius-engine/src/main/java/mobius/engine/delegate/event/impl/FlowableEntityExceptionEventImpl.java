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
package mobius.engine.delegate.event.impl;

import mobius.common.engine.api.FlowableIllegalArgumentException;
import mobius.common.engine.api.delegate.event.FlowableEngineEntityEvent;
import mobius.common.engine.api.delegate.event.FlowableEngineEventType;
import mobius.common.engine.api.delegate.event.FlowableEvent;
import mobius.common.engine.api.delegate.event.FlowableExceptionEvent;

/**
 * Base class for all {@link FlowableEvent} implementations, represents an exception occurred, related to an entity.
 *
 *
 */
public class FlowableEntityExceptionEventImpl extends FlowableProcessEventImpl implements FlowableEngineEntityEvent, FlowableExceptionEvent {

    protected Object entity;
    protected Throwable cause;

    public FlowableEntityExceptionEventImpl(Object entity, FlowableEngineEventType type, Throwable cause) {
        super(type);
        if (entity == null) {
            throw new FlowableIllegalArgumentException("Entity cannot be null.");
        }
        this.entity = entity;
        this.cause = cause;
    }

    @Override
    public Object getEntity() {
        return entity;
    }

    @Override
    public Throwable getCause() {
        return cause;
    }
}
