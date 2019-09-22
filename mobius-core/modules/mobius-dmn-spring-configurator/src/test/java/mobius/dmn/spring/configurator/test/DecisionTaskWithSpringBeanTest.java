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
package mobius.dmn.spring.configurator.test;

import mobius.common.engine.impl.interceptor.EngineConfigurationConstants;
import mobius.dmn.api.DmnDeployment;
import mobius.dmn.engine.DmnEngineConfiguration;
import mobius.engine.runtime.ProcessInstance;
import mobius.engine.test.Deployment;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tijs Rademakers
 */
@ContextConfiguration("classpath:flowable-context.xml")
public class DecisionTaskWithSpringBeanTest extends SpringDmnFlowableTestCase {

    @Test
    @Deployment(resources = { "mobius/dmn/spring/configurator/test/oneDecisionTaskProcess.bpmn20.xml",
        "mobius/dmn/spring/configurator/test/simple.dmn" })
    public void testDecisionTask() {
        
        DmnEngineConfiguration dmnEngineConfiguration = (DmnEngineConfiguration) processEngineConfiguration.getEngineConfigurations()
                        .get(EngineConfigurationConstants.KEY_DMN_ENGINE_CONFIG);
        
        DmnDeployment dmnDeployment = dmnEngineConfiguration.getDmnRepositoryService().createDeploymentQuery().singleResult();
        assertNotNull(dmnDeployment);
        
        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("input1", "testBla");
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("oneDecisionTaskProcess", variables);
    
            assertProcessEnded(processInstance.getId());
            
        } finally {
            dmnEngineConfiguration.getDmnRepositoryService().deleteDeployment(dmnDeployment.getId());
        }
    }

}