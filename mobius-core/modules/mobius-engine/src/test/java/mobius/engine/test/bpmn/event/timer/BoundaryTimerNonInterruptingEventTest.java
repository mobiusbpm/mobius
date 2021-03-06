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

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import mobius.engine.impl.test.PluggableFlowableTestCase;
import mobius.engine.runtime.Execution;
import mobius.engine.runtime.ProcessInstance;
import mobius.engine.test.Deployment;
import mobius.job.api.Job;
import mobius.job.api.TimerJobQuery;
import mobius.task.api.Task;
import mobius.task.api.TaskQuery;
import org.junit.jupiter.api.Test;

/**
 *
 */
public class BoundaryTimerNonInterruptingEventTest extends PluggableFlowableTestCase {

    @Test
    @Deployment
    public void testMultipleTimersOnUserTask() {
        // Set the clock fixed
        Date startTime = new Date();

        // After process start, there should be 3 timers created
        ProcessInstance pi = runtimeService.startProcessInstanceByKey("nonInterruptingTimersOnUserTask");
        Task task1 = taskService.createTaskQuery().singleResult();
        assertEquals("First Task", task1.getName());

        TimerJobQuery jobQuery = managementService.createTimerJobQuery().processInstanceId(pi.getId());
        List<Job> jobs = jobQuery.list();
        assertEquals(2, jobs.size());

        // After setting the clock to time '1 hour and 5 seconds', the first timer should fire
        processEngineConfiguration.getClock().setCurrentTime(new Date(startTime.getTime() + ((60 * 60 * 1000) + 5000)));
        Job job = managementService.createTimerJobQuery().executable().singleResult();
        assertNotNull(job);
        managementService.moveTimerToExecutableJob(job.getId());
        managementService.executeJob(job.getId());

        // we still have one timer more to fire
        assertEquals(1L, jobQuery.count());

        // and we are still in the first state, but in the second state as well!
        assertEquals(2L, taskService.createTaskQuery().count());
        List<Task> taskList = taskService.createTaskQuery().orderByTaskName().desc().list();
        assertEquals("First Task", taskList.get(0).getName());
        assertEquals("Escalation Task 1", taskList.get(1).getName());

        // complete the task and end the forked execution
        taskService.complete(taskList.get(1).getId());

        // but we still have the original executions
        assertEquals(1L, taskService.createTaskQuery().count());
        assertEquals("First Task", taskService.createTaskQuery().singleResult().getName());

        // After setting the clock to time '2 hour and 5 seconds', the second timer should fire
        processEngineConfiguration.getClock().setCurrentTime(new Date(startTime.getTime() + ((2 * 60 * 60 * 1000) + 5000)));
        waitForJobExecutorToProcessAllJobs(7000L, 25L);

        // no more timers to fire
        assertEquals(0L, jobQuery.count());

        // and we are still in the first state, but in the next escalation state as well
        assertEquals(2L, taskService.createTaskQuery().count());
        taskList = taskService.createTaskQuery().orderByTaskName().desc().list();
        assertEquals("First Task", taskList.get(0).getName());
        assertEquals("Escalation Task 2", taskList.get(1).getName());

        // This time we end the main task
        taskService.complete(taskList.get(0).getId());

        // but we still have the escalation task
        assertEquals(1L, taskService.createTaskQuery().count());
        Task escalationTask = taskService.createTaskQuery().singleResult();
        assertEquals("Escalation Task 2", escalationTask.getName());

        taskService.complete(escalationTask.getId());

        // now we are really done :-)
        assertProcessEnded(pi.getId());
    }

    @Test
    @Deployment
    public void testJoin() {
        // Set the clock fixed
        Date startTime = new Date();

        // After process start, there should be 3 timers created
        ProcessInstance pi = runtimeService.startProcessInstanceByKey("testJoin");
        Task task1 = taskService.createTaskQuery().singleResult();
        assertEquals("Main Task", task1.getName());

        TimerJobQuery jobQuery = managementService.createTimerJobQuery().processInstanceId(pi.getId());
        List<Job> jobs = jobQuery.list();
        assertEquals(1, jobs.size());

        // After setting the clock to time '1 hour and 5 seconds', the first timer should fire
        processEngineConfiguration.getClock().setCurrentTime(new Date(startTime.getTime() + ((60 * 60 * 1000) + 5000)));
        waitForJobExecutorToProcessAllJobs(7000L, 25L);

        // timer has fired
        assertEquals(0L, jobQuery.count());

        // we now have both tasks
        assertEquals(2L, taskService.createTaskQuery().count());

        // end the first
        taskService.complete(task1.getId());

        // we now have one task left
        assertEquals(1L, taskService.createTaskQuery().count());
        Task task2 = taskService.createTaskQuery().singleResult();
        assertEquals("Escalation Task", task2.getName());

        // complete the task, the parallel gateway should fire
        taskService.complete(task2.getId());

        // and the process has ended
        assertProcessEnded(pi.getId());
    }

