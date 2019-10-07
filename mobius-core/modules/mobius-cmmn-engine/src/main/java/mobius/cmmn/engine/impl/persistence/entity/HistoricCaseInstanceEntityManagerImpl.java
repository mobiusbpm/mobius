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
import mobius.cmmn.api.history.HistoricCaseInstanceQuery;
import mobius.cmmn.engine.CmmnEngineConfiguration;
import mobius.cmmn.engine.impl.history.HistoricCaseInstanceQueryImpl;
import mobius.cmmn.engine.impl.persistence.entity.data.HistoricCaseInstanceDataManager;
import mobius.common.engine.impl.persistence.entity.data.DataManager;

import java.util.List;

/**
 *
 */
public class HistoricCaseInstanceEntityManagerImpl extends AbstractCmmnEntityManager<HistoricCaseInstanceEntity>
		implements HistoricCaseInstanceEntityManager {

    protected HistoricCaseInstanceDataManager historicCaseInstanceDataManager;

    public HistoricCaseInstanceEntityManagerImpl(CmmnEngineConfiguration cmmnEngineConfiguration, HistoricCaseInstanceDataManager historicCaseInstanceDataManager) {
        super(cmmnEngineConfiguration);
        this.historicCaseInstanceDataManager = historicCaseInstanceDataManager;
    }

    @Override
    protected DataManager<HistoricCaseInstanceEntity> getDataManager() {
        return historicCaseInstanceDataManager;
    }
    
    @Override
    public HistoricCaseInstanceQuery createHistoricCaseInstanceQuery() {
        return new HistoricCaseInstanceQueryImpl(cmmnEngineConfiguration.getCommandExecutor());
    }
    
    @Override
    public List<HistoricCaseInstanceEntity> findHistoricCaseInstancesByCaseDefinitionId(String caseDefinitionId) {
        return historicCaseInstanceDataManager.findHistoricCaseInstancesByCaseDefinitionId(caseDefinitionId);
    }

    @Override
    public List<HistoricCaseInstance> findByCriteria(HistoricCaseInstanceQuery query) {
        return historicCaseInstanceDataManager.findByCriteria((HistoricCaseInstanceQueryImpl) query);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<HistoricCaseInstance> findWithVariablesByQueryCriteria(HistoricCaseInstanceQuery query) {
        return historicCaseInstanceDataManager.findWithVariablesByQueryCriteria((HistoricCaseInstanceQueryImpl) query);
    }


    @Override
   public long countByCriteria(HistoricCaseInstanceQuery query) {
        return historicCaseInstanceDataManager.countByCriteria((HistoricCaseInstanceQueryImpl) query);
    }
    
}
