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
package mobius.app.engine.impl.repository;

import mobius.app.engine.impl.deployer.AppDeploymentManager;
import mobius.app.engine.impl.util.CommandContextUtil;
import mobius.app.api.repository.AppDefinition;
import mobius.app.api.repository.AppModel;
import mobius.app.engine.impl.persistence.entity.deploy.AppDefinitionCacheEntry;

/**
 * @author Tijs Rademakers
 */
public class AppDefinitionUtil {
    
    public static AppDefinition getAppDefinition(String appDefinitionId) {
        AppDeploymentManager deploymentManager = CommandContextUtil.getAppEngineConfiguration().getDeploymentManager();
        AppDefinitionCacheEntry cacheEntry = deploymentManager.getAppDefinitionCache().get(appDefinitionId);
        return getAppDefinition(appDefinitionId, deploymentManager, cacheEntry);
    }

    protected static AppDefinition getAppDefinition(String appDefinitionId, AppDeploymentManager deploymentManager, AppDefinitionCacheEntry cacheEntry) {
        if (cacheEntry != null) {
            return cacheEntry.getAppDefinition();
        }
        return deploymentManager.findDeployedAppDefinitionById(appDefinitionId);
    }

    public static AppModel getAppModel(String appDefinitionId) {
        AppDeploymentManager deploymentManager = CommandContextUtil.getAppEngineConfiguration().getDeploymentManager();
        AppDefinitionCacheEntry cacheEntry = deploymentManager.getAppDefinitionCache().get(appDefinitionId);
        if (cacheEntry != null) {
            return cacheEntry.getAppModel();
        }
        deploymentManager.findDeployedAppDefinitionById(appDefinitionId);
        return deploymentManager.getAppDefinitionCache().get(appDefinitionId).getAppModel();
    }

}
