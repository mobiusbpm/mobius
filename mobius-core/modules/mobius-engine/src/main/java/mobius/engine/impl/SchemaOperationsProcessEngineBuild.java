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

import mobius.common.engine.impl.db.SchemaManager;
import mobius.common.engine.impl.interceptor.Command;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.engine.ProcessEngineConfiguration;
import mobius.engine.impl.cfg.ProcessEngineConfigurationImpl;
import mobius.engine.impl.util.CommandContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 */
public class SchemaOperationsProcessEngineBuild implements Command<Void> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SchemaOperationsProcessEngineBuild.class);

    @Override
    public Void execute(CommandContext commandContext) {
        
        SchemaManager schemaManager = CommandContextUtil.getProcessEngineConfiguration(commandContext).getSchemaManager();
        String databaseSchemaUpdate = CommandContextUtil.getProcessEngineConfiguration().getDatabaseSchemaUpdate();
        
        LOGGER.debug("Executing schema management with setting {}", databaseSchemaUpdate);
        if (ProcessEngineConfiguration.DB_SCHEMA_UPDATE_DROP_CREATE.equals(databaseSchemaUpdate)) {
            try {
                schemaManager.schemaDrop();
            } catch (RuntimeException e) {
                // ignore
            }
        }
        if (ProcessEngineConfiguration.DB_SCHEMA_UPDATE_CREATE_DROP.equals(databaseSchemaUpdate)
                || ProcessEngineConfigurationImpl.DB_SCHEMA_UPDATE_DROP_CREATE.equals(databaseSchemaUpdate) || ProcessEngineConfigurationImpl.DB_SCHEMA_UPDATE_CREATE.equals(databaseSchemaUpdate)) {
            schemaManager.schemaCreate();

        } else if (ProcessEngineConfiguration.DB_SCHEMA_UPDATE_FALSE.equals(databaseSchemaUpdate)) {
            schemaManager.schemaCheckVersion();

        } else if (ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE.equals(databaseSchemaUpdate)) {
            schemaManager.schemaUpdate();
        }
        
        return null;
    }
}