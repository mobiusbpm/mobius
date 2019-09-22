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
package mobius.engine.test.bpmn.usertask;

import java.util.List;

import mobius.engine.impl.test.PluggableFlowableTestCase;
import mobius.engine.test.Deployment;
import mobius.task.api.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test case for task candidate use case.
 * 
 * @author Tim Stephenson
 */
public class TaskAssignmentCandidateTest extends PluggableFlowableTestCase {

    @BeforeEach
    public void setUp() throws Exception {
        identityService.saveGroup(identityService.newGroup("accounting"));
        identityService.saveGroup(identityService.newGroup("management"));
    }

    @AfterEach
    public void tearDown() throws Exception {
        identityService.deleteGroup("accounting");
        identityService.deleteGroup("management");
    }

    @Test
    @Deployment
    public void testCandidateGroups() {
        runtimeService.startProcessInstanceByKey("taskCandidateExample");
        List<Task> tasks = taskService
                .createTaskQuery()
                .taskCandidateGroup("management")
                .list();
        assertEquals(1, tasks.size());
        assertEquals("theTask", tasks.get(0).getTaskDefinitionKey());
        taskService.complete(tasks.get(0).getId());

        tasks = taskService.createTaskQuery().taskCandidateGroup("accounting").list();
        assertEquals(1, tasks.size());
        assertEquals("theOtherTask", tasks.get(0).getTaskDefinitionKey());
        taskService.complete(tasks.get(0).getId());
    }

}
