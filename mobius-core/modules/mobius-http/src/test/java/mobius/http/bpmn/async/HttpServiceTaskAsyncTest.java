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
package mobius.http.bpmn.async;

import java.util.List;

import mobius.engine.runtime.ProcessInstance;
import mobius.engine.test.Deployment;
import mobius.http.bpmn.HttpServiceTaskTestCase;
import mobius.job.api.Job;
import org.junit.jupiter.api.Test;

/**
 * @author Harsha Teja Kanna
 */
public class HttpServiceTaskAsyncTest extends HttpServiceTaskTestCase {

    @Test
    @Deployment
    public void testAsyncSimpleGetOnly() {
        String procId = runtimeService.startProcessInstanceByKey("asyncSimpleGetOnly").getId();

        waitForJobExecutorToProcessAllJobsAndExecutableTimerJobs(20000L, 2000L);

        assertProcessEnded(procId);
        assertEquals(0, managementService.createJobQuery().count());
    }

    @Test
    @Deployment
    public void testFailedJobRetryTimeCycle() {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("asyncFailedJobRetryTimeCycle");

        List<Job> jobs = managementService.createJobQuery()
                .processInstanceId(processInstance.getId()).list();
        assertEquals(1, jobs.size());

        waitForJobExecutorToProcessAllJobsAndExecutableTimerJobs(20000L, 3000L);

        assertEquals(0, managementService.createJobQuery().count());
    }

}
