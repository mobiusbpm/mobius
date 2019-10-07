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
package mobius.engine.test.bpmn.servicetask;

import java.util.List;

import mobius.engine.impl.test.PluggableFlowableTestCase;
import mobius.engine.repository.ProcessDefinition;
import mobius.engine.runtime.ProcessInstance;
import mobius.engine.test.Deployment;
import mobius.idm.api.Group;
import mobius.idm.api.User;
import org.junit.jupiter.api.Test;

/**
 *
 */
public class CallServiceInServiceTaskTest extends PluggableFlowableTestCase {

    @Test
    @Deployment
    public void testStartProcessFromDelegate() {
        runtimeService.startProcessInstanceByKey("startProcessFromDelegate");

        // Starting the process should lead to two processes being started,
        // The other one started from the java delegate in the service task
        List<ProcessInstance> processInstances = runtimeService.createProcessInstanceQuery().list();
        assertEquals(2, processInstances.size());

        boolean startProcessFromDelegateFound = false;
        boolean oneTaskProcessFound = false;
        for (ProcessInstance processInstance : processInstances) {
            ProcessDefinition processDefinition = repositoryService.getProcessDefinition(processInstance.getProcessDefinitionId());
            if (processDefinition.getKey().equals("startProcessFromDelegate")) {
                startProcessFromDelegateFound = true;
            } else if (processDefinition.getKey().equals("oneTaskProcess")) {
                oneTaskProcessFound = true;
            }
        }

        assertTrue(startProcessFromDelegateFound);
        assertTrue(oneTaskProcessFound);
    }

    @Test
    @Deployment
    public void testRollBackOnException() {
        Exception expectedException = null;
        try {
            runtimeService.startProcessInstanceByKey("startProcessFromDelegate");
            fail("expected exception");
        } catch (Exception e) {
            expectedException = e;
        }
        assertNotNull(expectedException);

        // Starting the process should cause a rollback of both processes
        assertEquals(0, runtimeService.createProcessInstanceQuery().count());
    }

    @Test
    @Deployment
    public void testMultipleServiceInvocationsFromDelegate() {
        runtimeService.startProcessInstanceByKey("multipleServiceInvocations");

        // The service task should have created a user which is part of the admin group
        User user = identityService.createUserQuery().singleResult();
        assertEquals("Kermit", user.getId());
        Group group = identityService.createGroupQuery().groupMember(user.getId()).singleResult();
        assertNotNull(group);
        assertEquals("admin", group.getId());

        // Cleanup
        identityService.deleteUser("Kermit");
        identityService.deleteGroup("admin");
        identityService.deleteMembership("Kermit", "admin");
    }

}
