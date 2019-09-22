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

import org.apache.commons.lang3.StringUtils;
import mobius.cmmn.engine.impl.util.CommandContextUtil;
import mobius.common.engine.impl.interceptor.Command;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.task.api.Task;
import mobius.task.api.TaskBuilder;
import mobius.task.service.impl.persistence.CountingTaskEntity;
import mobius.task.service.impl.persistence.entity.TaskEntity;
import mobius.task.service.impl.util.CountingTaskUtil;

/**
 * Creates new task by {@link TaskBuilder}
 * 
 * @author martin.grofcik
 */
public class CreateCmmnTaskCmd implements Command<Task> {
    protected TaskBuilder taskBuilder;

    public CreateCmmnTaskCmd(TaskBuilder taskBuilder) {
        this.taskBuilder = taskBuilder;
    }

    @Override
    public Task execute(CommandContext commandContext) {
        Task task = CommandContextUtil.getTaskService().createTask(this.taskBuilder);
        if (CountingTaskUtil.isTaskRelatedEntityCountEnabledGlobally() && StringUtils.isNotEmpty(task.getParentTaskId())) {
            TaskEntity parentTaskEntity = CommandContextUtil.getTaskService().getTask(task.getParentTaskId());
            if (CountingTaskUtil.isTaskRelatedEntityCountEnabled(parentTaskEntity)) {
                CountingTaskEntity countingParentTaskEntity = (CountingTaskEntity) parentTaskEntity;
                countingParentTaskEntity.setSubTaskCount(countingParentTaskEntity.getSubTaskCount() + 1);
            }
        }

        return task;
    }
}
