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

import mobius.cmmn.api.history.HistoricCaseInstance;
import mobius.cmmn.api.history.HistoricMilestoneInstance;
import mobius.cmmn.api.repository.CaseDefinition;
import mobius.cmmn.api.repository.CaseDefinitionQuery;
import mobius.cmmn.api.runtime.CaseInstance;
import mobius.cmmn.engine.CmmnEngineConfiguration;
import mobius.cmmn.engine.impl.history.CmmnHistoryHelper;
import mobius.cmmn.engine.impl.history.HistoricCaseInstanceQueryImpl;
import mobius.cmmn.engine.impl.history.HistoricMilestoneInstanceQueryImpl;
import mobius.cmmn.engine.impl.persistence.entity.data.CaseDefinitionDataManager;
import mobius.cmmn.engine.impl.repository.CaseDefinitionQueryImpl;
import mobius.cmmn.engine.impl.runtime.CaseInstanceQueryImpl;
import mobius.cmmn.engine.impl.task.TaskHelper;
import mobius.cmmn.engine.impl.util.CommandContextUtil;
import mobius.common.engine.api.scope.ScopeTypes;
import mobius.common.engine.impl.persistence.entity.data.DataManager;
import mobius.identitylink.service.impl.persistence.entity.HistoricIdentityLinkEntityManager;
import mobius.task.api.history.HistoricTaskInstance;
import mobius.task.service.impl.HistoricTaskInstanceQueryImpl;
import mobius.task.service.impl.persistence.entity.HistoricTaskInstanceEntityManager;

import java.util.List;

/**
 *
 */
