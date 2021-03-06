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
package mobius.spring.test.jobexecutor;

import java.util.List;

import mobius.common.engine.impl.test.CleanTest;
import mobius.engine.HistoryService;
import mobius.engine.ManagementService;
import mobius.engine.ProcessEngineConfiguration;
import mobius.engine.RuntimeService;
import mobius.engine.TaskService;
import mobius.engine.impl.test.HistoryTestHelper;
import mobius.engine.runtime.ProcessInstance;
import mobius.spring.impl.test.SpringFlowableTestCase;
import mobius.task.api.Task;
import mobius.task.api.history.HistoricTaskInstance;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

@CleanTest
// We need to use per class as the test uses auto deployments. If they are deleted then the other tests will fail
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration("classpath:mobius/spring/test/components/SpringAsyncHistoryJobExecutorTest-context.xml")
public class SpringAsyncHistoryExecutorTest extends SpringFlowableTestCase {

    @Autowired
    protected ManagementService managementService;

    @Autowired
    protected RuntimeService runtimeService;

    @Autowired
    protected TaskService taskService;
    
    @Autowired
    protected HistoryService historyService;

    @Autowired
    protected ProcessEngineConfiguration processEngineConfiguration;

    @Test
    public void testHistoryDataGenerated() throws Exception {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("testProcess");
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list();
        while (!tasks.isEmpty()) {
            taskService.complete(tasks.get(0).getId());
            tasks = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list();
        }
        
        HistoryTestHelper.waitForJobExecutorToProcessAllHistoryJobs(processEngineConfiguration, managementService, 20000L, 200L, false);
        
        List<HistoricTaskInstance> historicTasks = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(processInstance.getId()).orderByTaskName().asc().list();
        
        String[] expectedTaskNames = new String[] { "Task a", "Task b1", "Task b2", "Task c" };
        assertEquals(expectedTaskNames.length, historicTasks.size());
        for (int i = 0; i < historicTasks.size(); i++) {
            assertEquals(expectedTaskNames[i], historicTasks.get(i).getName());
        }
    }
        
}
