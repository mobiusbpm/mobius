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
package mobius.engine.test.cmd;

import mobius.common.engine.api.FlowableException;
import mobius.engine.delegate.DelegateExecution;
import mobius.engine.delegate.JavaDelegate;

/**
 * @author Saeid Mirzaei
 */
public class FailingDelegate implements JavaDelegate {

    public static final String EXCEPTION_MESSAGE = "Expected exception.";

    @Override
    public void execute(DelegateExecution execution) {
        Boolean fail = (Boolean) execution.getVariable("fail");

        if (fail == null || fail) {
            throw new FlowableException(EXCEPTION_MESSAGE);
        }

    }
}
