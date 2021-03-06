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

import mobius.cmmn.engine.impl.util.CommandContextUtil;
import mobius.common.engine.api.FlowableIllegalArgumentException;
import mobius.common.engine.api.FlowableObjectNotFoundException;
import mobius.common.engine.impl.interceptor.Command;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.task.api.Task;
import mobius.task.service.impl.persistence.entity.TaskEntity;

/**
 *
 */
public class GetTaskVariableCmd implements Command<Object>, Serializable {

    private static final long serialVersionUID = 1L;
    protected String taskId;
    protected String variableName;
    protected boolean isLocal;

    public GetTaskVariableCmd(String taskId, String variableName, boolean isLocal) {
        this.taskId = taskId;
        this.variableName = variableName;
        this.isLocal = isLocal;
    }

    @Override
    public Object execute(CommandContext commandContext) {
        if (taskId == null) {
            throw new FlowableIllegalArgumentException("taskId is null");
        }
        if (variableName == null) {
            throw new FlowableIllegalArgumentException("variableName is null");
        }

        TaskEntity task = CommandContextUtil.getTaskService().getTask(taskId);

        if (task == null) {
            throw new FlowableObjectNotFoundException("task " + taskId + " doesn't exist", Task.class);
        }

        Object value;

        if (isLocal) {
            value = task.getVariableLocal(variableName, false);
        } else {
            value = task.getVariable(variableName, false);
        }

        return value;
    }
}
