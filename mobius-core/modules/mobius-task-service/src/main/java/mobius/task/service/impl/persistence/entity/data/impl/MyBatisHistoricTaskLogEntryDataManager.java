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
package mobius.task.service.impl.persistence.entity.data.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mobius.common.engine.impl.db.AbstractDataManager;
import mobius.task.api.history.HistoricTaskLogEntry;
import mobius.task.service.impl.HistoricTaskLogEntryQueryImpl;
import mobius.task.service.impl.persistence.entity.HistoricTaskLogEntryEntity;
import mobius.task.service.impl.persistence.entity.HistoricTaskLogEntryEntityImpl;
import mobius.task.service.impl.persistence.entity.data.HistoricTaskLogEntryDataManager;

/**
 * @author martin.grofcik
 */
public class MyBatisHistoricTaskLogEntryDataManager extends AbstractDataManager<HistoricTaskLogEntryEntity> implements HistoricTaskLogEntryDataManager {

    @Override
    public Class<? extends HistoricTaskLogEntryEntity> getManagedEntityClass() {
        return HistoricTaskLogEntryEntityImpl.class;
    }

    @Override
    public HistoricTaskLogEntryEntity create() {
        return new HistoricTaskLogEntryEntityImpl();
    }

    @Override
    public long findHistoricTaskLogEntriesCountByQueryCriteria(HistoricTaskLogEntryQueryImpl taskLogEntryQuery) {
        return (Long) getDbSqlSession().selectOne("selectHistoricTaskLogEntriesCountByQueryCriteria", taskLogEntryQuery);
    }

    @Override
    public List<HistoricTaskLogEntry> findHistoricTaskLogEntriesByQueryCriteria(HistoricTaskLogEntryQueryImpl taskLogEntryQuery) {
        return getDbSqlSession().selectList("selectHistoricTaskLogEntriesByQueryCriteria", taskLogEntryQuery);
    }

    @Override
    public void deleteHistoricTaskLogEntry(long logEntryNumber) {
        getDbSqlSession().delete("deleteHistoricTaskLogEntryByLogNumber", logEntryNumber, HistoricTaskLogEntryEntityImpl.class);
    }

    @Override
    public void deleteHistoricTaskLogEntriesByProcessDefinitionId(String processDefinitionId) {
        getDbSqlSession().delete("deleteHistoricTaskLogEntriesByProcessDefinitionId", processDefinitionId, HistoricTaskLogEntryEntityImpl.class);
    }

    @Override
    public void deleteHistoricTaskLogEntriesByScopeDefinitionId(String scopeType, String scopeDefinitionId) {
        Map<String, String> params = new HashMap<>(2);
        params.put("scopeDefinitionId", scopeDefinitionId);
        params.put("scopeType", scopeType);
        getDbSqlSession().delete("deleteHistoricTaskLogEntriesByScopeDefinitionId", params, HistoricTaskLogEntryEntityImpl.class);
    }

    @Override
    public void deleteHistoricTaskLogEntriesByTaskId(String taskId) {
        getDbSqlSession().delete("deleteHistoricTaskLogEntriesByTaskId", taskId, HistoricTaskLogEntryEntityImpl.class);
    }

    @Override
    public long findHistoricTaskLogEntriesCountByNativeQueryCriteria(Map<String, Object> nativeHistoricTaskLogEntryQuery) {
        return (Long) getDbSqlSession().selectOne("selectHistoricTaskLogEntriesCountByNativeQueryCriteria", nativeHistoricTaskLogEntryQuery);
    }
    @Override
    public List<HistoricTaskLogEntry> findHistoricTaskLogEntriesByNativeQueryCriteria(Map<String, Object> nativeHistoricTaskLogEntryQuery) {
        return getDbSqlSession().selectListWithRawParameter("selectHistoricTaskLogEntriesByNativeQueryCriteria", nativeHistoricTaskLogEntryQuery);
    }
}
