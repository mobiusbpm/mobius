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

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import mobius.common.engine.impl.history.HistoryLevel;
import mobius.engine.history.HistoricProcessInstance;
import mobius.engine.impl.test.HistoryTestHelper;
import mobius.engine.impl.test.PluggableFlowableTestCase;
import mobius.engine.runtime.ProcessInstance;
import mobius.engine.test.Deployment;
import mobius.job.api.Job;
import mobius.job.service.impl.persistence.entity.TimerJobEntity;
import mobius.task.api.Task;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.jupiter.api.Test;

public class BoundaryTimerEventRepeatWithDurationTest extends PluggableFlowableTestCase {

    @Test
    @Deployment
    public void testRepeatWithDuration() throws Throwable {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MILLISECOND, 0);
        Date baseTime = calendar.getTime();

        // reset the timer
        Calendar nextTimeCal = Calendar.getInstance();
        nextTimeCal.setTime(baseTime);
        processEngineConfiguration.getClock().setCurrentTime(nextTimeCal.getTime());

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("repeatWithDuration");

        List<Task> tasks = taskService.createTaskQuery().list();
        assertEquals(1, tasks.size());

        Task task = tasks.get(0);
        assertEquals("Task A", task.getName());

        // Test Boundary Events
        // complete will cause timer to be created
        taskService.complete(task.getId());

        List<Job> jobs = managementService.createTimerJobQuery().list();
        assertEquals(1, jobs.size());

        // R/<duration> is persisted with start date in ISO 8601 Zulu time.
        String repeatStr = ((TimerJobEntity) jobs.get(0)).getRepeat();
        List<String> expression = Arrays.asList(repeatStr.split("/"));
        String startDateStr = expression.get(1);

        // Validate that repeat string is in ISO8601 Zulu time.
        DateTime startDateTime = ISODateTimeFormat.dateTime().parseDateTime(startDateStr);
        assertEquals(startDateTime, new DateTime(baseTime));

        // boundary events
        Job executableJob = managementService.moveTimerToExecutableJob(jobs.get(0).getId());

        managementService.executeJob(executableJob.getId());

        assertEquals(0, managementService.createJobQuery().list().size());
        jobs = managementService.createTimerJobQuery().list();
        assertEquals(1, jobs.size());

        nextTimeCal.add(Calendar.SECOND, 15);
        processEngineConfiguration.getClock().setCurrentTime(nextTimeCal.getTime());

        executableJob = managementService.moveTimerToExecutableJob(jobs.get(0).getId());
        managementService.executeJob(executableJob.getId());

        assertEquals(0, managementService.createJobQuery().list().size());
        jobs = managementService.createTimerJobQuery().list();
        assertEquals(1, jobs.size());

        nextTimeCal.add(Calendar.SECOND, 15);
        processEngineConfiguration.getClock().setCurrentTime(nextTimeCal.getTime());

        executableJob = managementService.moveTimerToExecutableJob(jobs.get(0).getId());
        managementService.executeJob(executableJob.getId());

        jobs = managementService.createTimerJobQuery().list();
        assertEquals(0, jobs.size());
        jobs = managementService.createJobQuery().list();
        assertEquals(0, jobs.size());

        tasks = taskService.createTaskQuery().list();
        task = tasks.get(0);
        assertEquals("Task B", task.getName());
        assertEquals(1, tasks.size());
        taskService.complete(task.getId());

        jobs = managementService.createTimerJobQuery().list();
        assertEquals(0, jobs.size());
        jobs = managementService.createJobQuery().list();
        assertEquals(0, jobs.size());

        if (HistoryTestHelper.isHistoryLevelAtLeast(HistoryLevel.ACTIVITY, processEngineConfiguration)) {
            HistoricProcessInstance historicInstance = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(processInstance.getId())
                    .singleResult();
            assertNotNull(historicInstance.getEndTime());
        }

        // now all the process instances should be completed
        List<ProcessInstance> processInstances = runtimeService.createProcessInstanceQuery().list();
        assertEquals(0, processInstances.size());

        // no jobs
        jobs = managementService.createJobQuery().list();
        assertEquals(0, jobs.size());

        jobs = managementService.createTimerJobQuery().list();
        assertEquals(0, jobs.size());

        // no tasks
        tasks = taskService.createTaskQuery().list();
        assertEquals(0, tasks.size());
    }
}