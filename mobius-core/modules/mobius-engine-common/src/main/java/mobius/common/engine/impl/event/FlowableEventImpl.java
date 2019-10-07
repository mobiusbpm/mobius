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
package mobius.common.engine.impl.event;

import mobius.common.engine.api.FlowableIllegalArgumentException;
import mobius.common.engine.api.delegate.event.FlowableEvent;
import mobius.common.engine.api.delegate.event.FlowableEventType;

/**
 * Base class for all {@link FlowableEvent} implementations.
 *
 *
 */
public class FlowableEventImpl implements FlowableEvent {

    protected FlowableEventType type;

    public FlowableEventImpl(FlowableEventType type) {
        if (type == null) {
            throw new FlowableIllegalArgumentException("type is null");
        }

        this.type = type;
    }

    @Override
    public FlowableEventType getType() {
        return type;
    }

    @Override
    public String toString() {
        return getClass() + " - " + type;
    }
}
