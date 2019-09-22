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

package mobius.app.engine.impl;

import mobius.app.engine.impl.persistence.entity.AppDefinitionEntityManager;
import mobius.app.engine.impl.persistence.entity.AppDeploymentEntityManager;
import mobius.app.engine.impl.persistence.entity.AppResourceEntityManager;
import mobius.app.engine.AppEngineConfiguration;
import mobius.common.engine.impl.context.Context;
import mobius.common.engine.impl.db.DbSqlSession;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.identitylink.service.impl.persistence.entity.IdentityLinkEntityManager;
import mobius.variable.service.impl.persistence.entity.VariableInstanceEntityManager;

/**
 * @author Joram Barrez
 */
public abstract class AbstractCmmnManager {

    protected AppEngineConfiguration appEngineConfiguration;

    public AbstractCmmnManager(AppEngineConfiguration appEngineConfiguration) {
        this.appEngineConfiguration = appEngineConfiguration;
    }

    protected CommandContext getCommandContext() {
        return Context.getCommandContext();
    }

    protected <T> T getSession(Class<T> sessionClass) {
        return getCommandContext().getSession(sessionClass);
    }
    
    protected DbSqlSession getDbSqlSession() {
        return getSession(DbSqlSession.class);
    }
    
    protected AppResourceEntityManager getAppResourceEntityManager() {
        return appEngineConfiguration.getAppResourceEntityManager();
    }
    
    protected AppDeploymentEntityManager getAppDeploymentEntityManager() {
        return appEngineConfiguration.getAppDeploymentEntityManager();
    }
    
    protected AppDefinitionEntityManager getAppDefinitionEntityManager() {
        return appEngineConfiguration.getAppDefinitionEntityManager();
    }
    
    protected VariableInstanceEntityManager getVariableInstanceEntityManager() {
        return appEngineConfiguration.getVariableServiceConfiguration().getVariableInstanceEntityManager();
    }
    
    protected IdentityLinkEntityManager getIdentityLinkEntityManager() {
        return appEngineConfiguration.getIdentityLinkServiceConfiguration().getIdentityLinkEntityManager();
    }
    
    protected AppEngineConfiguration getappEngineConfiguration() {
        return appEngineConfiguration;
    }

}
