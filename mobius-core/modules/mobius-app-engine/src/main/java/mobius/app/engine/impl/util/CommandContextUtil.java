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
package mobius.app.engine.impl.util;

import mobius.app.api.AppRepositoryService;
import mobius.app.engine.AppEngineConfiguration;
import mobius.app.engine.impl.persistence.entity.AppDefinitionEntityManager;
import mobius.app.engine.impl.persistence.entity.AppDeploymentEntityManager;
import mobius.app.engine.impl.persistence.entity.AppResourceEntityManager;
import mobius.app.engine.impl.persistence.entity.data.TableDataManager;
import mobius.common.engine.api.delegate.event.FlowableEventDispatcher;
import mobius.common.engine.impl.context.Context;
import mobius.common.engine.impl.db.DbSqlSession;
import mobius.common.engine.impl.el.ExpressionManager;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.common.engine.impl.interceptor.EngineConfigurationConstants;
import mobius.common.engine.impl.persistence.cache.EntityCache;
import mobius.identitylink.service.IdentityLinkService;
import mobius.identitylink.service.IdentityLinkServiceConfiguration;
import mobius.idm.api.IdmEngineConfigurationApi;
import mobius.idm.api.IdmIdentityService;
import mobius.variable.service.VariableService;
import mobius.variable.service.VariableServiceConfiguration;

/**
 *
 * @author Tijs Rademakers
 */
public class CommandContextUtil {

    public static final String ATTRIBUTE_INVOLVED_CASE_INSTANCE_IDS = "ctx.attribute.involvedCaseInstanceIds";

    public static AppEngineConfiguration getAppEngineConfiguration() {
        return getAppEngineConfiguration(getCommandContext());
    }

    public static AppEngineConfiguration getAppEngineConfiguration(CommandContext commandContext) {
        return (AppEngineConfiguration) commandContext.getEngineConfigurations().get(EngineConfigurationConstants.KEY_APP_ENGINE_CONFIG);
    }

    public static AppRepositoryService getAppRepositoryService() {
        return getAppEngineConfiguration().getAppRepositoryService();
    }

    public static ExpressionManager getExpressionManager() {
        return getExpressionManager(getCommandContext());
    }

    public static ExpressionManager getExpressionManager(CommandContext commandContext) {
        return getAppEngineConfiguration(commandContext).getExpressionManager();
    }
    
    public static FlowableEventDispatcher getEventDispatcher() {
        return getEventDispatcher(getCommandContext());
    }
    
    public static FlowableEventDispatcher getEventDispatcher(CommandContext commandContext) {
        return getAppEngineConfiguration(commandContext).getEventDispatcher();
    }

    public static AppDeploymentEntityManager getAppDeploymentEntityManager() {
        return getAppDeploymentEntityManager(getCommandContext());
    }

    public static AppDeploymentEntityManager getAppDeploymentEntityManager(CommandContext commandContext) {
        return getAppEngineConfiguration(commandContext).getAppDeploymentEntityManager();
    }

    public static AppResourceEntityManager getAppResourceEntityManager() {
        return getAppResourceEntityManager(getCommandContext());
    }

    public static AppResourceEntityManager getAppResourceEntityManager(CommandContext commandContext) {
        return getAppEngineConfiguration(commandContext).getAppResourceEntityManager();
    }

    public static AppDefinitionEntityManager getAppDefinitionEntityManager() {
        return getAppDefinitionEntityManager(getCommandContext());
    }

    public static AppDefinitionEntityManager getAppDefinitionEntityManager(CommandContext commandContext) {
        return getAppEngineConfiguration(commandContext).getAppDefinitionEntityManager();
    }

    public static TableDataManager getTableDataManager() {
        return getTableDataManager(getCommandContext());
    }

    public static TableDataManager getTableDataManager(CommandContext commandContext) {
        return getAppEngineConfiguration(commandContext).getTableDataManager();
    }

    public static VariableService getVariableService() {
        return getVariableService(getCommandContext());
    }

    public static VariableService getVariableService(CommandContext commandContext) {
        VariableService variableService = null;
        VariableServiceConfiguration variableServiceConfiguration = getVariableServiceConfiguration(commandContext);
        if (variableServiceConfiguration != null) {
            variableService = variableServiceConfiguration.getVariableService();
        }
        return variableService;
    }

    // IDM ENGINE

    public static IdmEngineConfigurationApi getIdmEngineConfiguration() {
        return getIdmEngineConfiguration(getCommandContext());
    }

    public static IdmEngineConfigurationApi getIdmEngineConfiguration(CommandContext commandContext) {
        return (IdmEngineConfigurationApi) commandContext.getEngineConfigurations().get(EngineConfigurationConstants.KEY_IDM_ENGINE_CONFIG);
    }

    public static IdmIdentityService getIdmIdentityService() {
        IdmIdentityService identityService = null;
        IdmEngineConfigurationApi idmEngineConfiguration = getIdmEngineConfiguration();
        if (idmEngineConfiguration != null) {
            identityService = idmEngineConfiguration.getIdmIdentityService();
        }

        return identityService;
    }

    public static IdentityLinkServiceConfiguration getIdentityLinkServiceConfiguration() {
        return getIdentityLinkServiceConfiguration(getCommandContext());
    }

    public static IdentityLinkServiceConfiguration getIdentityLinkServiceConfiguration(CommandContext commandContext) {
        return (IdentityLinkServiceConfiguration) commandContext.getCurrentEngineConfiguration().getServiceConfigurations()
                        .get(EngineConfigurationConstants.KEY_IDENTITY_LINK_SERVICE_CONFIG);
    }

    public static IdentityLinkService getIdentityLinkService() {
        return getIdentityLinkService(getCommandContext());
    }

    public static IdentityLinkService getIdentityLinkService(CommandContext commandContext) {
        return getIdentityLinkServiceConfiguration(commandContext).getIdentityLinkService();
    }
    
    public static VariableServiceConfiguration getVariableServiceConfiguration() {
        return getVariableServiceConfiguration(getCommandContext());
    }

    public static VariableServiceConfiguration getVariableServiceConfiguration(CommandContext commandContext) {
        return (VariableServiceConfiguration) commandContext.getCurrentEngineConfiguration().getServiceConfigurations()
                        .get(EngineConfigurationConstants.KEY_VARIABLE_SERVICE_CONFIG);
    }

    public static DbSqlSession getDbSqlSession() {
        return getDbSqlSession(getCommandContext());
    }

    public static DbSqlSession getDbSqlSession(CommandContext commandContext) {
        return commandContext.getSession(DbSqlSession.class);
    }

    public static EntityCache getEntityCache() {
        return getEntityCache(getCommandContext());
    }

    public static EntityCache getEntityCache(CommandContext commandContext) {
        return commandContext.getSession(EntityCache.class);
    }

    public static CommandContext getCommandContext() {
        return Context.getCommandContext();
    }

}