    @Test
    @Deployment
    public void testTimerOnConcurrentTasks() {
        String procId = runtimeService.startProcessInstanceByKey("nonInterruptingOnConcurrentTasks").getId();
        assertEquals(2, taskService.createTaskQuery().count());

        Job timer = managementService.createTimerJobQuery().singleResult();
        managementService.moveTimerToExecutableJob(timer.getId());
        managementService.executeJob(timer.getId());
        assertEquals(3, taskService.createTaskQuery().count());

        // Complete task that was reached by non interrupting timer
        Task task = taskService.createTaskQuery().taskDefinitionKey("timerFiredTask").singleResult();
        taskService.complete(task.getId());
        assertEquals(2, taskService.createTaskQuery().count());

        // Complete other tasks
        for (Task t : taskService.createTaskQuery().list()) {
            taskService.complete(t.getId());
        }
        assertProcessEnded(procId);
    }

    // Difference with previous test: now the join will be reached first
    @Test
    @Deployment(resources = { "mobius/engine/test/bpmn/event/timer/BoundaryTimerNonInterruptingEventTest.testTimerOnConcurrentTasks.bpmn20.xml" })
    public void testTimerOnConcurrentTasks2() {
        String procId = runtimeService.startProcessInstanceByKey("nonInterruptingOnConcurrentTasks").getId();
        assertEquals(2, taskService.createTaskQuery().count());

        Job timer = managementService.createTimerJobQuery().singleResult();
        managementService.moveTimerToExecutableJob(timer.getId());
        managementService.executeJob(timer.getId());
        assertEquals(3, taskService.createTaskQuery().count());

        // Complete 2 tasks that will trigger the join
        Task task = taskService.createTaskQuery().taskDefinitionKey("firstTask").singleResult();
        taskService.complete(task.getId());
        task = taskService.createTaskQuery().taskDefinitionKey("secondTask").singleResult();
        taskService.complete(task.getId());
        assertEquals(1, taskService.createTaskQuery().count());

        // Finally, complete the task that was created due to the timer
        task = taskService.createTaskQuery().taskDefinitionKey("timerFiredTask").singleResult();
        taskService.complete(task.getId());

        assertProcessEnded(procId);
    }

    @Test
    @Deployment
    public void testTimerWithCycle() throws Exception {
        String processInstanceId = runtimeService.startProcessInstanceByKey("nonInterruptingCycle").getId();

        List<Job> jobs = managementService.createTimerJobQuery().processInstanceId(processInstanceId).list();
        assertEquals(1, jobs.size());

        // boundary events
        waitForJobExecutorToProcessAllJobs(2000, 100);

        // a new job must be prepared because there are indefinite number of repeats 1 hour interval");
        assertEquals(1, managementService.createTimerJobQuery().processInstanceId(processInstanceId).count());

        moveByMinutes(60);
        waitForJobExecutorToProcessAllJobs(2000, 100);

        // a new job must be prepared because there are indefinite number of repeats 1 hour interval");
        assertEquals(1, managementService.createTimerJobQuery().processInstanceId(processInstanceId).count());

        Task task = taskService.createTaskQuery().taskDefinitionKey("task").singleResult();
        taskService.complete(task.getId());

        moveByMinutes(60);
        try {
            waitForJobExecutorToProcessAllJobs(2000, 100);
        } catch (Exception ex) {
            fail("No more jobs since the user completed the task");
        }
    }

    /*
     * see https://activiti.atlassian.net/browse/ACT-1173
     */
    @Test
    @Deployment
    public void testTimerOnEmbeddedSubprocess() {
        String id = runtimeService.startProcessInstanceByKey("nonInterruptingTimerOnEmbeddedSubprocess").getId();

        TaskQuery tq = taskService.createTaskQuery().taskAssignee("kermit");

        assertEquals(1, tq.count());

        // Simulate timer
        Job timer = managementService.createTimerJobQuery().singleResult();
        managementService.moveTimerToExecutableJob(timer.getId());
        managementService.executeJob(timer.getId());

        tq = taskService.createTaskQuery().taskAssignee("kermit");

        assertEquals(2, tq.count());

        List<Task> tasks = tq.list();

        taskService.complete(tasks.get(0).getId());
        taskService.complete(tasks.get(1).getId());

        assertProcessEnded(id);
    }

