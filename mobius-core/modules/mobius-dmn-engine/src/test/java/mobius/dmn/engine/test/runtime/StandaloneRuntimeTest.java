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
package mobius.dmn.engine.test.runtime;

import java.util.HashMap;
import java.util.Map;

import mobius.dmn.api.DmnRuleService;
import mobius.dmn.engine.DmnEngine;
import mobius.dmn.engine.test.DmnDeployment;
import mobius.dmn.engine.test.FlowableDmnRule;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Yvo Swillens
 */
public class StandaloneRuntimeTest {

    @Rule
    public FlowableDmnRule flowableDmnRule = new FlowableDmnRule();

    @Test
    @DmnDeployment
    public void ruleUsageExample() {
        DmnEngine dmnEngine = flowableDmnRule.getDmnEngine();
        DmnRuleService dmnRuleService = dmnEngine.getDmnRuleService();

        Map<String, Object> inputVariables = new HashMap<>();
        inputVariables.put("inputVariable1", 2);
        inputVariables.put("inputVariable2", "test2");

        Map<String, Object> result = dmnRuleService.createExecuteDecisionBuilder()
                .decisionKey("decision1")
                .variables(inputVariables)
                .executeWithSingleResult();
        
        Assert.assertEquals("result2", result.get("outputVariable1"));
    }
}
