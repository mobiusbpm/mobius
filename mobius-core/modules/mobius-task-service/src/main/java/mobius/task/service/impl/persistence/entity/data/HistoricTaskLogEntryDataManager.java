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
package mobius.task.service.impl.persistence.entity.data;

import java.util.List;
import java.util.Map;

import mobius.common.engine.impl.persistence.entity.data.DataManager;
import mobius.task.api.history.HistoricTaskLogEntry;
import mobius.task.service.impl.HistoricTaskLogEntryQueryImpl;
import mobius.task.service.impl.persistence.entity.HistoricTaskLogEntryEntity;

/**
 * author martin.grofcik
 */
public interface HistoricTaskLogEntryDataManager extends DataManager<HistoricTaskLogEntryEntity> {

    void deleteHistoricTaskLogEntry(long logEntryNumber);

    long findHistoricTaskLogEntriesCountByQueryCriteria(HistoricTaskLogEntryQueryImpl taskLogEntryQuery);

    List<HistoricTaskLogEntry> findHistoricTaskLogEntriesByQueryCriteria(HistoricTaskLogEntryQueryImpl taskLogEntryQuery);

    long findHistoricTaskLogEntriesCountByNativeQueryCriteria(Map<String, Object> nativeHistoricTaskLogEntryQuery);

    List<HistoricTaskLogEntry> findHistoricTaskLogEntriesByNativeQueryCriteria(Map<String, Object> nativeHistoricTaskLogEntryQuery);

    void deleteHistoricTaskLogEntriesByProcessDefinitionId(String processDefinitionId);

    void deleteHistoricTaskLogEntriesByScopeDefinitionId(String scopeType, String scopeDefinitionId);

    void deleteHistoricTaskLogEntriesByTaskId(String taskId);
}
