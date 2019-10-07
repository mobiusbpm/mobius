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

package mobius.engine.test.api.repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mobius.engine.impl.test.PluggableFlowableTestCase;
import mobius.engine.repository.Deployment;
import mobius.engine.repository.DeploymentQuery;
import org.junit.jupiter.api.Test;

/**
 *
 *
 */
public class DeploymentCategoryTest extends PluggableFlowableTestCase {

    @Test
    public void testDeploymentCategory() {
        String noCategoryDeploymentId = null;
        String deploymentOneId = null;
        String deploymentTwoV1Id = null;
        String deploymentTwoV2Id = null;
        String deploymentTwoNoCategory = null;

        try {
            noCategoryDeploymentId = repositoryService.createDeployment().name("0").addClasspathResource("mobius/engine/test/service/oneTaskProcess.bpmn20.xml").deploy().getId();

            deploymentOneId = repositoryService.createDeployment().name("1").category("one").addClasspathResource("mobius/engine/test/repository/one.bpmn20.xml").deploy().getId();

            deploymentTwoV1Id = repositoryService.createDeployment().name("2v1").category("two").addClasspathResource("mobius/engine/test/repository/two.bpmn20.xml").deploy().getId();

            deploymentTwoV2Id = repositoryService.createDeployment().name("2v2").category("two").addClasspathResource("mobius/engine/test/repository/two.bpmn20.xml").deploy().getId();

            DeploymentQuery query = repositoryService.createDeploymentQuery();
            assertEquals(4, query.list().size());

            Set<String> deploymentNames = getDeploymentNames(repositoryService.createDeploymentQuery().deploymentCategory("one").list());

            Set<String> expectedDeploymentNames = new HashSet<>();
            expectedDeploymentNames.add("1");

            assertEquals(expectedDeploymentNames, deploymentNames);

            deploymentNames = getDeploymentNames(repositoryService.createDeploymentQuery().deploymentCategoryNotEquals("two").list());

            expectedDeploymentNames.add("0");

            assertEquals(expectedDeploymentNames, deploymentNames);

            deploymentTwoNoCategory = repositoryService.createDeployment().name("noCategory").addClasspathResource("mobius/engine/test/repository/two.bpmn20.xml").deploy().getId();

            Deployment deploymentNoCategory = repositoryService.createDeploymentQuery().deploymentId(deploymentTwoNoCategory).singleResult();
            assertNull(deploymentNoCategory.getCategory());

            repositoryService.setDeploymentCategory(deploymentTwoNoCategory, "newCategory");
            deploymentNoCategory = repositoryService.createDeploymentQuery().deploymentId(deploymentTwoNoCategory).singleResult();
            assertEquals("newCategory", deploymentNoCategory.getCategory());

        } finally {
            if (noCategoryDeploymentId != null)
                undeploy(noCategoryDeploymentId);
            if (deploymentOneId != null)
                undeploy(deploymentOneId);
            if (deploymentTwoV1Id != null)
                undeploy(deploymentTwoV1Id);
            if (deploymentTwoV2Id != null)
                undeploy(deploymentTwoV2Id);
            if (deploymentTwoNoCategory != null)
                undeploy(deploymentTwoNoCategory);
        }
    }

    @Test
    public void testDeploymentKey() {
        String noKeyDeploymentId = null;
        String deploymentOneId = null;
        String deploymentTwoV1Id = null;
        String deploymentTwoV2Id = null;
        String deploymentTwoNoKey = null;

        try {
            noKeyDeploymentId = repositoryService.createDeployment().name("0").addClasspathResource("mobius/engine/test/service/oneTaskProcess.bpmn20.xml").deploy().getId();

            deploymentOneId = repositoryService.createDeployment().name("1").key("one").addClasspathResource("mobius/engine/test/repository/one.bpmn20.xml").deploy().getId();

            deploymentTwoV1Id = repositoryService.createDeployment().name("2v1").key("two").addClasspathResource("mobius/engine/test/repository/two.bpmn20.xml").deploy().getId();

            deploymentTwoV2Id = repositoryService.createDeployment().name("2v2").key("two").addClasspathResource("mobius/engine/test/repository/two.bpmn20.xml").deploy().getId();

            DeploymentQuery query = repositoryService.createDeploymentQuery();
            assertEquals(4, query.list().size());

            Set<String> deploymentNames = getDeploymentNames(repositoryService.createDeploymentQuery().deploymentKey("one").list());

            Set<String> expectedDeploymentNames = new HashSet<>();
            expectedDeploymentNames.add("1");

            assertEquals(expectedDeploymentNames, deploymentNames);

            deploymentTwoNoKey = repositoryService.createDeployment().name("noCategory").addClasspathResource("mobius/engine/test/repository/two.bpmn20.xml").deploy().getId();

            Deployment deploymentNoCategory = repositoryService.createDeploymentQuery().deploymentId(deploymentTwoNoKey).singleResult();
            assertNull(deploymentNoCategory.getCategory());

            repositoryService.setDeploymentKey(deploymentTwoNoKey, "newKey");
            deploymentNoCategory = repositoryService.createDeploymentQuery().deploymentId(deploymentTwoNoKey).singleResult();
            assertEquals("newKey", deploymentNoCategory.getKey());

        } finally {
            if (noKeyDeploymentId != null)
                undeploy(noKeyDeploymentId);
            if (deploymentOneId != null)
                undeploy(deploymentOneId);
            if (deploymentTwoV1Id != null)
                undeploy(deploymentTwoV1Id);
            if (deploymentTwoV2Id != null)
                undeploy(deploymentTwoV2Id);
            if (deploymentTwoNoKey != null)
                undeploy(deploymentTwoNoKey);
        }
    }

    private Set<String> getDeploymentNames(List<Deployment> deployments) {
        Set<String> deploymentNames = new HashSet<>();
        for (Deployment deployment : deployments) {
            deploymentNames.add(deployment.getName());
        }
        return deploymentNames;
    }

    private void undeploy(String deploymentId) {
        try {
            repositoryService.deleteDeployment(deploymentId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
