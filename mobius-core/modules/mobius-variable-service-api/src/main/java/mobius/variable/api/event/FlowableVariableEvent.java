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
package mobius.variable.api.event;

import mobius.common.engine.api.delegate.event.FlowableEngineEvent;
import mobius.common.engine.api.delegate.event.FlowableEvent;
import mobius.variable.api.types.VariableType;

/**
 * An {@link FlowableEvent} related to a single variable.
 *
 *
 *
 */
public interface FlowableVariableEvent extends FlowableEngineEvent {

    /**
     * @return the name of the variable involved.
     */
    String getVariableName();

    /**
     * @return the current value of the variable.
     */
    Object getVariableValue();

    /**
     * @return The {@link VariableType} of the variable.
     */
    VariableType getVariableType();

    /**
     * @return the id of the task the variable has been set on.
     */
    String getTaskId();

    /**
     * @return the id of the scope the variable has been set on.
     */
    String getScopeId();

    /**
     * @return the type of the scope the variable has been set on.
     */
    String getScopeType();
}
