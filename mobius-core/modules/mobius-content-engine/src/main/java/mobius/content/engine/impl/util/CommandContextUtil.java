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
package mobius.content.engine.impl.util;

import mobius.common.engine.impl.context.Context;
import mobius.common.engine.impl.db.DbSqlSession;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.common.engine.impl.interceptor.EngineConfigurationConstants;
import mobius.content.engine.ContentEngineConfiguration;
import mobius.content.engine.impl.persistence.entity.ContentItemEntityManager;
import mobius.content.engine.impl.persistence.entity.TableDataManager;

public class CommandContextUtil {
    
    public static ContentEngineConfiguration getContentEngineConfiguration() {
        return getContentEngineConfiguration(getCommandContext());
    }
    
    public static ContentEngineConfiguration getContentEngineConfiguration(CommandContext commandContext) {
        if (commandContext != null) {
            return (ContentEngineConfiguration) commandContext.getEngineConfigurations().get(EngineConfigurationConstants.KEY_CONTENT_ENGINE_CONFIG);
        }
        return null;
    }
    
    public static DbSqlSession getDbSqlSession() {
        return getDbSqlSession(getCommandContext());
    }
    
    public static DbSqlSession getDbSqlSession(CommandContext commandContext) {
        return commandContext.getSession(DbSqlSession.class);
    }
    
    public static TableDataManager getTableDataManager() {
        return getTableDataManager(getCommandContext());
    }
    
    public static TableDataManager getTableDataManager(CommandContext commandContext) {
        return getContentEngineConfiguration(commandContext).getTableDataManager();
    }
    
    public static ContentItemEntityManager getContentItemEntityManager() {
        return getContentItemEntityManager(getCommandContext());
    }
    
    public static ContentItemEntityManager getContentItemEntityManager(CommandContext commandContext) {
        return getContentEngineConfiguration(commandContext).getContentItemEntityManager();
    }
    
    public static CommandContext getCommandContext() {
        return Context.getCommandContext();
    }

}
