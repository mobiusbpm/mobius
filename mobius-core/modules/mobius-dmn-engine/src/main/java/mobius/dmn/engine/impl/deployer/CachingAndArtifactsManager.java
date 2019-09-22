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
package mobius.dmn.engine.impl.deployer;

import mobius.common.engine.impl.persistence.deploy.DeploymentCache;
import mobius.dmn.engine.DmnEngineConfiguration;
import mobius.dmn.engine.impl.persistence.deploy.DecisionTableCacheEntry;
import mobius.dmn.engine.impl.persistence.entity.DecisionTableEntity;
import mobius.dmn.engine.impl.persistence.entity.DmnDeploymentEntity;
import mobius.dmn.engine.impl.util.CommandContextUtil;
import mobius.dmn.model.Decision;
import mobius.dmn.model.DmnDefinition;

/**
 * Updates caches and artifacts for a deployment and its decision tables
 */
public class CachingAndArtifactsManager {

    /**
     * Ensures that the decision table is cached in the appropriate places, including the deployment's collection of deployed artifacts and the deployment manager's cache.
     */
    public void updateCachingAndArtifacts(ParsedDeployment parsedDeployment) {
        final DmnEngineConfiguration dmnEngineConfiguration = CommandContextUtil.getDmnEngineConfiguration();
        DeploymentCache<DecisionTableCacheEntry> decisionTableCache = dmnEngineConfiguration.getDeploymentManager().getDecisionCache();
        DmnDeploymentEntity deployment = parsedDeployment.getDeployment();

        for (DecisionTableEntity decisionTable : parsedDeployment.getAllDecisionTables()) {
            DmnDefinition dmnDefinition = parsedDeployment.getDmnDefinitionForDecisionTable(decisionTable);
            Decision decision = parsedDeployment.getDecisionForDecisionTable(decisionTable);
            DecisionTableCacheEntry cacheEntry = new DecisionTableCacheEntry(decisionTable, dmnDefinition, decision);
            decisionTableCache.add(decisionTable.getId(), cacheEntry);

            // Add to deployment for further usage
            deployment.addDeployedArtifact(decisionTable);
        }
    }
}
