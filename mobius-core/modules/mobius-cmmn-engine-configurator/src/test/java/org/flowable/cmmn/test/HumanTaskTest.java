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
package mobius.cmmn.test;

import mobius.cmmn.api.runtime.CaseInstance;
import mobius.cmmn.engine.test.CmmnDeployment;
import mobius.common.engine.impl.interceptor.EngineConfigurationConstants;
import mobius.form.api.FormRepositoryService;
import mobius.form.engine.FormEngineConfiguration;
import mobius.task.api.Task;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author martin.grofcik
 */
public class HumanTaskTest extends mobius.cmmn.test.AbstractProcessEngineIntegrationTest {

    protected FormRepositoryService formRepositoryService;

    @Before
    public void setup() {
        super.setupServices();
        FormEngineConfiguration formEngineConfiguration = (FormEngineConfiguration) processEngine.getProcessEngineConfiguration()
            .getEngineConfigurations().get(EngineConfigurationConstants.KEY_FORM_ENGINE_CONFIG);
        this.formRepositoryService = formEngineConfiguration.getFormRepositoryService();

        formRepositoryService.createDeployment().addClasspathResource("mobius/cmmn/test/simple.form").deploy();
    }

    @After
    public void deleteFormDeployment() {
        this.formRepositoryService.createDeploymentQuery().list().forEach(
            formDeployment -> formRepositoryService.deleteDeployment(formDeployment.getId())
        );
    }

    @Test
    @CmmnDeployment(resources = "mobius/cmmn/test/CaseTaskTest.testCaseTask.cmmn")
    public void completeHumanTaskWithoutVariables() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().
            caseDefinitionKey("myCase").
            start();
        assertNotNull(caseInstance);

        Task caseTask = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        assertNotNull(caseTask);

        cmmnTaskService.completeTaskWithForm(caseTask.getId(), formRepositoryService.createFormDefinitionQuery().formDefinitionKey("form1").singleResult().getId(),
            "__COMPLETE", null);

        CaseInstance dbCaseInstance = cmmnRuntimeService.createCaseInstanceQuery().caseInstanceId(caseInstance.getId()).singleResult();
        assertNull(dbCaseInstance);
    }

}
