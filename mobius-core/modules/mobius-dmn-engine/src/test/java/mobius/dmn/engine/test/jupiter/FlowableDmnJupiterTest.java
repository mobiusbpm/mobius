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
package mobius.dmn.engine.test.jupiter;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import mobius.dmn.api.DmnRepositoryService;
import mobius.dmn.api.DmnRuleService;
import mobius.dmn.engine.DmnEngine;
import mobius.dmn.engine.DmnEngines;
import mobius.dmn.engine.test.DmnDeployment;
import mobius.dmn.engine.test.DmnDeploymentAnnotation;
import mobius.dmn.engine.test.DmnDeploymentId;
import mobius.dmn.engine.test.FlowableDmnTest;
import mobius.dmn.engine.test.FlowableDmnTestHelper;
import org.junit.jupiter.api.Test;

/**
 * Test runners follow this rule: - if the class extends Testcase, run as Junit 3 - otherwise use Junit 4, or JUnit 5
 * <p>
 * So this test can be included in the regular test suite without problems.
 *
 *
 */
@FlowableDmnTest
class FlowableDmnJupiterTest {

    @Test
    @DmnDeployment
    void extensionUsageExample(DmnEngine dmnEngine) {
        DmnRuleService dmnRuleService = dmnEngine.getDmnRuleService();

        Map<String, Object> inputVariables = new HashMap<>();
        inputVariables.put("inputVariable1", 2);
        inputVariables.put("inputVariable2", "test2");

        Map<String, Object> result = dmnRuleService.createExecuteDecisionBuilder()
            .decisionKey("extensionUsage")
            .variables(inputVariables)
            .executeWithSingleResult();

        assertThat(result)
            .containsEntry("outputVariable1", "result2");

        assertThat(dmnEngine.getName()).as("dmn engine name").isEqualTo(DmnEngines.NAME_DEFAULT);
    }

    @Test
    @DmnDeploymentAnnotation(resources = "mobius/dmn/engine/test/jupiter/FlowableDmnJupiterTest.extensionUsageExample.dmn")
    void extensionUsageExampleWithDmnDeploymentAnnotation(DmnRuleService dmnRuleService, DmnRepositoryService dmnRepositoryService,
        @DmnDeploymentId String deploymentId) {

        Map<String, Object> inputVariables = new HashMap<>();
        inputVariables.put("inputVariable1", 2);
        inputVariables.put("inputVariable2", "test2");

        Map<String, Object> result = dmnRuleService.createExecuteDecisionBuilder()
            .decisionKey("extensionUsage")
            .variables(inputVariables)
            .executeWithSingleResult();

        assertThat(result)
            .containsEntry("outputVariable1", "result2");

        mobius.dmn.api.DmnDeployment deployment = dmnRepositoryService.createDeploymentQuery().singleResult();

        assertThat(deployment.getId()).as("queried deployment").isEqualTo(deploymentId);
        assertThat(deployment.getName()).as("deployment name").isEqualTo("FlowableDmnJupiterTest.extensionUsageExampleWithDmnDeploymentAnnotation");
    }

    @Test
    @DmnDeployment(resources = "mobius/dmn/engine/test/jupiter/FlowableDmnJupiterTest.extensionUsageExample.dmn")
    void extensionUsageDmnDeploymentIdExample(@DmnDeploymentId String deploymentId, FlowableDmnTestHelper testHelper,
        DmnRepositoryService repositoryService) {
        assertThat(deploymentId).as("deploymentId parameter").isEqualTo(testHelper.getDeploymentIdFromDeploymentAnnotation());

        mobius.dmn.api.DmnDeployment deployment = repositoryService.createDeploymentQuery().singleResult();

        assertThat(deployment.getId()).as("queried deployment").isEqualTo(deploymentId);
        assertThat(deployment.getName()).as("deployment name").isEqualTo("FlowableDmnJupiterTest.extensionUsageDmnDeploymentIdExample");
    }
}
