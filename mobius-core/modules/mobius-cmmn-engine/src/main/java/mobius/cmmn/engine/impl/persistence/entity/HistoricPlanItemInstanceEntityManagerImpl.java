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

import mobius.cmmn.api.history.HistoricPlanItemInstance;
import mobius.cmmn.api.history.HistoricPlanItemInstanceQuery;
import mobius.cmmn.engine.CmmnEngineConfiguration;
import mobius.cmmn.engine.impl.history.HistoricPlanItemInstanceQueryImpl;
import mobius.cmmn.engine.impl.persistence.entity.data.HistoricPlanItemInstanceDataManager;
import mobius.common.engine.impl.persistence.entity.data.DataManager;

import java.util.List;

/**
 * @author Dennis Federico
 */
public class HistoricPlanItemInstanceEntityManagerImpl extends AbstractCmmnEntityManager<HistoricPlanItemInstanceEntity>
		implements HistoricPlanItemInstanceEntityManager {

    protected HistoricPlanItemInstanceDataManager historicPlanItemInstanceDataManager;

    public HistoricPlanItemInstanceEntityManagerImpl(CmmnEngineConfiguration cmmnEngineConfiguration, HistoricPlanItemInstanceDataManager historicPlanItemInstanceDataManager) {
        super(cmmnEngineConfiguration);
        this.historicPlanItemInstanceDataManager = historicPlanItemInstanceDataManager;
    }

    @Override
    protected DataManager<HistoricPlanItemInstanceEntity> getDataManager() {
        return historicPlanItemInstanceDataManager;
    }

    @Override
    public HistoricPlanItemInstanceQuery createHistoricPlanItemInstanceQuery() {
        return new HistoricPlanItemInstanceQueryImpl(cmmnEngineConfiguration.getCommandExecutor());
    }

    @Override
    public List<HistoricPlanItemInstance> findByCaseDefinitionId(String caseDefinitionId) {
        return historicPlanItemInstanceDataManager.findByCaseDefinitionId(caseDefinitionId);
    }

    @Override
    public List<HistoricPlanItemInstance> findByCriteria(HistoricPlanItemInstanceQuery query) {
        return historicPlanItemInstanceDataManager.findByCriteria((HistoricPlanItemInstanceQueryImpl) query);
    }

    @Override
    public long countByCriteria(HistoricPlanItemInstanceQuery query) {
        return historicPlanItemInstanceDataManager.countByCriteria((HistoricPlanItemInstanceQueryImpl) query);
    }
}
