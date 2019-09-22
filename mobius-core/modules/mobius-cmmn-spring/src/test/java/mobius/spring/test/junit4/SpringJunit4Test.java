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
package mobius.spring.test.junit4;

import static org.junit.Assert.assertNotNull;

import mobius.cmmn.api.runtime.CaseInstance;
import mobius.cmmn.engine.test.CmmnDeployment;
import mobius.cmmn.engine.test.FlowableCmmnRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Tijs Rademakers
 */
public class SpringJunit4Test {
    
    @Rule
    public FlowableCmmnRule cmmnRule = new FlowableCmmnRule("mobius/spring/test/junit4/springTypicalUsageTest-context.xml");

    @Test
    @CmmnDeployment
    public void simpleCaseTest() {
        CaseInstance caseInstance = cmmnRule.getCmmnRuntimeService().createCaseInstanceBuilder().caseDefinitionKey("junitCase").start();
        assertNotNull(caseInstance);
    }
}
