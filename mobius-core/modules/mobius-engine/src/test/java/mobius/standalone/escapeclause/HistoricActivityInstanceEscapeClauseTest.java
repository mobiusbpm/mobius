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
package mobius.standalone.escapeclause;

import mobius.engine.history.HistoricActivityInstanceQuery;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HistoricActivityInstanceEscapeClauseTest extends AbstractEscapeClauseTestCase {

    private String deploymentOneId;

    private String deploymentTwoId;

    @BeforeEach
    protected void setUp() throws Exception {
        deploymentOneId = repositoryService
                .createDeployment()
                .tenantId("One%")
                .addClasspathResource("mobius/engine/test/history/HistoricActivityInstanceTest.testHistoricActivityInstanceQuery.bpmn20.xml")
                .deploy()
                .getId();

        deploymentTwoId = repositoryService
                .createDeployment()
                .tenantId("Two_")
                .addClasspathResource("mobius/engine/test/history/HistoricActivityInstanceTest.testHistoricActivityInstanceQuery.bpmn20.xml")
                .deploy()
                .getId();

    }

    @AfterEach
    protected void tearDown() throws Exception {
        repositoryService.deleteDeployment(deploymentOneId, true);
        repositoryService.deleteDeployment(deploymentTwoId, true);
    }

    @Test
    public void testQueryByTenantIdLike() {
        runtimeService.startProcessInstanceByKeyAndTenantId("noopProcess", "One%");
        runtimeService.startProcessInstanceByKeyAndTenantId("noopProcess", "Two_");

        HistoricActivityInstanceQuery query = historyService.createHistoricActivityInstanceQuery().activityId("noop").activityTenantIdLike("%\\%%");
        assertEquals("One%", query.singleResult().getTenantId());
        assertEquals(1, query.list().size());
        assertEquals(1, query.count());

        query = historyService.createHistoricActivityInstanceQuery().activityId("noop").activityTenantIdLike("%\\_%");
        assertEquals("Two_", query.singleResult().getTenantId());
        assertEquals(1, query.list().size());
        assertEquals(1, query.count());
    }
}