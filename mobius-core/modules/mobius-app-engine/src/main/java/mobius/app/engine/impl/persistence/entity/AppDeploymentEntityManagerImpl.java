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

package mobius.app.engine.impl.persistence.entity;

import java.util.List;

import mobius.app.engine.impl.persistence.entity.data.AppDeploymentDataManager;
import mobius.app.engine.impl.repository.AppDeploymentQueryImpl;
import mobius.app.api.repository.AppDefinition;
import mobius.app.api.repository.AppDeployment;
import mobius.app.api.repository.AppDeploymentQuery;
import mobius.app.engine.AppEngineConfiguration;
import mobius.common.engine.api.repository.EngineResource;
import mobius.common.engine.impl.persistence.entity.data.DataManager;

/**
 *
 */
public class AppDeploymentEntityManagerImpl extends AbstractAppEntityManager<AppDeploymentEntity> implements AppDeploymentEntityManager {

    protected AppDeploymentDataManager deploymentDataManager;

    public AppDeploymentEntityManagerImpl(AppEngineConfiguration appEngineConfiguration, AppDeploymentDataManager deploymentDataManager) {
        super(appEngineConfiguration);
        this.deploymentDataManager = deploymentDataManager;
    }

    @Override
    protected DataManager<AppDeploymentEntity> getDataManager() {
        return deploymentDataManager;
    }

    @Override
    public void insert(AppDeploymentEntity deployment) {
        super.insert(deployment, true);

        for (EngineResource resource : deployment.getResources().values()) {
            resource.setDeploymentId(deployment.getId());
            getAppResourceEntityManager().insert((AppResourceEntity) resource);
        }
    }

    @Override
    public void deleteDeploymentAndRelatedData(String deploymentId, boolean cascade) {
        AppDefinitionEntityManager appDefinitionEntityManager = getAppDefinitionEntityManager();
        List<AppDefinition> appDefinitions = appDefinitionEntityManager.createAppDefinitionQuery().deploymentId(deploymentId).list();
        for (AppDefinition appDefinition : appDefinitions) {
            if (cascade) {
                appDefinitionEntityManager.deleteAppDefinitionAndRelatedData(appDefinition.getId());
            } else {
                appDefinitionEntityManager.delete(appDefinition.getId());
            }
        }
        getAppResourceEntityManager().deleteResourcesByDeploymentId(deploymentId);
        delete(findById(deploymentId));
    }

    @Override
    public AppDeploymentEntity findLatestDeploymentByName(String deploymentName) {
        return deploymentDataManager.findLatestDeploymentByName(deploymentName);
    }
    
    @Override
    public List<String> getDeploymentResourceNames(String deploymentId) {
        return deploymentDataManager.getDeploymentResourceNames(deploymentId);
    }
    
    @Override
    public AppDeploymentQuery createDeploymentQuery() {
        return new AppDeploymentQueryImpl(appEngineConfiguration.getCommandExecutor());
    }
    
    @Override
    public List<AppDeployment> findDeploymentsByQueryCriteria(AppDeploymentQuery deploymentQuery) {
        return deploymentDataManager.findDeploymentsByQueryCriteria((AppDeploymentQueryImpl) deploymentQuery);
    }
    
    @Override
    public long findDeploymentCountByQueryCriteria(AppDeploymentQuery deploymentQuery) {
        return deploymentDataManager.findDeploymentCountByQueryCriteria((AppDeploymentQueryImpl) deploymentQuery);
    }

    public AppDeploymentDataManager getDeploymentDataManager() {
        return deploymentDataManager;
    }

    public void setDeploymentDataManager(AppDeploymentDataManager deploymentDataManager) {
        this.deploymentDataManager = deploymentDataManager;
    }

}
