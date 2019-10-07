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
package mobius.task.service;

import java.util.Date;
import java.util.List;

import mobius.common.engine.impl.interceptor.CommandExecutor;
import mobius.task.api.TaskInfo;
import mobius.task.api.history.HistoricTaskInstance;
import mobius.task.api.history.HistoricTaskLogEntryBuilder;
import mobius.task.api.history.HistoricTaskLogEntryQuery;
import mobius.task.api.history.NativeHistoricTaskLogEntryQuery;
import mobius.task.service.impl.HistoricTaskInstanceQueryImpl;
import mobius.task.service.impl.persistence.entity.HistoricTaskInstanceEntity;
import mobius.task.service.impl.persistence.entity.TaskEntity;

/**
 * Service which provides access to {@link HistoricTaskInstanceEntity}.
 * 
 * @author Tijs Rademakers
 *
 */
public interface HistoricTaskService {

    HistoricTaskInstanceEntity getHistoricTask(String id);
    
    List<HistoricTaskInstanceEntity> findHistoricTasksByParentTaskId(String parentTaskId);
    
    List<HistoricTaskInstanceEntity> findHistoricTasksByProcessInstanceId(String processInstanceId);
    
    List<HistoricTaskInstance> findHistoricTaskInstancesByQueryCriteria(HistoricTaskInstanceQueryImpl historicTaskInstanceQuery);
    
    HistoricTaskInstanceEntity createHistoricTask();
    
    HistoricTaskInstanceEntity createHistoricTask(TaskEntity taskEntity);
    
    void updateHistoricTask(HistoricTaskInstanceEntity historicTaskInstanceEntity, boolean fireUpdateEvent);
    
    void insertHistoricTask(HistoricTaskInstanceEntity historicTaskInstanceEntity, boolean fireCreateEvent);
    
    void deleteHistoricTask(HistoricTaskInstanceEntity HistoricTaskInstance);
    
    HistoricTaskInstanceEntity recordTaskCreated(TaskEntity task);
    
    HistoricTaskInstanceEntity recordTaskEnd(TaskEntity task, String deleteReason, Date endTime);
    
    HistoricTaskInstanceEntity recordTaskInfoChange(TaskEntity taskEntity, Date changeTime);

    void deleteHistoricTaskLogEntry(long taskLogNumber);

    void createHistoricTaskLogEntry(HistoricTaskLogEntryBuilder historicTaskLogEntryBuilder);

    /**
     * Log new entry to the task log.
     *
     * @param taskInfo task to which add log entry
     * @param logEntryType log entry type
     * @param data log entry data
     */
    void addHistoricTaskLogEntry(TaskInfo taskInfo, String logEntryType, String data);

    HistoricTaskLogEntryQuery createHistoricTaskLogEntryQuery(CommandExecutor commandExecutor);

    NativeHistoricTaskLogEntryQuery createNativeHistoricTaskLogEntryQuery(CommandExecutor commandExecutor);

    void deleteHistoricTaskLogEntriesForProcessDefinition(String processDefinitionId);

    void deleteHistoricTaskLogEntriesForScopeDefinition(String scopeType, String scopeDefinitionId);

    void deleteHistoricTaskLogEntriesForTaskId(String taskId);

}
