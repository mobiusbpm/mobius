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
package mobius.form.engine.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import mobius.form.api.FormDefinition;
import mobius.form.api.FormDeployment;
import mobius.form.engine.test.FlowableFormRule;
import mobius.form.engine.test.FormDeploymentAnnotation;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 *
 */
public class FlowableFormRuleJunit4Test {

    @Rule
    public final FlowableFormRule formRule = new FlowableFormRule();

    @Before
    public void setUp() {
        FormDeployment formDeployment = formRule.getRepositoryService().createDeploymentQuery().singleResult();
        assertThat(formDeployment.getName()).startsWith("FlowableFormRuleJunit4Test.");
    }

    @Test
    @FormDeploymentAnnotation
    public void ruleUsageExample() {
        assertThat(formRule.getRepositoryService().createFormDefinitionQuery().list())
            .extracting(FormDefinition::getKey, FormDefinition::getName)
            .containsExactlyInAnyOrder(
                tuple("ruleUsageExample", "Form for rule usage example")
            );

        FormDeployment formDeployment = formRule.getRepositoryService().createDeploymentQuery().singleResult();
        assertThat(formDeployment.getName()).isEqualTo("FlowableFormRuleJunit4Test.ruleUsageExample");
    }

    @Test
    @FormDeploymentAnnotation(resources = {
        "mobius/form/engine/test/FlowableFormRuleJunit4Test.ruleUsageExample.form",
        "mobius/form/engine/test/example.form"
    })
    public void ruleUsageExampleWithDefinedResources() {
        assertThat(formRule.getRepositoryService().createFormDefinitionQuery().list())
            .extracting(FormDefinition::getKey, FormDefinition::getName)
            .containsExactlyInAnyOrder(
                tuple("ruleUsageExample", "Form for rule usage example"),
                tuple("simpleExample", "Form for example")
            );

        FormDeployment formDeployment = formRule.getRepositoryService().createDeploymentQuery().singleResult();
        assertThat(formDeployment.getName()).isEqualTo("FlowableFormRuleJunit4Test.ruleUsageExampleWithDefinedResources");
    }
}
