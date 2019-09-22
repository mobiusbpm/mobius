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
package mobius.cmmn.test;

import mobius.cmmn.engine.test.impl.CmmnJobTestHelper;
import mobius.common.engine.api.FlowableException;
import mobius.common.engine.impl.runtime.Clock;
import mobius.engine.impl.cfg.ProcessEngineConfigurationImpl;
import mobius.engine.impl.test.JobTestHelper;
import mobius.engine.runtime.ProcessInstance;
import mobius.job.api.Job;
import mobius.task.api.Task;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Tijs Rademakers
 */
public class BpmnTimerTaskTest extends mobius.cmmn.test.AbstractProcessEngineIntegrationTest {
    
    @Before
    public void deployTimerProcess() {
        if (processEngineRepositoryService.createDeploymentQuery().count() == 0) {
            processEngineRepositoryService.createDeployment().addClasspathResource("mobius/cmmn/test/taskTimerProcess.bpmn20.xml").deploy();
        }
    }
    
    @Test
    public void testBpmnTimerTask() {
        ProcessInstance processInstance = processEngineRuntimeService.startProcessInstanceByKey("timerProcess");
        List<Task> processTasks = processEngineTaskService.createTaskQuery().processInstanceId(processInstance.getId()).list();
        assertEquals(1, processTasks.size());
        
        List<Job> timerJobs = processEngineManagementService.createTimerJobQuery().processInstanceId(processInstance.getId()).list();
        assertEquals(1, timerJobs.size());
        
        timerJobs = cmmnManagementService.createTimerJobQuery().processInstanceId(processInstance.getId()).executable().list();
        assertEquals(0, timerJobs.size());
        
        Clock clock = cmmnEngineConfiguration.getClock();
        Calendar currentCalendar = clock.getCurrentCalendar();
        currentCalendar.add(Calendar.MINUTE, 15);
        clock.setCurrentCalendar(currentCalendar);
        
        timerJobs = cmmnManagementService.createTimerJobQuery().processInstanceId(processInstance.getId()).executable().list();
        assertEquals(1, timerJobs.size());
        String timerJobId = timerJobs.get(0).getId();
        
        try {
            CmmnJobTestHelper.waitForJobExecutorToProcessAllJobs(cmmnEngineConfiguration, 7000, 200, true);
            fail("should throw time limit exceeded");
        } catch (FlowableException e) {
            assertEquals("Time limit of 7000 was exceeded", e.getMessage());
        }
        
        processEngine.getProcessEngineConfiguration().setClock(clock);
        
        timerJobs = processEngineManagementService.createTimerJobQuery().processInstanceId(processInstance.getId()).executable().list();
        assertEquals(1, timerJobs.size());
        assertEquals(timerJobId, timerJobs.get(0).getId());
        
        JobTestHelper.waitForJobExecutorToProcessAllJobs(processEngine.getProcessEngineConfiguration(), processEngineManagementService, 7000, 200, true);
        
        timerJobs = processEngineManagementService.createTimerJobQuery().processInstanceId(processInstance.getId()).list();
        assertEquals(0, timerJobs.size());
        
        Task task = processEngineTaskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        assertNotNull(task);
        assertEquals("secondTask", task.getTaskDefinitionKey());
        
        processEngineTaskService.complete(task.getId());
        
        assertEquals(0, processEngineRuntimeService.createProcessInstanceQuery().count());
        
        cmmnEngineConfiguration.resetClock();
        ((ProcessEngineConfigurationImpl) processEngine.getProcessEngineConfiguration()).resetClock();
    }
    
}
