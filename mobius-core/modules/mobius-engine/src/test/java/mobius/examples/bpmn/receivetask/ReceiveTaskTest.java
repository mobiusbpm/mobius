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

package mobius.examples.bpmn.receivetask;

import mobius.engine.impl.test.PluggableFlowableTestCase;
import mobius.engine.runtime.Execution;
import mobius.engine.runtime.ProcessInstance;
import mobius.engine.test.Deployment;
import org.junit.jupiter.api.Test;

/**
 * @author Joram Barrez
 */
public class ReceiveTaskTest extends PluggableFlowableTestCase {

    @Test
    @Deployment
    public void testWaitStateBehavior() {
        ProcessInstance pi = runtimeService.startProcessInstanceByKey("receiveTask");
        Execution execution = runtimeService.createExecutionQuery().processInstanceId(pi.getId()).activityId("waitState").singleResult();
        assertNotNull(execution);

        runtimeService.trigger(execution.getId());
        assertProcessEnded(pi.getId());
    }

}