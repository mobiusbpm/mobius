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
package mobius.cmmn.engine.impl.cmd;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import mobius.cmmn.engine.CmmnEngineConfiguration;
import mobius.cmmn.engine.impl.task.TaskHelper;
import mobius.cmmn.engine.impl.util.CommandContextUtil;
import mobius.common.engine.api.FlowableIllegalArgumentException;
import mobius.common.engine.api.delegate.event.FlowableEngineEventType;
import mobius.common.engine.impl.history.HistoryLevel;
import mobius.common.engine.impl.interceptor.Command;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.task.api.Task;
import mobius.task.api.TaskInfo;
import mobius.task.service.delegate.TaskListener;
import mobius.task.service.impl.FlowableTaskEventBuilder;
import mobius.task.service.impl.persistence.entity.TaskEntity;

/**
 *
 */
public class SaveTaskCmd implements Command<Void>, Serializable {

    private static final long serialVersionUID = 1L;

    protected TaskEntity task;

    public SaveTaskCmd(Task task) {
        this.task = (TaskEntity) task;
    }

    @Override
    public Void execute(CommandContext commandContext) {
        if (task == null) {
            throw new FlowableIllegalArgumentException("task is null");
        }

        if (task.getRevision() == 0) {
            TaskHelper.insertTask(task, true);
            CommandContextUtil.getCmmnHistoryManager().recordTaskCreated(task);

            if (CommandContextUtil.getEventDispatcher() != null && CommandContextUtil.getEventDispatcher().isEnabled()) {
                CommandContextUtil.getEventDispatcher().dispatchEvent(FlowableTaskEventBuilder.createEntityEvent(FlowableEngineEventType.TASK_CREATED, task));
            }

        } else {
            
            CmmnEngineConfiguration cmmnEngineConfiguration = CommandContextUtil.getCmmnEngineConfiguration(commandContext);

            TaskInfo originalTaskEntity = CommandContextUtil.getTaskService().getTask(task.getId());
            
            if (originalTaskEntity == null && cmmnEngineConfiguration.getHistoryLevel().isAtLeast(HistoryLevel.AUDIT)) {
                originalTaskEntity = CommandContextUtil.getHistoricTaskService().getHistoricTask(task.getId());
            }
            
            String originalAssignee = originalTaskEntity.getAssignee();
            
            CommandContextUtil.getCmmnHistoryManager(commandContext).recordTaskInfoChange(task, cmmnEngineConfiguration.getClock().getCurrentTime());
            CommandContextUtil.getTaskService().updateTask(task, true);
            
            if (!StringUtils.equals(originalAssignee, task.getAssignee())) {

                CommandContextUtil.getCmmnEngineConfiguration(commandContext).getListenerNotificationHelper()
                    .executeTaskListeners(task, TaskListener.EVENTNAME_ASSIGNMENT);

                if (CommandContextUtil.getEventDispatcher() != null && CommandContextUtil.getEventDispatcher().isEnabled()) {
                    CommandContextUtil.getEventDispatcher().dispatchEvent(FlowableTaskEventBuilder.createEntityEvent(FlowableEngineEventType.TASK_ASSIGNED, task));
                }

            }
        }

        return null;
    }

}
