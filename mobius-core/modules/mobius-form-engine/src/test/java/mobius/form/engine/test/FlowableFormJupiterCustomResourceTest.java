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

import mobius.form.engine.FormEngine;
import mobius.form.engine.test.FlowableFormExtension;
import mobius.form.engine.test.FormConfigurationResource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 *
 */
@ExtendWith(FlowableFormExtension.class)
@FormConfigurationResource("flowable.custom.form.cfg.xml")
class FlowableFormJupiterCustomResourceTest {

    @Test
    void customResourceUsage(FormEngine formEngine) {
        assertThat(formEngine.getName()).as("form engine name").isEqualTo("customName");
    }
}
