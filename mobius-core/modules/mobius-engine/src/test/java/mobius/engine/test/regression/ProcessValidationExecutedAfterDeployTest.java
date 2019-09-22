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
package mobius.engine.test.regression;

import java.util.List;

import mobius.common.engine.api.FlowableException;
import mobius.engine.impl.test.PluggableFlowableTestCase;
import mobius.engine.repository.ProcessDefinition;
import mobius.validation.ProcessValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * From http://forums.activiti.org/content/skip-parse-validation-while-fetching- startformdata
 * 
 * Test for validating that the process validator ONLY kicks in on deployment, not on reading again from database. The two tests should fail, cause the validator kicks in the second time, but not
 * originally (don't do this at home, kids. Disabling the validator on deploy is BAD).
 */
public class ProcessValidationExecutedAfterDeployTest extends PluggableFlowableTestCase {

    protected ProcessValidator processValidator;
    protected ProcessValidator initialProcessValidator;

    private void disableValidation() {
        processValidator = processEngineConfiguration.getProcessValidator();
        processEngineConfiguration.setProcessValidator(null);
    }

    private void enableValidation() {
        processEngineConfiguration.setProcessValidator(processValidator);
    }

    private void clearDeploymentCache() {
        processEngineConfiguration.getProcessDefinitionCache().clear();
    }

    @BeforeEach
    public void setUp() {
        // We need to make sure that we have the initial validator before we run the tests
        initialProcessValidator = processEngineConfiguration.getProcessValidator();
    }

    @AfterEach
    protected void tearDown() throws Exception {
        // Set the initial validator at the end of the tests
        processEngineConfiguration.setProcessValidator(initialProcessValidator);
    }

    private ProcessDefinition getLatestProcessDefinitionVersionByKey(String processDefinitionKey) {
        List<ProcessDefinition> definitions = null;
        try {
            definitions = repositoryService.createProcessDefinitionQuery().processDefinitionKey(processDefinitionKey).orderByProcessDefinitionVersion().latestVersion().desc().list();
            if (definitions.isEmpty()) {
                return null;
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
        return definitions.get(0);
    }

    @Test
    public void testGetLatestProcessDefinitionTextByKey() {

        disableValidation();
        repositoryService.createDeployment().addClasspathResource("mobius/engine/test/regression/ProcessValidationExecutedAfterDeployTest.bpmn20.xml").deploy();
        enableValidation();
        clearDeploymentCache();

        ProcessDefinition definition = getLatestProcessDefinitionVersionByKey("testProcess1");
        if (definition == null) {
            fail("Error occurred in fetching process model.");
        }
        try {
            repositoryService.getProcessModel(definition.getId());
            assertTrue(true);
        } catch (FlowableException e) {
            fail("Error occurred in fetching process model.");
        }

        for (mobius.engine.repository.Deployment deployment : repositoryService.createDeploymentQuery().list()) {
            repositoryService.deleteDeployment(deployment.getId());
        }
    }

    @Test
    public void testGetStartFormData() {

        disableValidation();
        repositoryService.createDeployment().addClasspathResource("mobius/engine/test/regression/ProcessValidationExecutedAfterDeployTest.bpmn20.xml").deploy();
        enableValidation();
        clearDeploymentCache();

        ProcessDefinition definition = getLatestProcessDefinitionVersionByKey("testProcess1");
        if (definition == null) {
            fail("Error occurred in fetching process model.");
        }
        try {
            formService.getStartFormData(definition.getId());
            assertTrue(true);
        } catch (FlowableException e) {
            fail("Error occurred in fetching start form data:");
        }

        for (mobius.engine.repository.Deployment deployment : repositoryService.createDeploymentQuery().list()) {
            repositoryService.deleteDeployment(deployment.getId());
        }
    }
}
