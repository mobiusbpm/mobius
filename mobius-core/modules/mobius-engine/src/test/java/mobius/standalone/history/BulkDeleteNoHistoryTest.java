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
package mobius.standalone.history;

import java.util.HashMap;
import java.util.Map;

import mobius.engine.impl.test.ResourceFlowableTestCase;
import mobius.engine.runtime.ProcessInstance;
import mobius.engine.test.Deployment;
import mobius.task.api.Task;
import org.junit.jupiter.api.Test;

public class BulkDeleteNoHistoryTest extends ResourceFlowableTestCase {

    public BulkDeleteNoHistoryTest() {
        // History needs to be disabled to prevent any historic entities come in
        // between the variable deletes
        // to make sure a single batch is used for all entities
        super("mobius/standalone/history/nohistory.flowable.cfg.xml");
    }

    @Test
    @Deployment(resources = { "mobius/engine/test/api/oneTaskProcess.bpmn20.xml" })
    public void testLargeAmountOfVariableBulkDelete() throws Exception {
        Map<String, Object> variables = new HashMap<>();

        // Do a bulk-update with a number higher than any DB's magic numbers
        for (int i = 0; i < 4001; i++) {
            variables.put("var" + i, i);
        }

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("oneTaskProcess", variables);
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        assertNotNull(task);

        // Completing the task will cause a bulk delete of 4001 entities
        taskService.complete(task.getId());

        // Check if process is gone
        assertEquals(0L, runtimeService.createProcessInstanceQuery().processInstanceId(processInstance.getId()).count());
    }
}
