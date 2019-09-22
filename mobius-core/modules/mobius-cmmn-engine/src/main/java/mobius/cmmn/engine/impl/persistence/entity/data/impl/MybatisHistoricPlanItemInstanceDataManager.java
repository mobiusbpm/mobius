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
package mobius.cmmn.engine.impl.persistence.entity.data.impl;

import mobius.cmmn.api.history.HistoricPlanItemInstance;
import mobius.cmmn.engine.CmmnEngineConfiguration;
import mobius.cmmn.engine.impl.history.HistoricPlanItemInstanceQueryImpl;
import mobius.cmmn.engine.impl.persistence.entity.HistoricPlanItemInstanceEntity;
import mobius.cmmn.engine.impl.persistence.entity.HistoricPlanItemInstanceEntityImpl;
import mobius.cmmn.engine.impl.persistence.entity.data.AbstractCmmnDataManager;
import mobius.cmmn.engine.impl.persistence.entity.data.HistoricPlanItemInstanceDataManager;
import mobius.common.engine.impl.persistence.cache.CachedEntityMatcherAdapter;

import java.util.List;

/**
 * @author Dennis Federico
 */
public class MybatisHistoricPlanItemInstanceDataManager extends AbstractCmmnDataManager<HistoricPlanItemInstanceEntity> implements
		HistoricPlanItemInstanceDataManager {

    protected CachedEntityMatcherAdapter<HistoricPlanItemInstanceEntity> historicPlanItemInstanceByCaseDefinitionIdMatcher = new CachedEntityMatcherAdapter<HistoricPlanItemInstanceEntity>() {
        @Override
        public boolean isRetained(HistoricPlanItemInstanceEntity entity, Object param) {
            return entity.getCaseDefinitionId().equals(param);
        }
    };

    public MybatisHistoricPlanItemInstanceDataManager(CmmnEngineConfiguration cmmnEngineConfiguration) {
        super(cmmnEngineConfiguration);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<HistoricPlanItemInstance> findByCriteria(HistoricPlanItemInstanceQueryImpl query) {
        return getDbSqlSession().selectList("selectHistoricPlanItemInstancesByQueryCriteria", query);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<HistoricPlanItemInstance> findByCaseDefinitionId(String caseDefinitionId) {
        List<? extends HistoricPlanItemInstance> list = getList("selectHistoricPlanItemInstancesByCaseDefinitionId", caseDefinitionId, historicPlanItemInstanceByCaseDefinitionIdMatcher, true);
        return (List<HistoricPlanItemInstance>) list;
    }

    @Override
    public long countByCriteria(HistoricPlanItemInstanceQueryImpl query) {
        return (Long) getDbSqlSession().selectOne("selectHistoricPlanItemInstancesCountByQueryCriteria", query);
    }

    @Override
    public void deleteByCaseDefinitionId(String caseDefinitionId) {
        getDbSqlSession().delete("deleteHistoricPlanItemInstanceByCaseDefinitionId", caseDefinitionId, getManagedEntityClass());
    }

    @Override
    public Class<? extends HistoricPlanItemInstanceEntity> getManagedEntityClass() {
        return HistoricPlanItemInstanceEntityImpl.class;
    }

    @Override
    public HistoricPlanItemInstanceEntity create() {
        return new HistoricPlanItemInstanceEntityImpl();
    }

}
