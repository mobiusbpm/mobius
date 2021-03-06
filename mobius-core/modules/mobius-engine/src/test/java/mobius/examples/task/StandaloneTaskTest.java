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
package mobius.examples.task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mobius.common.engine.api.FlowableOptimisticLockingException;
import mobius.common.engine.impl.history.HistoryLevel;
import mobius.engine.impl.test.HistoryTestHelper;
import mobius.engine.impl.test.PluggableFlowableTestCase;
import mobius.engine.impl.util.CommandContextUtil;
import mobius.task.api.Task;
import mobius.task.service.impl.persistence.entity.TaskEntity;
import mobius.variable.api.history.HistoricVariableInstance;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 */
public class StandaloneTaskTest extends PluggableFlowableTestCase {

    @BeforeEach
    public void setUp() throws Exception {
        identityService.saveUser(identityService.newUser("kermit"));
        identityService.saveUser(identityService.newUser("gonzo"));
    }

    @AfterEach
    public void tearDown() throws Exception {
        identityService.deleteUser("kermit");
        identityService.deleteUser("gonzo");
    }

    @Test
    public void testCreateToComplete() {

        // Create and save task
        Task task = taskService.newTask();
        task.setName("testTask");
        taskService.saveTask(task);
        String taskId = task.getId();

        // Add user as candidate user
        taskService.addCandidateUser(taskId, "kermit");
        taskService.addCandidateUser(taskId, "gonzo");

        // Retrieve task list for kermit
        List<Task> tasks = taskService.createTaskQuery().taskCandidateUser("kermit").list();
        assertEquals(1, tasks.size());
        assertEquals("testTask", tasks.get(0).getName());

        // Retrieve task list for gonzo
        tasks = taskService.createTaskQuery().taskCandidateUser("gonzo").list();
        assertEquals(1, tasks.size());

        task = tasks.get(0);
        assertEquals("testTask", task.getName());

        task.setName("Update name");
        taskService.saveTask(task);
        tasks = taskService.createTaskQuery().taskCandidateUser("kermit").list();
        assertEquals(1, tasks.size());
        assertEquals("Update name", tasks.get(0).getName());

        // Claim task
        taskService.claim(taskId, "kermit");

        // Tasks shouldn't appear in the candidate tasklists anymore
        assertTrue(taskService.createTaskQuery().taskCandidateUser("kermit").list().isEmpty());
        assertTrue(taskService.createTaskQuery().taskCandidateUser("gonzo").list().isEmpty());

        // Complete task
        taskService.deleteTask(taskId, true);

        // mobius.task.service.Task should be removed from runtime data
        // TODO: check for historic data when implemented!
        assertNull(taskService.createTaskQuery().taskId(taskId).singleResult());
    }

    @Test
    public void testOptimisticLockingThrownOnMultipleUpdates() {
        Task task = taskService.newTask();
        taskService.saveTask(task);
        String taskId = task.getId();

        // first modification
        Task task1 = taskService.createTaskQuery().taskId(taskId).singleResult();
        Task task2 = taskService.createTaskQuery().taskId(taskId).singleResult();

        task1.setDescription("first modification");
        taskService.saveTask(task1);

        // second modification on the initial instance
        task2.setDescription("second modification");
        try {
            taskService.saveTask(task2);
            fail("should get an exception here as the task was modified by someone else.");
        } catch (FlowableOptimisticLockingException expected) {
            // exception was thrown as expected
        }

        taskService.deleteTask(taskId, true);
    }

    // See https://activiti.atlassian.net/browse/ACT-1290
    @Test
    public void testRevisionUpdatedOnSave() {
        Task task = taskService.newTask();
        taskService.saveTask(task);
        assertEquals(1, ((TaskEntity) task).getRevision());

        task.setDescription("first modification");
        taskService.saveTask(task);
        assertEquals(2, ((TaskEntity) task).getRevision());

        task.setDescription("second modification");
        taskService.saveTask(task);
        assertEquals(3, ((TaskEntity) task).getRevision());

        taskService.deleteTask(task.getId(), true);
    }

    // See https://activiti.atlassian.net/browse/ACT-1290
    @Test
    public void testRevisionUpdatedOnSaveWhenFetchedUsingQuery() {
        Task task = taskService.newTask();
        taskService.saveTask(task);
        assertEquals(1, ((TaskEntity) task).getRevision());

        task.setAssignee("kermit");
        taskService.saveTask(task);
        assertEquals(2, ((TaskEntity) task).getRevision());

        // Now fetch the task through the query api
        task = taskService.createTaskQuery().singleResult();
        assertEquals(2, ((TaskEntity) task).getRevision());
        task.setPriority(1);
        taskService.saveTask(task);

        assertEquals(3, ((TaskEntity) task).getRevision());

        taskService.deleteTask(task.getId(), true);
    }

    @Test
    public void testHistoricVariableOkOnUpdate() {
        if (HistoryTestHelper.isHistoryLevelAtLeast(HistoryLevel.AUDIT, processEngineConfiguration)) {
            // 1. create a task
            Task task = taskService.newTask();
            task.setName("test execution");
            task.setOwner("josOwner");
            task.setAssignee("JosAssignee");
            taskService.saveTask(task);

            // 2. set task variables
            Map<String, Object> taskVariables = new HashMap<>();
            taskVariables.put("finishedAmount", 0);
            taskService.setVariables(task.getId(), taskVariables);

            // 3. complete this task with a new variable
            Map<String, Object> finishVariables = new HashMap<>();
            finishVariables.put("finishedAmount", 40);
            taskService.complete(task.getId(), finishVariables);
            
            waitForHistoryJobExecutorToProcessAllJobs(7000, 100);

            // 4. get completed variable
            List<HistoricVariableInstance> hisVarList = historyService.createHistoricVariableInstanceQuery().taskId(task.getId()).list();
            assertEquals(1, hisVarList.size());
            assertEquals(40, hisVarList.get(0).getValue());

            // Cleanup
            historyService.deleteHistoricTaskInstance(task.getId());
            managementService.executeCommand(commandContext -> {
                CommandContextUtil.getHistoricTaskService(commandContext).deleteHistoricTaskLogEntriesForTaskId(task.getId());
                return null;
            });
        }
    }

}
