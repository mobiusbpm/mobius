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
package mobius.cmmn.test.repository;

import mobius.cmmn.api.repository.CaseDefinition;
import mobius.cmmn.engine.impl.persistence.entity.deploy.CaseDefinitionCacheEntry;
import mobius.cmmn.engine.test.CmmnDeployment;
import mobius.cmmn.engine.test.FlowableCmmnTestCase;
import mobius.cmmn.model.CmmnModel;
import mobius.cmmn.model.PlanItem;
import mobius.common.engine.impl.persistence.deploy.DefaultDeploymentCache;
import mobius.common.engine.impl.persistence.deploy.DeploymentCache;
import org.h2.util.IOUtils;
import org.junit.Test;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.Assert.*;

/**
 * @author Joram Barrez
 */
public class DeploymentTest extends FlowableCmmnTestCase {

    /**
     * Simplest test possible: deploy the simple-case.cmmn (from the cmmn-converter module) and see if 
     * - a deployment exists
     * - a resouce exists
     * - a case definition was created 
     * - that case definition is in the cache
     * - case definition properties set
     */
    @Test
    public void testCaseDefinitionDeployed() throws Exception {

        DeploymentCache<CaseDefinitionCacheEntry> caseDefinitionCache = cmmnEngineConfiguration.getCaseDefinitionCache();
        caseDefinitionCache.clear();

        String deploymentId = cmmnRepositoryService.createDeployment()
            .addClasspathResource("mobius/cmmn/test/repository/DeploymentTest.testCaseDefinitionDeployed.cmmn").deploy().getId();

        mobius.cmmn.api.repository.CmmnDeployment cmmnDeployment = cmmnRepositoryService.createDeploymentQuery().singleResult();
        assertNotNull(cmmnDeployment);
        
        List<String> resourceNames = cmmnRepositoryService.getDeploymentResourceNames(cmmnDeployment.getId());
        assertEquals(1, resourceNames.size());
        assertEquals("mobius/cmmn/test/repository/DeploymentTest.testCaseDefinitionDeployed.cmmn", resourceNames.get(0));
        
        InputStream inputStream = cmmnRepositoryService.getResourceAsStream(cmmnDeployment.getId(), resourceNames.get(0));
        assertNotNull(inputStream);
        inputStream.close();
        
        assertEquals(1, ((DefaultDeploymentCache<CaseDefinitionCacheEntry>) caseDefinitionCache).getAll().size());

        CaseDefinitionCacheEntry cachedCaseDefinition = ((DefaultDeploymentCache<CaseDefinitionCacheEntry>) caseDefinitionCache).getAll().iterator().next();
        assertNotNull(cachedCaseDefinition.getCase());
        assertNotNull(cachedCaseDefinition.getCmmnModel());
        assertNotNull(cachedCaseDefinition.getCaseDefinition());
        
        CaseDefinition caseDefinition = cachedCaseDefinition.getCaseDefinition();
        assertNotNull(caseDefinition.getId());
        assertNotNull(caseDefinition.getDeploymentId());
        assertNotNull(caseDefinition.getKey());
        assertNotNull(caseDefinition.getResourceName());
        assertTrue(caseDefinition.getVersion() > 0);
        
        caseDefinition = cmmnRepositoryService.createCaseDefinitionQuery().deploymentId(cmmnDeployment.getId()).singleResult();
        assertNotNull(caseDefinition.getId());
        assertNotNull(caseDefinition.getDeploymentId());
        assertNotNull(caseDefinition.getKey());
        assertNotNull(caseDefinition.getResourceName());
        assertEquals(1, caseDefinition.getVersion());
        
        CmmnModel cmmnModel = cmmnRepositoryService.getCmmnModel(caseDefinition.getId());
        assertNotNull(cmmnModel);
        
        // CmmnParser should have added behavior to plan items
        for (PlanItem planItem : cmmnModel.getPrimaryCase().getPlanModel().getPlanItems()) {
            assertNotNull(planItem.getBehavior());
        }

        cmmnRepositoryService.deleteDeployment(deploymentId, true);
    }
    
