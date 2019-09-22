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
package mobius.engine.test.debugger;

import mobius.engine.impl.agenda.DebugContinueProcessOperation;
import mobius.engine.impl.agenda.DebugFlowableEngineAgenda;
import mobius.engine.impl.test.JobTestHelper;
import mobius.engine.impl.test.ResourceFlowableTestCase;
import mobius.engine.runtime.ProcessDebugger;
import mobius.engine.runtime.ProcessInstance;
import mobius.engine.test.Deployment;
import mobius.job.api.Job;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * This class tests {@link DebugContinueProcessOperation}, {@link ProcessDebugger} and {@link DebugFlowableEngineAgenda}
 * implementation
 *
 * @author martin.grofcik
 */
public class DebugProcessOperationTest extends ResourceFlowableTestCase {

    public DebugProcessOperationTest() {
        super("/mobius/engine/impl/agenda/DebugProcessOperationTest.flowable.cfg.xml");
    }

    @Test
    @Deployment(resources = "mobius/engine/test/api/oneTaskProcess.bpmn20.xml")
    public void testDebugOneTaskProcess() {
        ProcessInstance oneTaskProcess = this.runtimeService.startProcessInstanceByKey("oneTaskProcess");

        assertProcessActivityId("The execution must stop on the start node.", oneTaskProcess, "theStart");
        triggerBreakPoint();

        assertProcessActivityId("The execution must stop on the user task node before it's execution.", oneTaskProcess, "theTask");
        triggerBreakPoint();

        assertEquals("User task has to be created.", 1, this.taskService.createTaskQuery().count());
        assertProcessActivityId("The execution is still on the user task.", oneTaskProcess, "theTask");
        String taskId = this.taskService.createTaskQuery().processInstanceId(oneTaskProcess.getProcessInstanceId()).singleResult().getId();
        this.taskService.complete(taskId);

        assertProcessActivityId("The execution must stop on the end event.", oneTaskProcess, "theEnd");
        triggerBreakPoint();

        assertThat("No process instance is running.", this.runtimeService.createExecutionQuery().count(), is(0L));
    }

    @Test
    @Deployment(resources = "mobius/engine/impl/agenda/oneFailureScriptTask.bpmn20.xml")
    public void testDebuggerExecutionFailure() {
        ProcessInstance oneTaskProcess = this.runtimeService.startProcessInstanceByKey("oneTaskFailingProcess");

        assertProcessActivityId("The execution must stop on the start node.", oneTaskProcess, "theStart");
        triggerBreakPoint();

        assertProcessActivityId("The execution must stop on the user task node before it's execution.", oneTaskProcess, "theTask");
        Job job = managementService.createSuspendedJobQuery().handlerType("breakpoint").singleResult();
        assertNotNull(job);
        managementService.moveSuspendedJobToExecutableJob(job.getId());
        JobTestHelper.waitForJobExecutorToProcessAllJobs(this.processEngineConfiguration, this.managementService, 10000, 500);
        Job updatedJob = managementService.createSuspendedJobQuery().handlerType("breakpoint").singleResult();
        assertNotNull("Triggering breakpoint and failure must reassign breakpoint to suspended jobs again", updatedJob);
    }

    protected void triggerBreakPoint() {
        Job job = managementService.createSuspendedJobQuery().handlerType("breakpoint").singleResult();
        assertNotNull(job);
        Job activatedJob = managementService.moveSuspendedJobToExecutableJob(job.getId());
        managementService.executeJob(activatedJob.getId());
    }

    protected void assertProcessActivityId(String message, ProcessInstance process, String activityId) {
        assertThat(message, this.runtimeService.createExecutionQuery().parentId(process.getId()).singleResult().getActivityId(),
                is(activityId));
    }

}
