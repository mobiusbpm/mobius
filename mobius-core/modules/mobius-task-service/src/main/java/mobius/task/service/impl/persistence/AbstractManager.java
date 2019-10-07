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

package mobius.task.service.impl.persistence;

import mobius.common.engine.api.delegate.event.FlowableEventDispatcher;
import mobius.common.engine.impl.context.Context;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.common.engine.impl.runtime.Clock;
import mobius.task.service.TaskServiceConfiguration;
import mobius.task.service.impl.persistence.entity.HistoricTaskInstanceEntityManager;
import mobius.task.service.impl.persistence.entity.TaskEntityManager;

/**
 *
 */
public abstract class AbstractManager {
    
    protected TaskServiceConfiguration taskServiceConfiguration;

    public AbstractManager(TaskServiceConfiguration taskServiceConfiguration) {
        this.taskServiceConfiguration = taskServiceConfiguration;
    }

    // Command scoped

    protected CommandContext getCommandContext() {
        return Context.getCommandContext();
    }

    protected <T> T getSession(Class<T> sessionClass) {
        return getCommandContext().getSession(sessionClass);
    }

    // Engine scoped
    
    protected TaskServiceConfiguration getTaskServiceConfiguration() {
        return taskServiceConfiguration;
    }

    protected Clock getClock() {
        return getTaskServiceConfiguration().getClock();
    }

    protected FlowableEventDispatcher getEventDispatcher() {
        return getTaskServiceConfiguration().getEventDispatcher();
    }

    protected TaskEntityManager getTaskEntityManager() {
        return getTaskServiceConfiguration().getTaskEntityManager();
    }

    protected HistoricTaskInstanceEntityManager getHistoricTaskInstanceEntityManager() {
        return getTaskServiceConfiguration().getHistoricTaskInstanceEntityManager();
    }
}