    @Test
    @CmmnDeployment
    public void testCaseDefinitionDI() throws Exception {
        mobius.cmmn.api.repository.CmmnDeployment cmmnDeployment = cmmnRepositoryService.createDeploymentQuery().singleResult();
        assertNotNull(cmmnDeployment);
        
        List<String> resourceNames = cmmnRepositoryService.getDeploymentResourceNames(cmmnDeployment.getId());
        assertEquals(2, resourceNames.size());
        
        String resourceName = "mobius/cmmn/test/repository/DeploymentTest.testCaseDefinitionDI.cmmn";
        String diagramResourceName = "mobius/cmmn/test/repository/DeploymentTest.testCaseDefinitionDI.caseB.png";
        assertTrue(resourceNames.contains(resourceName));
        assertTrue(resourceNames.contains(diagramResourceName));
        
        InputStream inputStream = cmmnRepositoryService.getResourceAsStream(cmmnDeployment.getId(), resourceName);
        assertNotNull(inputStream);
        IOUtils.closeSilently(inputStream);
        
        InputStream diagramInputStream = cmmnRepositoryService.getResourceAsStream(cmmnDeployment.getId(), diagramResourceName);
        assertNotNull(diagramInputStream);
        IOUtils.closeSilently(diagramInputStream);
        
        CaseDefinition caseDefinition = cmmnRepositoryService.createCaseDefinitionQuery().deploymentId(cmmnDeployment.getId()).singleResult();
        
        InputStream caseDiagramInputStream = cmmnRepositoryService.getCaseDiagram(caseDefinition.getId());
        assertNotNull(caseDiagramInputStream);
        IOUtils.closeSilently(caseDiagramInputStream);
    }

    @Test
    public void testBulkInsertCmmnDeployments() {

        List<String> deploymentIds = cmmnEngineConfiguration.getCommandExecutor()
            .execute(commandContext -> {
                mobius.cmmn.api.repository.CmmnDeployment deployment1 = cmmnRepositoryService.createDeployment()
                    .name("First deployment")
                    .key("one-human")
                    .category("test")
                    .addClasspathResource("mobius/cmmn/test/one-human-task-model.cmmn")
                    .deploy();
                mobius.cmmn.api.repository.CmmnDeployment deployment2 = cmmnRepositoryService.createDeployment()
                    .name("Second deployment")
                    .key("example-task")
                    .addClasspathResource("mobius/cmmn/test/example-task-model.cmmn")
                    .deploy();

                return Arrays.asList(deployment1.getId(), deployment2.getId());
            });

        assertThat(cmmnRepositoryService.getDeploymentResourceNames(deploymentIds.get(0)))
            .containsExactlyInAnyOrder("mobius/cmmn/test/one-human-task-model.cmmn");

        assertThat(cmmnRepositoryService.getDeploymentResourceNames(deploymentIds.get(1)))
            .containsExactlyInAnyOrder("mobius/cmmn/test/example-task-model.cmmn");

        assertThat(cmmnRepositoryService.createDeploymentQuery().list())
            .as("Deployment time not null")
            .allSatisfy(deployment -> assertThat(deployment.getDeploymentTime()).as(deployment.getName()).isNotNull())
            .extracting(mobius.cmmn.api.repository.CmmnDeployment::getId, mobius.cmmn.api.repository.CmmnDeployment::getName,
                mobius.cmmn.api.repository.CmmnDeployment::getKey, mobius.cmmn.api.repository.CmmnDeployment::getCategory)
            .as("id, name, key, category")
            .containsExactlyInAnyOrder(
                tuple(deploymentIds.get(0), "First deployment", "one-human", "test"),
                tuple(deploymentIds.get(1), "Second deployment", "example-task", null)
            );

        deploymentIds.forEach(deploymentId -> cmmnRepositoryService.deleteDeployment(deploymentId, true));
    }
}
