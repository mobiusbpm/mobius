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

package mobius.spring.test.el;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import mobius.cmmn.api.CmmnRuntimeService;
import mobius.cmmn.api.repository.CmmnDeployment;
import mobius.cmmn.api.runtime.CaseInstance;
import mobius.cmmn.api.runtime.PlanItemInstance;
import mobius.cmmn.api.runtime.PlanItemInstanceState;
import mobius.cmmn.engine.test.FlowableCmmnRule;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

/**
 *
 */
public class SpringCustomBeanTest {

    @Rule
    public FlowableCmmnRule cmmnRule = new FlowableCmmnRule("mobius/spring/test/el/SpringBeanTest-context.xml");

    @Test
    public void testSimpleCaseBean() {
        cmmnRule.getCmmnRepositoryService().createDeployment().addClasspathResource("mobius/spring/test/el/springExpression.cmmn").deploy();
        
        try {
            CmmnRuntimeService cmmnRuntimeService = cmmnRule.getCmmnRuntimeService();
            CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder()
                            .caseDefinitionKey("myCase")
                            .variable("var1", "John Doe")
                            .start();
            
            Assert.assertNotNull(caseInstance);
            
            PlanItemInstance planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery()
                            .caseInstanceId(caseInstance.getId())
                            .planItemInstanceState(PlanItemInstanceState.ACTIVE)
                            .singleResult();
            assertNotNull(planItemInstance);
            
            assertEquals("hello John Doe", cmmnRuntimeService.getVariable(caseInstance.getId(), "customResponse"));
            
            // Triggering the task should start the child case instance
            cmmnRuntimeService.triggerPlanItemInstance(planItemInstance.getId());
            assertEquals(0, cmmnRuntimeService.createCaseInstanceQuery().count());
            
            assertEquals("hello John Doe", cmmnRule.getCmmnHistoryService().createHistoricVariableInstanceQuery()
                            .caseInstanceId(caseInstance.getId())
                            .variableName("customResponse")
                            .singleResult().getValue());
        } finally {
            removeAllDeployments();
        }
    }

    // --Helper methods
    // ----------------------------------------------------------

    private void removeAllDeployments() {
        for (CmmnDeployment deployment : cmmnRule.getCmmnRepositoryService().createDeploymentQuery().list()) {
            cmmnRule.getCmmnRepositoryService().deleteDeployment(deployment.getId(), true);
        }
    }

}