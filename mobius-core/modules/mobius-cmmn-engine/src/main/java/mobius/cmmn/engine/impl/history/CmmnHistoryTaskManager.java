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

import java.util.Date;

import mobius.cmmn.engine.CmmnEngineConfiguration;
import mobius.task.api.history.HistoricTaskLogEntryBuilder;
import mobius.task.service.history.InternalHistoryTaskManager;
import mobius.task.service.impl.persistence.entity.TaskEntity;

/**
 *
 */
public class CmmnHistoryTaskManager implements InternalHistoryTaskManager {

    protected CmmnEngineConfiguration cmmnEngineConfiguration;

    public CmmnHistoryTaskManager(CmmnEngineConfiguration cmmnEngineConfiguration) {
        this.cmmnEngineConfiguration = cmmnEngineConfiguration;
    }

    @Override
    public void recordTaskInfoChange(TaskEntity taskEntity, Date changeTime) {
        cmmnEngineConfiguration.getCmmnHistoryManager().recordTaskInfoChange(taskEntity, changeTime);
    }

    @Override
    public void recordTaskCreated(TaskEntity taskEntity) {
        cmmnEngineConfiguration.getCmmnHistoryManager().recordTaskCreated(taskEntity);
    }

    @Override
    public void recordHistoryUserTaskLog(HistoricTaskLogEntryBuilder taskLogEntryBuilder) {
        cmmnEngineConfiguration.getCmmnHistoryManager().recordHistoricUserTaskLogEntry(taskLogEntryBuilder);
    }

    @Override
    public void deleteHistoryUserTaskLog(long logNumber) {
        cmmnEngineConfiguration.getCmmnHistoryManager().deleteHistoricUserTaskLogEntry(logNumber);
    }
}
