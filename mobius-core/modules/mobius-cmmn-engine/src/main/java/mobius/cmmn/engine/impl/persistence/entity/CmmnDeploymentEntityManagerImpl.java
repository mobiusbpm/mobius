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

package mobius.cmmn.engine.impl.persistence.entity;

import mobius.cmmn.api.repository.CaseDefinition;
import mobius.cmmn.api.repository.CmmnDeployment;
import mobius.cmmn.api.repository.CmmnDeploymentQuery;
import mobius.cmmn.engine.CmmnEngineConfiguration;
import mobius.cmmn.engine.impl.persistence.entity.data.CmmnDeploymentDataManager;
import mobius.cmmn.engine.impl.repository.CmmnDeploymentQueryImpl;
import mobius.cmmn.engine.impl.util.CommandContextUtil;
import mobius.common.engine.api.repository.EngineResource;
import mobius.common.engine.api.scope.ScopeTypes;
import mobius.common.engine.impl.persistence.entity.data.DataManager;

import java.util.List;

/**
 * @author Joram Barrez
 */
public class CmmnDeploymentEntityManagerImpl extends AbstractCmmnEntityManager<CmmnDeploymentEntity>
		implements CmmnDeploymentEntityManager {

    protected CmmnDeploymentDataManager deploymentDataManager;

    public CmmnDeploymentEntityManagerImpl(CmmnEngineConfiguration cmmnEngineConfiguration, CmmnDeploymentDataManager deploymentDataManager) {
        super(cmmnEngineConfiguration);
        this.deploymentDataManager = deploymentDataManager;
    }

    @Override
    protected DataManager<CmmnDeploymentEntity> getDataManager() {
        return deploymentDataManager;
    }

    @Override
    public void insert(CmmnDeploymentEntity deployment) {
        super.insert(deployment, true);

        for (EngineResource resource : deployment.getResources().values()) {
            resource.setDeploymentId(deployment.getId());
            getCmmnResourceEntityManager().insert((CmmnResourceEntity) resource);
        }
    }

    @Override
    public void deleteDeploymentAndRelatedData(String deploymentId, boolean cascade) {
        CaseDefinitionEntityManager caseDefinitionEntityManager = getCaseDefinitionEntityManager();
        List<CaseDefinition> caseDefinitions = caseDefinitionEntityManager.createCaseDefinitionQuery().deploymentId(deploymentId).list();
        for (CaseDefinition caseDefinition : caseDefinitions) {
            CommandContextUtil.getIdentityLinkService().deleteIdentityLinksByScopeDefinitionIdAndType(caseDefinition.getId(), ScopeTypes.CMMN);
            
            if (cascade) {
                caseDefinitionEntityManager.deleteCaseDefinitionAndRelatedData(caseDefinition.getId(), true);
            } else {
                caseDefinitionEntityManager.delete(caseDefinition.getId());
            }
        }
        getCmmnResourceEntityManager().deleteResourcesByDeploymentId(deploymentId);
        delete(findById(deploymentId));
    }

    @Override
    public CmmnDeploymentEntity findLatestDeploymentByName(String deploymentName) {
        return deploymentDataManager.findLatestDeploymentByName(deploymentName);
    }
    
    @Override
    public List<String> getDeploymentResourceNames(String deploymentId) {
        return deploymentDataManager.getDeploymentResourceNames(deploymentId);
    }
    
    @Override
    public CmmnDeploymentQuery createDeploymentQuery() {
        return new CmmnDeploymentQueryImpl(cmmnEngineConfiguration.getCommandExecutor());
    }
    
    @Override
    public List<CmmnDeployment> findDeploymentsByQueryCriteria(CmmnDeploymentQuery deploymentQuery) {
        return deploymentDataManager.findDeploymentsByQueryCriteria((CmmnDeploymentQueryImpl) deploymentQuery);
    }
    
    @Override
    public long findDeploymentCountByQueryCriteria(CmmnDeploymentQuery deploymentQuery) {
        return deploymentDataManager.findDeploymentCountByQueryCriteria((CmmnDeploymentQueryImpl) deploymentQuery);
    }

    public CmmnDeploymentDataManager getDeploymentDataManager() {
        return deploymentDataManager;
    }

    public void setDeploymentDataManager(CmmnDeploymentDataManager deploymentDataManager) {
        this.deploymentDataManager = deploymentDataManager;
    }

}
