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

package mobius.engine.test.history;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mobius.common.engine.api.FlowableIllegalArgumentException;
import mobius.common.engine.impl.history.HistoryLevel;
import mobius.common.engine.impl.util.CollectionUtil;
import mobius.engine.history.HistoricActivityInstance;
import mobius.engine.history.HistoricActivityInstanceQuery;
import mobius.engine.history.HistoricProcessInstance;
import mobius.engine.impl.test.HistoryTestHelper;
import mobius.engine.impl.test.PluggableFlowableTestCase;
import mobius.engine.runtime.Execution;
import mobius.engine.runtime.ProcessInstance;
import mobius.engine.test.Deployment;
import mobius.task.api.Task;
import org.junit.jupiter.api.Test;

/**
 *
 *
 */
public class HistoricActivityInstanceTest extends PluggableFlowableTestCase {

    @Test
    @Deployment
    public void testHistoricActivityInstanceNoop() {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("noopProcess");
        
        waitForHistoryJobExecutorToProcessAllJobs(7000, 100);

        HistoricActivityInstance historicActivityInstance = historyService.createHistoricActivityInstanceQuery().activityId("noop").singleResult();

        assertEquals("noop", historicActivityInstance.getActivityId());
        assertEquals("serviceTask", historicActivityInstance.getActivityType());
        assertNotNull(historicActivityInstance.getProcessDefinitionId());
        assertEquals(processInstance.getId(), historicActivityInstance.getProcessInstanceId());
        assertNotNull(historicActivityInstance.getStartTime());
        assertNotNull(historicActivityInstance.getEndTime());
        assertTrue(historicActivityInstance.getDurationInMillis() >= 0);
    }
    @Test
    @Deployment
    public void testOneTaskProcessActivityTypes() {
        ProcessInstance processInstance = runtimeService.createProcessInstanceBuilder()
                        .processDefinitionKey("oneTaskProcessActivityTypesProcess")
                        .overrideProcessDefinitionTenantId("tenant1")
                        .start();
    
        Set<String> activityTypes = new HashSet<>();
        activityTypes.add("startEvent");

        if (HistoryTestHelper.isHistoryLevelAtLeast(HistoryLevel.ACTIVITY, processEngineConfiguration)) {
            List<HistoricActivityInstance> historicActivityInstance = historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstance.getId()).activityTypes(activityTypes).list();
            assertEquals(1, historicActivityInstance.size());
    
            activityTypes.add("userTask");
            List<HistoricActivityInstance> historicActivityInstance2 = historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstance.getId()).activityTypes(activityTypes).list();
            assertEquals(2, historicActivityInstance2.size());

            Calendar hourAgo = Calendar.getInstance();
            hourAgo.add(Calendar.HOUR_OF_DAY, -1);
            Calendar hourFromNow = Calendar.getInstance();
            hourFromNow.add(Calendar.HOUR_OF_DAY, 1);
    
            // Start/end dates
            assertEquals(0, historyService.createHistoricActivityInstanceQuery().activityId("theTask").finishedBefore(hourAgo.getTime()).count());
            assertEquals(0, historyService.createHistoricActivityInstanceQuery().activityId("theTask").finishedBefore(hourFromNow.getTime()).count());
            assertEquals(0, historyService.createHistoricActivityInstanceQuery().activityId("theTask").finishedAfter(hourAgo.getTime()).count());
            assertEquals(0, historyService.createHistoricActivityInstanceQuery().activityId("theTask").finishedAfter(hourFromNow.getTime()).count());
            assertEquals(1, historyService.createHistoricActivityInstanceQuery().activityId("theTask").startedBefore(hourFromNow.getTime()).count());
            assertEquals(0, historyService.createHistoricActivityInstanceQuery().activityId("theTask").startedBefore(hourAgo.getTime()).count());
            assertEquals(1, historyService.createHistoricActivityInstanceQuery().activityId("theTask").startedAfter(hourAgo.getTime()).count());
            assertEquals(0, historyService.createHistoricActivityInstanceQuery().activityId("theTask").startedAfter(hourFromNow.getTime()).count());
            assertEquals(0, historyService.createHistoricActivityInstanceQuery().activityId("theTask").startedAfter(hourFromNow.getTime()).startedBefore(hourAgo.getTime()).count());
            
