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
package mobius.engine.impl;

import mobius.common.engine.impl.interceptor.CommandExecutor;
import mobius.engine.impl.cmd.CreateTaskCmd;
import mobius.task.api.Task;
import mobius.task.api.TaskBuilder;
import mobius.task.service.impl.BaseTaskBuilderImpl;

/**
 * {@link TaskBuilder} implementation
 */
public class TaskBuilderImpl extends BaseTaskBuilderImpl {
    TaskBuilderImpl(CommandExecutor commandExecutor) {
        super(commandExecutor);
    }

    @Override
    public Task create() {
        return commandExecutor.execute(new CreateTaskCmd(this));
    }

}
