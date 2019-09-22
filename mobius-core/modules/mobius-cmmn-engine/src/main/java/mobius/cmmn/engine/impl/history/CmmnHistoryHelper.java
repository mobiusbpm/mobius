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
package mobius.cmmn.engine.impl.history;

import mobius.cmmn.engine.CmmnEngineConfiguration;
import mobius.cmmn.engine.impl.persistence.entity.HistoricCaseInstanceEntity;
import mobius.cmmn.engine.impl.persistence.entity.HistoricCaseInstanceEntityManager;
import mobius.cmmn.engine.impl.persistence.entity.HistoricMilestoneInstanceEntityManager;
import mobius.cmmn.engine.impl.persistence.entity.HistoricPlanItemInstanceEntityManager;
import mobius.cmmn.engine.impl.util.CommandContextUtil;
import mobius.common.engine.api.scope.ScopeTypes;
import mobius.variable.service.impl.persistence.entity.HistoricVariableInstanceEntity;
import mobius.variable.service.impl.persistence.entity.HistoricVariableInstanceEntityManager;

import java.util.List;

import static mobius.variable.service.impl.util.CommandContextUtil.getHistoricVariableInstanceEntityManager;

/**
 * Contains logic that is shared by multiple classes around history.
 * 
 * @author Joram Barrez
 */
public class CmmnHistoryHelper {
    
    public static void deleteHistoricCaseInstance(CmmnEngineConfiguration cmmnEngineConfiguration, String caseInstanceId) {
        HistoricCaseInstanceEntityManager historicCaseInstanceEntityManager = cmmnEngineConfiguration.getHistoricCaseInstanceEntityManager();
        HistoricCaseInstanceEntity historicCaseInstance = historicCaseInstanceEntityManager.findById(caseInstanceId);

        HistoricMilestoneInstanceEntityManager historicMilestoneInstanceEntityManager = cmmnEngineConfiguration.getHistoricMilestoneInstanceEntityManager();
        historicMilestoneInstanceEntityManager.findHistoricMilestoneInstancesByQueryCriteria(new HistoricMilestoneInstanceQueryImpl().milestoneInstanceCaseInstanceId(historicCaseInstance.getId()))
                .forEach(m -> historicMilestoneInstanceEntityManager.delete(m.getId()));

        HistoricPlanItemInstanceEntityManager historicPlanItemInstanceEntityManager = cmmnEngineConfiguration.getHistoricPlanItemInstanceEntityManager();
        historicPlanItemInstanceEntityManager.findByCriteria(new HistoricPlanItemInstanceQueryImpl().planItemInstanceCaseInstanceId(historicCaseInstance.getId()))
                .forEach(p -> historicPlanItemInstanceEntityManager.delete(p.getId()));

        CommandContextUtil.getHistoricIdentityLinkService().deleteHistoricIdentityLinksByScopeIdAndScopeType(historicCaseInstance.getId(), ScopeTypes.CMMN);
        
        if (cmmnEngineConfiguration.isEnableEntityLinks()) {
            CommandContextUtil.getHistoricEntityLinkService().deleteHistoricEntityLinksByScopeIdAndScopeType(historicCaseInstance.getId(), ScopeTypes.CMMN);
        }

        HistoricVariableInstanceEntityManager historicVariableInstanceEntityManager = getHistoricVariableInstanceEntityManager();
        List<HistoricVariableInstanceEntity> historicVariableInstanceEntities = historicVariableInstanceEntityManager
            .findHistoricalVariableInstancesByScopeIdAndScopeType(caseInstanceId, ScopeTypes.CMMN);
        for (HistoricVariableInstanceEntity historicVariableInstanceEntity : historicVariableInstanceEntities) {
            historicVariableInstanceEntityManager.delete(historicVariableInstanceEntity);
        }

        historicCaseInstanceEntityManager.delete(historicCaseInstance);

        // Also delete any sub cases that may be active
        historicCaseInstanceEntityManager.createHistoricCaseInstanceQuery().caseInstanceParentId(caseInstanceId).list()
                .forEach(c -> deleteHistoricCaseInstance(cmmnEngineConfiguration, c.getId()));
    }

}