            // After finishing process
            taskService.complete(taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult().getId());
            
            HistoryTestHelper.waitForJobExecutorToProcessAllHistoryJobs(processEngineConfiguration, managementService, 5000, 200);
            
            assertEquals(1, historyService.createHistoricActivityInstanceQuery().activityId("theTask").finished().count());
            assertEquals(0, historyService.createHistoricActivityInstanceQuery().activityId("theTask").finishedBefore(hourAgo.getTime()).count());
            assertEquals(1, historyService.createHistoricActivityInstanceQuery().activityId("theTask").finishedBefore(hourFromNow.getTime()).count());
            assertEquals(1, historyService.createHistoricActivityInstanceQuery().activityId("theTask").finishedAfter(hourAgo.getTime()).count());
            assertEquals(0, historyService.createHistoricActivityInstanceQuery().activityId("theTask").finishedAfter(hourFromNow.getTime()).count());
            assertEquals(0, historyService.createHistoricActivityInstanceQuery().activityId("theTask").finishedBefore(hourAgo.getTime()).finishedAfter(hourFromNow.getTime()).count());
        }
    
        ProcessInstance processInstance2 = runtimeService.createProcessInstanceBuilder()
                        .processDefinitionKey("oneTaskProcessActivityTypesProcess")
                        .overrideProcessDefinitionTenantId("tenant1")
                        .start();
        
        taskService.complete(taskService.createTaskQuery().processInstanceId(processInstance2.getId()).singleResult().getId());
        
        ProcessInstance otherTenantProcessInstance = runtimeService.createProcessInstanceBuilder()
                        .processDefinitionKey("oneTaskProcessActivityTypesProcess")
                        .overrideProcessDefinitionTenantId("tenant2")
                        .start();
        
        taskService.complete(taskService.createTaskQuery().processInstanceId(otherTenantProcessInstance.getId()).singleResult().getId());
        
        if (HistoryTestHelper.isHistoryLevelAtLeast(HistoryLevel.ACTIVITY, processEngineConfiguration)) {
            assertEquals(3, historyService.createHistoricActivityInstanceQuery().activityId("theTask").finished().count());
            
            List<String> tenantIds = new ArrayList<>();
            tenantIds.add("tenant1");
            tenantIds.add("tenant2");
            assertEquals(3, historyService.createHistoricActivityInstanceQuery().activityId("theTask").finished().tenantIdIn(tenantIds).count());
            
            assertEquals(2, historyService.createHistoricActivityInstanceQuery().activityId("theTask").finished().tenantIdIn(Collections.singletonList("tenant1")).count());
            
            assertEquals(1, historyService.createHistoricActivityInstanceQuery().activityId("theTask").finished().tenantIdIn(Collections.singletonList("tenant2")).count());
            
            assertEquals(0, historyService.createHistoricActivityInstanceQuery().activityId("theTask").finished().tenantIdIn(Collections.singletonList("unexisting")).count());
        }
    }

    @Test
    @Deployment
    public void testHistoricActivityInstanceReceive() {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("receiveProcess");
        
        waitForHistoryJobExecutorToProcessAllJobs(7000, 100);

        HistoricActivityInstance historicActivityInstance = historyService.createHistoricActivityInstanceQuery().activityId("receive").singleResult();
        assertActivityInstancesAreSame(historicActivityInstance, runtimeService.createActivityInstanceQuery().activityInstanceId(historicActivityInstance.getId()).singleResult());

        assertEquals("receive", historicActivityInstance.getActivityId());
        assertEquals("receiveTask", historicActivityInstance.getActivityType());
        assertNull(historicActivityInstance.getEndTime());
        assertNull(historicActivityInstance.getDurationInMillis());
        assertNotNull(historicActivityInstance.getProcessDefinitionId());
        assertEquals(processInstance.getId(), historicActivityInstance.getProcessInstanceId());
        assertNotNull(historicActivityInstance.getStartTime());

        Execution execution = runtimeService.createExecutionQuery().onlyChildExecutions().processInstanceId(processInstance.getId()).singleResult();
        runtimeService.trigger(execution.getId());
        
        waitForHistoryJobExecutorToProcessAllJobs(7000, 100);

        historicActivityInstance = historyService.createHistoricActivityInstanceQuery().activityId("receive").singleResult();

        assertEquals("receive", historicActivityInstance.getActivityId());
        assertEquals("receiveTask", historicActivityInstance.getActivityType());
        assertNotNull(historicActivityInstance.getEndTime());
        assertTrue(historicActivityInstance.getDurationInMillis() >= 0);
        assertNotNull(historicActivityInstance.getProcessDefinitionId());
        assertEquals(processInstance.getId(), historicActivityInstance.getProcessInstanceId());
        assertNotNull(historicActivityInstance.getStartTime());
    }

    @Test
    @Deployment(resources = "mobius/engine/test/history/oneTaskProcess.bpmn20.xml")
    public void testHistoricActivityInstanceUnfinished() {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("oneTaskProcess");
        assertNotNull(processInstance);
        
        waitForHistoryJobExecutorToProcessAllJobs(7000, 100);

        HistoricActivityInstanceQuery historicActivityInstanceQuery = historyService.createHistoricActivityInstanceQuery();

        long finishedActivityInstanceCount = historicActivityInstanceQuery.finished().count();
        assertEquals("The Start event and sequence flow are completed", 2, finishedActivityInstanceCount);

        long unfinishedActivityInstanceCount = historicActivityInstanceQuery.unfinished().count();
        assertEquals("One active (unfinished) User mobius.task.service.Task", 1, unfinishedActivityInstanceCount);
    }

    @Test
    @Deployment
    public void testHistoricActivityInstanceQuery() {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("noopProcess");
        
        waitForHistoryJobExecutorToProcessAllJobs(7000, 100);

        assertEquals(0, historyService.createHistoricActivityInstanceQuery().activityId("nonExistingActivityId").list().size());
        assertEquals(1, historyService.createHistoricActivityInstanceQuery().activityId("noop").list().size());

        assertEquals(0, historyService.createHistoricActivityInstanceQuery().activityType("nonExistingActivityType").list().size());
        assertEquals(1, historyService.createHistoricActivityInstanceQuery().activityType("serviceTask").list().size());

        assertEquals(0, historyService.createHistoricActivityInstanceQuery().activityName("nonExistingActivityName").list().size());
        assertEquals(1, historyService.createHistoricActivityInstanceQuery().activityName("No operation").list().size());

        assertEquals(0, historyService.createHistoricActivityInstanceQuery().taskAssignee("nonExistingAssignee").list().size());

        assertEquals(0, historyService.createHistoricActivityInstanceQuery().executionId("nonExistingExecutionId").list().size());

        if (HistoryTestHelper.isHistoryLevelAtLeast(HistoryLevel.ACTIVITY, processEngineConfiguration)) {
            assertEquals(5, historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstance.getId()).list().size());
        } else {
            assertEquals(0, historyService.createHistoricActivityInstanceQuery().executionId(processInstance.getId()).list().size());
        }

        assertEquals(0, historyService.createHistoricActivityInstanceQuery().processInstanceId("nonExistingProcessInstanceId").list().size());

        if (HistoryTestHelper.isHistoryLevelAtLeast(HistoryLevel.ACTIVITY, processEngineConfiguration)) {
            assertEquals(5, historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstance.getId()).list().size());
        } else {
            assertEquals(0, historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstance.getId()).list().size());
        }

        assertEquals(0, historyService.createHistoricActivityInstanceQuery().processDefinitionId("nonExistingProcessDefinitionId").list().size());

        if (HistoryTestHelper.isHistoryLevelAtLeast(HistoryLevel.ACTIVITY, processEngineConfiguration)) {
            assertEquals(5, historyService.createHistoricActivityInstanceQuery().processDefinitionId(processInstance.getProcessDefinitionId()).list().size());
        } else {
            assertEquals(0, historyService.createHistoricActivityInstanceQuery().processDefinitionId(processInstance.getProcessDefinitionId()).list().size());
        }

        assertEquals(0, historyService.createHistoricActivityInstanceQuery().unfinished().list().size());

        if (HistoryTestHelper.isHistoryLevelAtLeast(HistoryLevel.ACTIVITY, processEngineConfiguration)) {
            assertEquals(5, historyService.createHistoricActivityInstanceQuery().finished().list().size());
        } else {
            assertEquals(0, historyService.createHistoricActivityInstanceQuery().finished().list().size());
        }

        if (HistoryTestHelper.isHistoryLevelAtLeast(HistoryLevel.ACTIVITY, processEngineConfiguration)) {
            HistoricActivityInstance historicActivityInstance = historyService.createHistoricActivityInstanceQuery().list().get(0);
            assertEquals(1, historyService.createHistoricActivityInstanceQuery().activityInstanceId(historicActivityInstance.getId()).list().size());
        }
    }

    @Test
    @Deployment
    public void testHistoricActivityInstanceForEventsQuery() {
        ProcessInstance pi = runtimeService.startProcessInstanceByKey("eventProcess");
        assertEquals(1, taskService.createTaskQuery().count());
        runtimeService.signalEventReceived("signal");
        assertProcessEnded(pi.getId());
        
        waitForHistoryJobExecutorToProcessAllJobs(7000, 100);

        assertEquals(1, historyService.createHistoricActivityInstanceQuery().activityId("noop").list().size());
        assertEquals(1, historyService.createHistoricActivityInstanceQuery().activityId("userTask").list().size());
        assertEquals(1, historyService.createHistoricActivityInstanceQuery().activityId("intermediate-event").list().size());
        assertEquals(1, historyService.createHistoricActivityInstanceQuery().activityId("start").list().size());
        assertEquals(1, historyService.createHistoricActivityInstanceQuery().activityId("end").list().size());

        // TODO: Discuss if boundary events will occur in the log! 
        // assertEquals(1,
        // historyService.createHistoricActivityInstanceQuery().activityId("boundaryEvent").list().size());

        HistoricActivityInstance intermediateEvent = historyService.createHistoricActivityInstanceQuery().activityId("intermediate-event").singleResult();
        assertNotNull(intermediateEvent.getStartTime());
        assertNotNull(intermediateEvent.getEndTime());

        HistoricActivityInstance startEvent = historyService.createHistoricActivityInstanceQuery().activityId("start").singleResult();
        assertNotNull(startEvent.getStartTime());
        assertNotNull(startEvent.getEndTime());

        HistoricActivityInstance endEvent = historyService.createHistoricActivityInstanceQuery().activityId("end").singleResult();
        assertNotNull(endEvent.getStartTime());
        assertNotNull(endEvent.getEndTime());
    }

    @Test
    @Deployment
    public void testHistoricActivityInstanceProperties() {
        // Start process instance
        runtimeService.startProcessInstanceByKey("taskAssigneeProcess");
        
        waitForHistoryJobExecutorToProcessAllJobs(7000, 100);

        // Get task list
        HistoricActivityInstance historicActivityInstance = historyService.createHistoricActivityInstanceQuery().activityId("theTask").singleResult();
        assertActivityInstancesAreSame(historicActivityInstance, runtimeService.createActivityInstanceQuery().activityInstanceId(historicActivityInstance.getId()).singleResult());

        Task task = taskService.createTaskQuery().singleResult();
        assertEquals(task.getId(), historicActivityInstance.getTaskId());
        assertEquals("kermit", historicActivityInstance.getAssignee());
    }

    @Test
    @Deployment(resources = { "mobius/engine/test/history/calledProcess.bpmn20.xml", "mobius/engine/test/history/HistoricActivityInstanceTest.testCallSimpleSubProcess.bpmn20.xml" })
    public void testHistoricActivityInstanceCalledProcessId() {
        runtimeService.startProcessInstanceByKey("callSimpleSubProcess");
        
        waitForHistoryJobExecutorToProcessAllJobs(7000, 100);

        HistoricActivityInstance historicActivityInstance = historyService.createHistoricActivityInstanceQuery().activityId("callSubProcess").singleResult();

        HistoricProcessInstance oldInstance = historyService.createHistoricProcessInstanceQuery().processDefinitionKey("calledProcess").singleResult();

        assertEquals(oldInstance.getId(), historicActivityInstance.getCalledProcessInstanceId());
    }

    @Test
    @Deployment
    public void testSorting() {
        runtimeService.startProcessInstanceByKey("process");

        int expectedActivityInstances;
        if (HistoryTestHelper.isHistoryLevelAtLeast(HistoryLevel.ACTIVITY, processEngineConfiguration, 20000)) {
            expectedActivityInstances = 3;
        } else {
            expectedActivityInstances = 0;
        }

        assertEquals(expectedActivityInstances, historyService.createHistoricActivityInstanceQuery().orderByHistoricActivityInstanceId().asc().list().size());
        assertEquals(expectedActivityInstances, historyService.createHistoricActivityInstanceQuery().orderByHistoricActivityInstanceStartTime().asc().list().size());
        assertEquals(expectedActivityInstances, historyService.createHistoricActivityInstanceQuery().orderByHistoricActivityInstanceEndTime().asc().list().size());
        assertEquals(expectedActivityInstances, historyService.createHistoricActivityInstanceQuery().orderByHistoricActivityInstanceDuration().asc().list().size());
        assertEquals(expectedActivityInstances, historyService.createHistoricActivityInstanceQuery().orderByExecutionId().asc().list().size());
        assertEquals(expectedActivityInstances, historyService.createHistoricActivityInstanceQuery().orderByProcessDefinitionId().asc().list().size());
        assertEquals(expectedActivityInstances, historyService.createHistoricActivityInstanceQuery().orderByProcessInstanceId().asc().list().size());

        assertEquals(expectedActivityInstances, historyService.createHistoricActivityInstanceQuery().orderByHistoricActivityInstanceId().desc().list().size());
        assertEquals(expectedActivityInstances, historyService.createHistoricActivityInstanceQuery().orderByHistoricActivityInstanceStartTime().desc().list().size());
        assertEquals(expectedActivityInstances, historyService.createHistoricActivityInstanceQuery().orderByHistoricActivityInstanceEndTime().desc().list().size());
        assertEquals(expectedActivityInstances, historyService.createHistoricActivityInstanceQuery().orderByHistoricActivityInstanceDuration().desc().list().size());
        assertEquals(expectedActivityInstances, historyService.createHistoricActivityInstanceQuery().orderByExecutionId().desc().list().size());
        assertEquals(expectedActivityInstances, historyService.createHistoricActivityInstanceQuery().orderByProcessDefinitionId().desc().list().size());
        assertEquals(expectedActivityInstances, historyService.createHistoricActivityInstanceQuery().orderByProcessInstanceId().desc().list().size());

        assertEquals(expectedActivityInstances, historyService.createHistoricActivityInstanceQuery().orderByHistoricActivityInstanceId().asc().count());
        assertEquals(expectedActivityInstances, historyService.createHistoricActivityInstanceQuery().orderByHistoricActivityInstanceStartTime().asc().count());
        assertEquals(expectedActivityInstances, historyService.createHistoricActivityInstanceQuery().orderByHistoricActivityInstanceEndTime().asc().count());
        assertEquals(expectedActivityInstances, historyService.createHistoricActivityInstanceQuery().orderByHistoricActivityInstanceDuration().asc().count());
        assertEquals(expectedActivityInstances, historyService.createHistoricActivityInstanceQuery().orderByExecutionId().asc().count());
        assertEquals(expectedActivityInstances, historyService.createHistoricActivityInstanceQuery().orderByProcessDefinitionId().asc().count());
        assertEquals(expectedActivityInstances, historyService.createHistoricActivityInstanceQuery().orderByProcessInstanceId().asc().count());

        assertEquals(expectedActivityInstances, historyService.createHistoricActivityInstanceQuery().orderByHistoricActivityInstanceId().desc().count());
        assertEquals(expectedActivityInstances, historyService.createHistoricActivityInstanceQuery().orderByHistoricActivityInstanceStartTime().desc().count());
        assertEquals(expectedActivityInstances, historyService.createHistoricActivityInstanceQuery().orderByHistoricActivityInstanceEndTime().desc().count());
        assertEquals(expectedActivityInstances, historyService.createHistoricActivityInstanceQuery().orderByHistoricActivityInstanceDuration().desc().count());
        assertEquals(expectedActivityInstances, historyService.createHistoricActivityInstanceQuery().orderByExecutionId().desc().count());
        assertEquals(expectedActivityInstances, historyService.createHistoricActivityInstanceQuery().orderByProcessDefinitionId().desc().count());
        assertEquals(expectedActivityInstances, historyService.createHistoricActivityInstanceQuery().orderByProcessInstanceId().desc().count());
    }

    @Test
    public void testInvalidSorting() {
        try {
            historyService.createHistoricActivityInstanceQuery().asc().list();
            fail();
        } catch (FlowableIllegalArgumentException e) {

        }

        try {
            historyService.createHistoricActivityInstanceQuery().desc().list();
            fail();
        } catch (FlowableIllegalArgumentException e) {

        }

        try {
            historyService.createHistoricActivityInstanceQuery().orderByHistoricActivityInstanceDuration().list();
            fail();
        } catch (FlowableIllegalArgumentException e) {

        }
    }

    /**
     * Test to validate fix for ACT-1399: Boundary-event and event-based auditing
     */
    @Test
    @Deployment
    public void testBoundaryEvent() {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("boundaryEventProcess");
        // Complete the task with the boundary-event on it
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        assertNotNull(task);
        taskService.complete(task.getId());

        assertEquals(0L, runtimeService.createProcessInstanceQuery().processInstanceId(processInstance.getId()).count());
        
        waitForHistoryJobExecutorToProcessAllJobs(7000, 100);

        // Check if there is NO historic activity instance for a boundary-event that has not triggered
        HistoricActivityInstance historicActivityInstance = historyService.createHistoricActivityInstanceQuery().activityId("boundary").processInstanceId(processInstance.getId()).singleResult();

        assertNull(historicActivityInstance);

        // Now check the history when the boundary-event is fired
        processInstance = runtimeService.startProcessInstanceByKey("boundaryEventProcess");

        task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();

        Execution signalExecution = runtimeService.createExecutionQuery().signalEventSubscriptionName("alert").singleResult();
        runtimeService.signalEventReceived("alert", signalExecution.getId());
        assertEquals(0L, runtimeService.createProcessInstanceQuery().processInstanceId(processInstance.getId()).count());
        
        waitForHistoryJobExecutorToProcessAllJobs(7000, 100);

        historicActivityInstance = historyService.createHistoricActivityInstanceQuery().activityId("boundary").processInstanceId(processInstance.getId()).singleResult();

        assertNotNull(historicActivityInstance);
        assertNotNull(historicActivityInstance.getStartTime());
        assertNotNull(historicActivityInstance.getEndTime());
    }

    /**
     * Test to validate fix for ACT-1399: Boundary-event and event-based auditing
     */
    @Test
    @Deployment
    public void testEventBasedGateway() {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("catchSignal");
        Execution waitingExecution = runtimeService.createExecutionQuery().signalEventSubscriptionName("alert").singleResult();
        assertNotNull(waitingExecution);
        runtimeService.signalEventReceived("alert", waitingExecution.getId());

        assertEquals(0L, runtimeService.createProcessInstanceQuery().processInstanceId(processInstance.getId()).count());
        
        waitForHistoryJobExecutorToProcessAllJobs(7000, 100);

        HistoricActivityInstance historicActivityInstance = historyService.createHistoricActivityInstanceQuery().activityId("eventBasedgateway").processInstanceId(processInstance.getId()).singleResult();

        assertNotNull(historicActivityInstance);
    }

    /**
     * Test to validate fix for ACT-1549: endTime of joining parallel gateway is not set
     */
    @Test
    @Deployment
    public void testParallelJoinEndTime() {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("forkJoin");

        List<Task> tasksToComplete = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list();
        assertEquals(2, tasksToComplete.size());

        // Complete both tasks, second task-complete should end the fork-gateway and set time
        taskService.complete(tasksToComplete.get(0).getId());
        taskService.complete(tasksToComplete.get(1).getId());
        
        waitForHistoryJobExecutorToProcessAllJobs(7000, 100);

        List<HistoricActivityInstance> historicActivityInstance = historyService.createHistoricActivityInstanceQuery().activityId("join").processInstanceId(processInstance.getId()).list();

        assertNotNull(historicActivityInstance);

        // History contains 2 entries for parallel join (one for each path
        // arriving in the join), should contain end-time
        assertEquals(2, historicActivityInstance.size());
        assertNotNull(historicActivityInstance.get(0).getEndTime());
        assertNotNull(historicActivityInstance.get(1).getEndTime());
    }

    @Test
    @Deployment
    public void testLoop() {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("historic-activity-loops", CollectionUtil.singletonMap("input", 0));

        // completing 10 user tasks
        // 15 service tasks should have passed

        for (int i = 0; i < 10; i++) {
            Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
            Number inputNumber = (Number) taskService.getVariable(task.getId(), "input");
            int input = inputNumber.intValue();
            assertEquals(i, input);
            taskService.complete(task.getId(), CollectionUtil.singletonMap("input", input + 1));
            task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        }
        
        waitForHistoryJobExecutorToProcessAllJobs(7000, 100);

        // Verify history
        List<HistoricActivityInstance> taskActivityInstances = historyService.createHistoricActivityInstanceQuery().activityType("userTask").list();
        assertEquals(10, taskActivityInstances.size());
        for (HistoricActivityInstance historicActivityInstance : taskActivityInstances) {
            assertNotNull(historicActivityInstance.getStartTime());
            assertNotNull(historicActivityInstance.getEndTime());
        }

        List<HistoricActivityInstance> serviceTaskInstances = historyService.createHistoricActivityInstanceQuery().activityType("serviceTask").list();
        assertEquals(15, serviceTaskInstances.size());
        for (HistoricActivityInstance historicActivityInstance : serviceTaskInstances) {
            assertNotNull(historicActivityInstance.getStartTime());
            assertNotNull(historicActivityInstance.getEndTime());
        }
    }

    @Test
    @Deployment(
        resources = {
            "mobius/engine/test/api/runtime/callActivity.bpmn20.xml",
            "mobius/engine/test/api/runtime/calledActivity.bpmn20.xml"
        }
    )
    public void callSubProcess() {
        ProcessInstance pi = this.runtimeService.startProcessInstanceByKey("callActivity");

        if (HistoryTestHelper.isHistoryLevelAtLeast(HistoryLevel.ACTIVITY, processEngineConfiguration)) {
            HistoricActivityInstance callSubProcessActivityInstance = historyService.createHistoricActivityInstanceQuery().processInstanceId(pi.getId())
                .activityId("callSubProcess").singleResult();
            assertThat(callSubProcessActivityInstance.getCalledProcessInstanceId(), is(
                runtimeService.createProcessInstanceQuery().superProcessInstanceId(pi.getId()).singleResult().getId()));
        }
    }


}
