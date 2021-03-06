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
package mobius.engine.impl.dynamic;

import mobius.bpmn.model.FlowElementsContainer;
import mobius.bpmn.model.Process;
import mobius.bpmn.model.SubProcess;
import mobius.bpmn.model.UserTask;

public interface DynamicUserTaskCallback {

    void handleCreatedDynamicUserTask(UserTask userTask, SubProcess newSubProcess, FlowElementsContainer parentContainer, Process process);

}