public class CaseDefinitionEntityManagerImpl extends AbstractCmmnEntityManager<CaseDefinitionEntity> implements
		CaseDefinitionEntityManager {

    protected CaseDefinitionDataManager caseDefinitionDataManager;

    public CaseDefinitionEntityManagerImpl(CmmnEngineConfiguration cmmnEngineConfiguration, CaseDefinitionDataManager caseDefinitionDataManager) {
        super(cmmnEngineConfiguration);
        this.caseDefinitionDataManager = caseDefinitionDataManager;
    }

    @Override
    protected DataManager<CaseDefinitionEntity> getDataManager() {
        return caseDefinitionDataManager;
    }

    @Override
    public CaseDefinitionEntity findLatestCaseDefinitionByKey(String caseDefinitionKey) {
        return caseDefinitionDataManager.findLatestCaseDefinitionByKey(caseDefinitionKey);
    }

    @Override
    public CaseDefinitionEntity findLatestCaseDefinitionByKeyAndTenantId(String caseDefinitionKey, String tenantId) {
        return caseDefinitionDataManager.findLatestCaseDefinitionByKeyAndTenantId(caseDefinitionKey, tenantId);
    }

    @Override
    public CaseDefinitionEntity findCaseDefinitionByDeploymentAndKey(String deploymentId, String caseDefinitionKey) {
        return caseDefinitionDataManager.findCaseDefinitionByDeploymentAndKey(deploymentId, caseDefinitionKey);
    }

    @Override
    public CaseDefinitionEntity findCaseDefinitionByDeploymentAndKeyAndTenantId(String deploymentId, String caseDefinitionKey, String tenantId) {
        return caseDefinitionDataManager.findCaseDefinitionByDeploymentAndKeyAndTenantId(deploymentId, caseDefinitionKey, tenantId);
    }

    @Override
    public CaseDefinition findCaseDefinitionByKeyAndVersionAndTenantId(String caseDefinitionKey, Integer caseDefinitionVersion, String tenantId) {
        if (tenantId == null || CmmnEngineConfiguration.NO_TENANT_ID.equals(tenantId)) {
            return caseDefinitionDataManager.findCaseDefinitionByKeyAndVersion(caseDefinitionKey, caseDefinitionVersion);
        } else {
            return caseDefinitionDataManager.findCaseDefinitionByKeyAndVersionAndTenantId(caseDefinitionKey, caseDefinitionVersion, tenantId);
        }
    }
    
    @Override
    public void deleteCaseDefinitionAndRelatedData(String caseDefinitionId, boolean cascadeHistory) {
        
        // Case instances
        CaseInstanceEntityManager caseInstanceEntityManager = getCaseInstanceEntityManager();
        List<CaseInstance> caseInstances = caseInstanceEntityManager.findByCriteria(new CaseInstanceQueryImpl().caseDefinitionId(caseDefinitionId));
        for (CaseInstance caseInstance : caseInstances) {
            caseInstanceEntityManager.delete(caseInstance.getId(), true, null);
        }
        
        if (cascadeHistory) {
            CommandContextUtil.getHistoricTaskService().deleteHistoricTaskLogEntriesForScopeDefinition(ScopeTypes.CMMN, caseDefinitionId);

            HistoricIdentityLinkEntityManager historicIdentityLinkEntityManager = getHistoricIdentityLinkEntityManager();
            historicIdentityLinkEntityManager.deleteHistoricIdentityLinksByScopeDefinitionIdAndScopeType(caseDefinitionId, ScopeTypes.CMMN);
            
            // Historic milestone
            HistoricMilestoneInstanceEntityManager historicMilestoneInstanceEntityManager = getHistoricMilestoneInstanceEntityManager();
            List<HistoricMilestoneInstance> historicMilestoneInstances = historicMilestoneInstanceEntityManager
                    .findHistoricMilestoneInstancesByQueryCriteria(new HistoricMilestoneInstanceQueryImpl().milestoneInstanceCaseDefinitionId(caseDefinitionId));
            for (HistoricMilestoneInstance historicMilestoneInstance : historicMilestoneInstances) {
                historicMilestoneInstanceEntityManager.delete(historicMilestoneInstance.getId());
            }

            // Historic tasks
            HistoricTaskInstanceEntityManager historicTaskInstanceEntityManager = getHistoricTaskInstanceEntityManager();
            List<HistoricTaskInstance> historicTaskInstances = historicTaskInstanceEntityManager
                    .findHistoricTaskInstancesByQueryCriteria(new HistoricTaskInstanceQueryImpl().scopeDefinitionId(caseDefinitionId).scopeType(
							ScopeTypes.CMMN));
            for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
                TaskHelper.deleteHistoricTask(historicTaskInstance.getId());
            }

            // Historic Plan Items
            HistoricPlanItemInstanceEntityManager historicPlanItemInstanceEntityManager = getHistoricPlanItemInstanceEntityManager();
            historicPlanItemInstanceEntityManager.findByCaseDefinitionId(caseDefinitionId)
                    .forEach(p -> historicPlanItemInstanceEntityManager.delete(p.getId()));

            HistoricCaseInstanceEntityManager historicCaseInstanceEntityManager = getHistoricCaseInstanceEntityManager();
            List<HistoricCaseInstance> historicCaseInstanceEntities = historicCaseInstanceEntityManager
                    .findByCriteria(new HistoricCaseInstanceQueryImpl().caseDefinitionId(caseDefinitionId));
            for (HistoricCaseInstance historicCaseInstanceEntity : historicCaseInstanceEntities) {
                CmmnHistoryHelper.deleteHistoricCaseInstance(cmmnEngineConfiguration, historicCaseInstanceEntity.getId());
            }
        }
        
        CaseDefinitionEntity caseDefinitionEntity = findById(caseDefinitionId);
        delete(caseDefinitionEntity);
    }
    
    @Override
    public CaseDefinitionQuery createCaseDefinitionQuery() {
        return new CaseDefinitionQueryImpl(cmmnEngineConfiguration.getCommandExecutor());
    }

    @Override
    public List<CaseDefinition> findCaseDefinitionsByQueryCriteria(CaseDefinitionQuery caseDefinitionQuery) {
        return caseDefinitionDataManager.findCaseDefinitionsByQueryCriteria((CaseDefinitionQueryImpl) caseDefinitionQuery);
    }

    @Override
    public long findCaseDefinitionCountByQueryCriteria(CaseDefinitionQuery caseDefinitionQuery) {
        return caseDefinitionDataManager.findCaseDefinitionCountByQueryCriteria((CaseDefinitionQueryImpl) caseDefinitionQuery);
    }

    public CaseDefinitionDataManager getCaseDefinitionDataManager() {
        return caseDefinitionDataManager;
    }

    public void setCaseDefinitionDataManager(CaseDefinitionDataManager caseDefinitionDataManager) {
        this.caseDefinitionDataManager = caseDefinitionDataManager;
    }

}
