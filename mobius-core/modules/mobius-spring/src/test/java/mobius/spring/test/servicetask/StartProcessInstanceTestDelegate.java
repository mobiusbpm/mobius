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
package mobius.spring.test.servicetask;

import mobius.engine.RuntimeService;
import mobius.engine.delegate.DelegateExecution;
import mobius.engine.delegate.JavaDelegate;
import mobius.engine.impl.context.Context;

/**
 * @author Joram Barrez
 */
public class StartProcessInstanceTestDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        RuntimeService runtimeService = Context.getProcessEngineConfiguration().getRuntimeService();
        runtimeService.startProcessInstanceByKey("oneTaskProcess");
    }

}
