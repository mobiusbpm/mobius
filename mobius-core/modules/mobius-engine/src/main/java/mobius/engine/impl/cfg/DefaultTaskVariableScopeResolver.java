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

package mobius.engine.impl.cfg;

import mobius.engine.impl.persistence.entity.ExecutionEntityImpl;
import mobius.engine.impl.persistence.entity.ExecutionEntityManager;
import mobius.task.service.InternalTaskVariableScopeResolver;
import mobius.task.service.impl.persistence.entity.TaskEntity;
import mobius.variable.service.impl.persistence.entity.VariableScopeImpl;

/**
 *
 */
public class DefaultTaskVariableScopeResolver implements InternalTaskVariableScopeResolver {
    
    protected ProcessEngineConfigurationImpl processEngineConfiguration;

    public DefaultTaskVariableScopeResolver(ProcessEngineConfigurationImpl processEngineConfiguration) {
        this.processEngineConfiguration = processEngineConfiguration;
    }

    @Override
    public VariableScopeImpl resolveParentVariableScope(TaskEntity task) {
        if (task.getExecutionId() != null) {
            return (ExecutionEntityImpl) getExecutionEntityManager().findById(task.getExecutionId());
        }
        return null;
    }
    
    protected ExecutionEntityManager getExecutionEntityManager() {
        return processEngineConfiguration.getExecutionEntityManager();
    }
}
