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
package mobius.standalone.deploy;

import java.io.IOException;
import java.text.MessageFormat;

import mobius.common.engine.impl.persistence.deploy.DefaultDeploymentCache;
import mobius.engine.impl.persistence.deploy.ProcessDefinitionCacheEntry;
import mobius.engine.impl.test.ResourceFlowableTestCase;
import mobius.engine.repository.Deployment;
import org.junit.jupiter.api.Test;

/**
 *
 */
public class DeploymentCacheLimitTest extends ResourceFlowableTestCase {

    public DeploymentCacheLimitTest() {
        super("mobius/standalone/deploy/deployment.cache.limit.test.flowable.cfg.xml");
    }

    @Test
    public void testDeploymentCacheLimit() throws IOException {
        int processDefinitionCacheLimit = 3; // This is set in the configuration
                                             // above

        DefaultDeploymentCache<ProcessDefinitionCacheEntry> processDefinitionCache = (DefaultDeploymentCache<ProcessDefinitionCacheEntry>) processEngineConfiguration.getProcessDefinitionCache();
        assertEquals(0, processDefinitionCache.size());

        String processDefinitionTemplate = DeploymentCacheTestUtil.readTemplateFile("/mobius/standalone/deploy/deploymentCacheTest.bpmn20.xml");
        for (int i = 1; i <= 5; i++) {
            repositoryService.createDeployment().addString("Process " + i + ".bpmn20.xml", MessageFormat.format(processDefinitionTemplate, i)).deploy();

            if (i < processDefinitionCacheLimit) {
                assertEquals(i, processDefinitionCache.size());
            } else {
                assertEquals(processDefinitionCacheLimit, processDefinitionCache.size());
            }
        }

        // Cleanup
        for (Deployment deployment : repositoryService.createDeploymentQuery().list()) {
            repositoryService.deleteDeployment(deployment.getId(), true);
        }
    }

}
