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

package mobius.engine.test.bpmn.event.timer;

import mobius.engine.impl.test.ResourceFlowableTestCase;
import mobius.engine.runtime.ProcessInstance;
import mobius.engine.test.Deployment;
import org.junit.jupiter.api.Test;

/**
 *
 */
public class BoundaryTimerEventFullHistoryTest extends ResourceFlowableTestCase {

    public BoundaryTimerEventFullHistoryTest() {
        super("mobius/standalone/history/fullhistory.flowable.cfg.xml");
    }

    @Test
    @Deployment
    public void testSetProcessVariablesFromTaskWhenTimerOnTask() {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("timerVariablesProcess");
        runtimeService.setVariable(processInstance.getId(), "myVar", 123456L);
    }

}
