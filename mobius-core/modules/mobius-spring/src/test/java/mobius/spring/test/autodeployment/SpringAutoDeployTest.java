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

package mobius.spring.test.autodeployment;

import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mobius.common.engine.impl.util.IoUtil;
import mobius.engine.RepositoryService;
import mobius.engine.impl.test.AbstractTestCase;
import mobius.engine.repository.Deployment;
import mobius.engine.repository.DeploymentQuery;
import mobius.engine.repository.ProcessDefinition;
import mobius.engine.repository.ProcessDefinitionQuery;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 *
 */
public class SpringAutoDeployTest extends AbstractTestCase {

    protected static final String CTX_PATH = "mobius/spring/test/autodeployment/SpringAutoDeployTest-context.xml";
    protected static final String CTX_NO_DROP_PATH = "mobius/spring/test/autodeployment/SpringAutoDeployTest-no-drop-context.xml";
    protected static final String CTX_CREATE_DROP_CLEAN_DB = "mobius/spring/test/autodeployment/SpringAutoDeployTest-create-drop-clean-db-context.xml";
    protected static final String CTX_DEPLOYMENT_MODE_DEFAULT = "mobius/spring/test/autodeployment/SpringAutoDeployTest-deploymentmode-default-context.xml";
    protected static final String CTX_DEPLOYMENT_MODE_SINGLE_RESOURCE = "mobius/spring/test/autodeployment/SpringAutoDeployTest-deploymentmode-single-resource-context.xml";
    protected static final String CTX_DEPLOYMENT_MODE_RESOURCE_PARENT_FOLDER = "mobius/spring/test/autodeployment/SpringAutoDeployTest-deploymentmode-resource-parent-folder-context.xml";

    protected ApplicationContext applicationContext;
    protected RepositoryService repositoryService;

    protected void createAppContext(String path) {
        this.applicationContext = new ClassPathXmlApplicationContext(path);
        this.repositoryService = applicationContext.getBean(RepositoryService.class);
    }

    @AfterEach
    protected void tearDown() throws Exception {
        removeAllDeployments();
        this.applicationContext = null;
        this.repositoryService = null;
    }

    @Test
    public void testBasicActivitiSpringIntegration() {
        createAppContext("mobius/spring/test/autodeployment/SpringAutoDeployTest-context.xml");
        List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery().list();

        Set<String> processDefinitionKeys = new HashSet<>();
        for (ProcessDefinition processDefinition : processDefinitions) {
            processDefinitionKeys.add(processDefinition.getKey());
        }

        Set<String> expectedProcessDefinitionKeys = new HashSet<>();
        expectedProcessDefinitionKeys.add("a");
        expectedProcessDefinitionKeys.add("b");
        expectedProcessDefinitionKeys.add("c");

        assertEquals(expectedProcessDefinitionKeys, processDefinitionKeys);
    }

    @Test
    public void testNoRedeploymentForSpringContainerRestart() throws Exception {
        createAppContext(CTX_PATH);
        DeploymentQuery deploymentQuery = repositoryService.createDeploymentQuery();
        assertEquals(1, deploymentQuery.count());
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        assertEquals(3, processDefinitionQuery.count());

        // Creating a new app context with same resources doesn't lead to more
        // deployments
        new ClassPathXmlApplicationContext(CTX_NO_DROP_PATH);
        assertEquals(1, deploymentQuery.count());
        assertEquals(3, processDefinitionQuery.count());
    }

    // Updating the bpmn20 file should lead to a new deployment when restarting
    // the Spring container
    @Test
    public void testResourceRedeploymentAfterProcessDefinitionChange() throws Exception {
        createAppContext(CTX_PATH);
        assertEquals(1, repositoryService.createDeploymentQuery().count());
        ((AbstractXmlApplicationContext) applicationContext).destroy();

        String filePath = "mobius/spring/test/autodeployment/autodeploy.a.bpmn20.xml";
        String originalBpmnFileContent = IoUtil.readFileAsString(filePath);
        String updatedBpmnFileContent = originalBpmnFileContent.replace("flow1", "fromStartToEndFlow");
        assertTrue(updatedBpmnFileContent.length() > originalBpmnFileContent.length());
        IoUtil.writeStringToFile(updatedBpmnFileContent, filePath);

        // Classic produced/consumer problem here:
        // The file is already written in Java, but not yet completely persisted
        // by
        // the OS
        // Constructing the new app context reads the same file which is
        // sometimes
        // not yet fully written to disk
        waitUntilFileIsWritten(filePath, updatedBpmnFileContent.length());

        try {
            applicationContext = new ClassPathXmlApplicationContext(CTX_NO_DROP_PATH);
            repositoryService = (RepositoryService) applicationContext.getBean("repositoryService");
        } finally {
            // Reset file content such that future test are not seeing something
            // funny
            IoUtil.writeStringToFile(originalBpmnFileContent, filePath);
        }

        // Assertions come AFTER the file write! Otherwise the process file is
        // messed up if the assertions fail.
        assertEquals(2, repositoryService.createDeploymentQuery().count());
        assertEquals(6, repositoryService.createProcessDefinitionQuery().count());
    }

    @Test
    public void testAutoDeployWithCreateDropOnCleanDb() {
        createAppContext(CTX_CREATE_DROP_CLEAN_DB);
        assertEquals(1, repositoryService.createDeploymentQuery().count());
        assertEquals(3, repositoryService.createProcessDefinitionQuery().count());
    }

    @Test
    public void testAutoDeployWithDeploymentModeDefault() {
        createAppContext(CTX_DEPLOYMENT_MODE_DEFAULT);
        assertEquals(1, repositoryService.createDeploymentQuery().count());
        assertEquals(3, repositoryService.createProcessDefinitionQuery().count());
    }

    @Test
    public void testAutoDeployWithDeploymentModeSingleResource() {
        createAppContext(CTX_DEPLOYMENT_MODE_SINGLE_RESOURCE);
        assertEquals(3, repositoryService.createDeploymentQuery().count());
        assertEquals(3, repositoryService.createProcessDefinitionQuery().count());
    }

    @Test
    public void testAutoDeployWithDeploymentModeResourceParentFolder() {
        createAppContext(CTX_DEPLOYMENT_MODE_RESOURCE_PARENT_FOLDER);
        assertEquals(2, repositoryService.createDeploymentQuery().count());
        assertEquals(4, repositoryService.createProcessDefinitionQuery().count());
    }

    // --Helper methods
    // ----------------------------------------------------------

    private void removeAllDeployments() {
        for (Deployment deployment : repositoryService.createDeploymentQuery().list()) {
            repositoryService.deleteDeployment(deployment.getId(), true);
        }
    }

    private boolean waitUntilFileIsWritten(String filePath, int expectedBytes) throws URISyntaxException {
        while (IoUtil.getFile(filePath).length() != (long) expectedBytes) {
            try {
                wait(100L);
            } catch (InterruptedException e) {
                fail(e.getMessage());
            }
        }
        return true;
    }

}
