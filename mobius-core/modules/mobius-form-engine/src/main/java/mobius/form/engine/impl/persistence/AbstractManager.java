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

package mobius.form.engine.impl.persistence;

import mobius.common.engine.impl.context.Context;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.form.engine.FormEngineConfiguration;
import mobius.form.engine.impl.persistence.entity.FormDefinitionEntityManager;
import mobius.form.engine.impl.persistence.entity.FormDeploymentEntityManager;
import mobius.form.engine.impl.persistence.entity.FormResourceEntityManager;

/**
 * @author Tijs Rademakers
 *
 */
public abstract class AbstractManager {

    protected FormEngineConfiguration formEngineConfiguration;

    public AbstractManager(FormEngineConfiguration formEngineConfiguration) {
        this.formEngineConfiguration = formEngineConfiguration;
    }

    // Command scoped

    protected CommandContext getCommandContext() {
        return Context.getCommandContext();
    }

    protected <T> T getSession(Class<T> sessionClass) {
        return getCommandContext().getSession(sessionClass);
    }

    // Engine scoped

    protected FormEngineConfiguration getFormEngineConfiguration() {
        return formEngineConfiguration;
    }

    protected FormDeploymentEntityManager getDeploymentEntityManager() {
        return getFormEngineConfiguration().getDeploymentEntityManager();
    }

    protected FormDefinitionEntityManager getFormDefinitionEntityManager() {
        return getFormEngineConfiguration().getFormDefinitionEntityManager();
    }

    protected FormResourceEntityManager getResourceEntityManager() {
        return getFormEngineConfiguration().getResourceEntityManager();
    }

}
