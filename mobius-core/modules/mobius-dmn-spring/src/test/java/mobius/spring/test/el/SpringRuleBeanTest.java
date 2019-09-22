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

import java.util.Map;

import mobius.dmn.api.DmnDeployment;
import mobius.dmn.api.DmnRepositoryService;
import mobius.dmn.api.DmnRuleService;
import mobius.dmn.engine.impl.test.AbstractDmnTestCase;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Tijs Rademakers
 */
public class SpringRuleBeanTest extends AbstractDmnTestCase {

    protected static final String CTX_PATH = "mobius/spring/test/el/SpringBeanTest-context.xml";

    protected ApplicationContext applicationContext;
    protected DmnRepositoryService repositoryService;
    protected DmnRuleService ruleService;

    protected void createAppContext(String path) {
        this.applicationContext = new ClassPathXmlApplicationContext(path);
        this.repositoryService = applicationContext.getBean(DmnRepositoryService.class);
        this.ruleService = applicationContext.getBean(DmnRuleService.class);
    }

    @Override
    protected void tearDown() throws Exception {
        removeAllDeployments();
        this.applicationContext = null;
        this.repositoryService = null;
        this.ruleService = null;
        super.tearDown();
    }

    public void testSimpleRuleBean() {
        createAppContext(CTX_PATH);
        repositoryService.createDeployment().addClasspathResource("mobius/spring/test/el/springbean.dmn").deploy();
        
        Map<String, Object> outputVariables = ruleService.createExecuteDecisionBuilder()
                        .decisionKey("springDecision")
                        .variable("input1", "John Doe")
                        .executeWithSingleResult();
        
        assertEquals("test1", outputVariables.get("output1"));
        
        outputVariables = ruleService.createExecuteDecisionBuilder()
                        .decisionKey("springDecision")
                        .variable("input1", "test")
                        .executeWithSingleResult();
        
        assertEquals("test2", outputVariables.get("output1"));
    }

    // --Helper methods
    // ----------------------------------------------------------

    private void removeAllDeployments() {
        for (DmnDeployment deployment : repositoryService.createDeploymentQuery().list()) {
            repositoryService.deleteDeployment(deployment.getId());
        }
    }

}