    /*
     * see https://activiti.atlassian.net/browse/ACT-1106
     */
    @Test
    @Deployment
    public void testReceiveTaskWithBoundaryTimer() {
        // Set the clock fixed
        HashMap<String, Object> variables = new HashMap<>();
        variables.put("timeCycle", "R/PT1H");

        // After process start, there should be a timer created
        ProcessInstance pi = runtimeService.startProcessInstanceByKey("nonInterruptingCycle", variables);

        TimerJobQuery jobQuery = managementService.createTimerJobQuery().processInstanceId(pi.getId());
        List<Job> jobs = jobQuery.list();
        assertEquals(1, jobs.size());

        // The Execution Query should work normally and find executions in state "task"
        List<Execution> executions = runtimeService.createExecutionQuery().activityId("task").list();
        assertEquals(1, executions.size());
        List<String> activeActivityIds = runtimeService.getActiveActivityIds(executions.get(0).getId());
        assertEquals(2, activeActivityIds.size());
        Collections.sort(activeActivityIds);
        assertEquals("task", activeActivityIds.get(0));
        assertEquals("timer", activeActivityIds.get(1));

        runtimeService.trigger(executions.get(0).getId());

        // // After setting the clock to time '1 hour and 5 seconds', the second
        // timer should fire
        // processEngineConfiguration.getClock().setCurrentTime(new
        // Date(startTime.getTime() + ((60 * 60 * 1000) + 5000)));
        // waitForJobExecutorToProcessAllJobs(7000L, 25L);
        // assertEquals(0L, jobQuery.count());

        // which means the process has ended
        assertProcessEnded(pi.getId());
    }

    @Test
    @Deployment
    public void testTimerOnConcurrentSubprocess() {
        String procId = runtimeService.startProcessInstanceByKey("testTimerOnConcurrentSubprocess").getId();
        assertEquals(4, taskService.createTaskQuery().count());

        Job timer = managementService.createTimerJobQuery().singleResult();
        managementService.moveTimerToExecutableJob(timer.getId());
        managementService.executeJob(timer.getId());
        assertEquals(5, taskService.createTaskQuery().count());

        // Complete 4 tasks that will trigger the join
        Task task = taskService.createTaskQuery().taskDefinitionKey("sub1task1").singleResult();
        taskService.complete(task.getId());
        task = taskService.createTaskQuery().taskDefinitionKey("sub1task2").singleResult();
        taskService.complete(task.getId());
        task = taskService.createTaskQuery().taskDefinitionKey("sub2task1").singleResult();
        taskService.complete(task.getId());
        task = taskService.createTaskQuery().taskDefinitionKey("sub2task2").singleResult();
        taskService.complete(task.getId());
        assertEquals(1, taskService.createTaskQuery().count());

        // Finally, complete the task that was created due to the timer
        task = taskService.createTaskQuery().taskDefinitionKey("timerFiredTask").singleResult();
        taskService.complete(task.getId());

        assertProcessEnded(procId);
    }

    @Test
    @Deployment(resources = "mobius/engine/test/bpmn/event/timer/BoundaryTimerNonInterruptingEventTest.testTimerOnConcurrentSubprocess.bpmn20.xml")
    public void testTimerOnConcurrentSubprocess2() {
        String procId = runtimeService.startProcessInstanceByKey("testTimerOnConcurrentSubprocess").getId();
        assertEquals(4, taskService.createTaskQuery().count());

        Job timer = managementService.createTimerJobQuery().singleResult();
        managementService.moveTimerToExecutableJob(timer.getId());
        managementService.executeJob(timer.getId());
        assertEquals(5, taskService.createTaskQuery().count());

        Task task = taskService.createTaskQuery().taskDefinitionKey("sub1task1").singleResult();
        taskService.complete(task.getId());
        task = taskService.createTaskQuery().taskDefinitionKey("sub1task2").singleResult();
        taskService.complete(task.getId());

        // complete the task that was created due to the timer
        task = taskService.createTaskQuery().taskDefinitionKey("timerFiredTask").singleResult();
        taskService.complete(task.getId());

        task = taskService.createTaskQuery().taskDefinitionKey("sub2task1").singleResult();
        taskService.complete(task.getId());
        task = taskService.createTaskQuery().taskDefinitionKey("sub2task2").singleResult();
        taskService.complete(task.getId());
        assertEquals(0, taskService.createTaskQuery().count());

        assertProcessEnded(procId);
    }

    private void moveByMinutes(int minutes) throws Exception {
        processEngineConfiguration.getClock().setCurrentTime(new Date(processEngineConfiguration.getClock().getCurrentTime().getTime() + ((minutes * 60 * 1000))));
    }

}
