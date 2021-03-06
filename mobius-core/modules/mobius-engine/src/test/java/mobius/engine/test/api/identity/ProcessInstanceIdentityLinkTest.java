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
package mobius.engine.test.api.identity;

import mobius.engine.impl.test.PluggableFlowableTestCase;
import mobius.engine.runtime.ProcessInstance;
import mobius.engine.test.Deployment;
import mobius.task.api.Task;
import org.junit.jupiter.api.Test;

/**
 *
 */
public class ProcessInstanceIdentityLinkTest extends PluggableFlowableTestCase {

    // Test specific for fix introduced by
    // https://jira.codehaus.org/browse/ACT-1591
    // (Referential integrity constraint violation on PROC_INST and
    // IDENTITY_LINK)
    @Test
    @Deployment
    public void testSetAuthenticatedUserAndCompleteLastTask() {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("identityLinktest");

        // There are two tasks

        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        taskService.complete(task.getId());

        identityService.setAuthenticatedUserId("kermit");
        task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        taskService.complete(task.getId());
        identityService.setAuthenticatedUserId(null);

        assertProcessEnded(processInstance.getId());

    }

    // Test specific for fix introduced by
    // https://jira.codehaus.org/browse/ACT-1591
    // (Referential integrity constraint violation on PROC_INST and
    // IDENTITY_LINK)
    @Test
    @Deployment
    public void testSetAuthenticatedUserWithNoWaitStates() {
        identityService.setAuthenticatedUserId("kermit");

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("identityLinktest");
        assertProcessEnded(processInstance.getId());

        identityService.setAuthenticatedUserId(null);
    }

}
