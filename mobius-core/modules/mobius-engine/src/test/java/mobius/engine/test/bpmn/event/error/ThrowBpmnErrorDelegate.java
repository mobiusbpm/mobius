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

package mobius.engine.test.bpmn.event.error;

import mobius.engine.delegate.BpmnError;
import mobius.engine.delegate.DelegateExecution;
import mobius.engine.delegate.JavaDelegate;

/**
 * @author Falko Menge
 */
public class ThrowBpmnErrorDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        Integer executionsBeforeError = (Integer) execution.getVariable("executionsBeforeError");
        Integer executions = (Integer) execution.getVariable("executions");
        if (executions == null) {
            executions = 0;
        }
        executions++;
        if (executionsBeforeError == null || executionsBeforeError < executions) {
            throw new BpmnError("23", "This is a business fault, which can be caught by a BPMN Error Event.");
        } else {
            execution.setVariable("executions", executions);
        }
    }

}
