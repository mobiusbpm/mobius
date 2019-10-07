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

package mobius.standalone.testing;

import static org.assertj.core.api.Assertions.assertThat;

import mobius.engine.ManagementService;
import mobius.engine.ProcessEngine;
import mobius.engine.ProcessEngines;
import mobius.engine.RepositoryService;
import mobius.engine.RuntimeService;
import mobius.engine.TaskService;
import mobius.engine.test.Deployment;
import mobius.engine.test.DeploymentId;
import mobius.engine.test.FlowableTest;
import mobius.engine.test.FlowableTestHelper;
import mobius.task.api.Task;
import org.junit.jupiter.api.Test;

/**
 * Test runners follow the this rule: - if the class extends Testcase, run as Junit 3 - otherwise use Junit 4, or JUnit 5
 * <p>
 * So this test can be included in the regular test suite without problems.
 *
 *
 */
@FlowableTest
class FlowableJupiterTest {

    @Test
    @Deployment
    void extensionUsageExample(ProcessEngine processEngine) {
        RuntimeService runtimeService = processEngine.getRuntimeService();
        runtimeService.startProcessInstanceByKey("extensionUsage");

        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery().singleResult();
        assertThat(task.getName()).isEqualTo("My Task");

        taskService.complete(task.getId());
        assertThat(runtimeService.createProcessInstanceQuery().count()).isEqualTo(0);
        assertThat(processEngine.getName()).as("process engine  name").isEqualTo(ProcessEngines.NAME_DEFAULT);
    }

    @Test
    @Deployment(resources = "mobius/standalone/testing/FlowableJupiterTest.extensionUsageExample.bpmn20.xml")
    void extensionUsageDeploymentIdExample(@DeploymentId String deploymentId, FlowableTestHelper testHelper, RepositoryService repositoryService) {
        assertThat(deploymentId).as("deploymentId parameter").isEqualTo(testHelper.getDeploymentIdFromDeploymentAnnotation());

        mobius.engine.repository.Deployment deployment = repositoryService.createDeploymentQuery().singleResult();

        assertThat(deployment.getId()).as("queried deployment").isEqualTo(deploymentId);
    }

    // this is to show how JobTestHelper could be used to wait for jobs to be all processed
    @Test
    @Deployment(resources = { "mobius/engine/test/bpmn/async/AsyncTaskTest.testAsyncTask.bpmn20.xml" })
    void testWaitForJobs(FlowableTestHelper testHelper, RuntimeService runtimeService, ManagementService managementService) {
        // start process
        runtimeService.startProcessInstanceByKey("asyncTask");

        // now there should be one job in the database:
        assertThat(managementService.createJobQuery().count()).isEqualTo(1);

        testHelper.waitForJobExecutorToProcessAllJobs(7000L, 500L);

        // the job is done
        assertThat(managementService.createJobQuery().count()).isEqualTo(0);
    }
}
