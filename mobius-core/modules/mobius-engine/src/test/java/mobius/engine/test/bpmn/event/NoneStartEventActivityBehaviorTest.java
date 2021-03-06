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
package mobius.engine.test.bpmn.event;

import static org.assertj.core.api.Assertions.assertThat;

import mobius.engine.impl.test.JobTestHelper;
import mobius.engine.impl.test.PluggableFlowableTestCase;
import mobius.engine.runtime.ProcessInstance;
import mobius.engine.test.Deployment;
import mobius.job.api.Job;
import org.junit.jupiter.api.Test;

class NoneStartEventActivityBehaviorTest extends PluggableFlowableTestCase {

    @Test
    @Deployment
    public void asyncStartEvent() {
        ProcessInstance pi = runtimeService.startProcessInstanceByKey("asyncNoneStartEvent");

        assertThat(runtimeService.createProcessInstanceQuery().processInstanceId(pi.getId()).count()).isEqualTo(1l);
        Job job = managementService.createJobQuery().processInstanceId(pi.getId()).singleResult();
        assertThat(job).isNotNull();
        assertThat(runtimeService.createExecutionQuery().processInstanceId(pi.getId()).onlyChildExecutions().singleResult().getActivityId()).isEqualTo("theStart");

        JobTestHelper.waitForJobExecutorToProcessAllJobs(processEngineConfiguration, managementService, 2000, 200);
        assertProcessEnded(pi.getProcessInstanceId());
    }

}
