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

import mobius.dmn.engine.DmnEngine;
import mobius.dmn.engine.test.DmnConfigurationResource;
import mobius.dmn.engine.test.FlowableDmnTest;
import org.junit.jupiter.api.Test;

/**
 * Test runners follow this rule: - if the class extends Testcase, run as Junit 3 - otherwise use Junit 4, or JUnit 5
 * <p>
 * So this test can be included in the regular test suite without problems.
 *
 *
 */
@FlowableDmnTest
@DmnConfigurationResource("flowable.custom.dmn.cfg.xml")
class FlowableDmnJupiterCustomResourceTest {

    @Test
    void customResourceUsages(DmnEngine dmnEngine) {
        assertThat(dmnEngine.getName()).as("dmn engine name").isEqualTo("customName");
    }
}
