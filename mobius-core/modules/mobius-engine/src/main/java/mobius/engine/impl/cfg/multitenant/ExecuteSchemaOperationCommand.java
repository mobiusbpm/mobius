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
package mobius.engine.impl.cfg.multitenant;

import mobius.common.engine.impl.cfg.multitenant.TenantInfoHolder;
import mobius.common.engine.impl.db.SchemaManager;
import mobius.common.engine.impl.interceptor.Command;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.engine.ProcessEngineConfiguration;
import mobius.engine.impl.cfg.ProcessEngineConfigurationImpl;
import mobius.engine.impl.util.CommandContextUtil;

/**
 * {@link Command} that is used by the {@link MultiSchemaMultiTenantProcessEngineConfiguration} to make sure the 'databaseSchemaUpdate' setting is applied for each tenant datasource.
 * 
 *
 */
public class ExecuteSchemaOperationCommand implements Command<Void> {

    protected String schemaOperation;

    protected TenantInfoHolder tenantInfoHolder;

    public ExecuteSchemaOperationCommand(String schemaOperation) {
        this.schemaOperation = schemaOperation;
    }

    @Override
    public Void execute(CommandContext commandContext) {
        SchemaManager processSchemaManager = CommandContextUtil.getProcessEngineConfiguration(commandContext).getSchemaManager();
        if (ProcessEngineConfigurationImpl.DB_SCHEMA_UPDATE_DROP_CREATE.equals(schemaOperation)) {
            try {
                processSchemaManager.schemaDrop();
            } catch (RuntimeException e) {
                // ignore
            }
        }
        if (mobius.engine.ProcessEngineConfiguration.DB_SCHEMA_UPDATE_CREATE_DROP.equals(schemaOperation)
                || ProcessEngineConfigurationImpl.DB_SCHEMA_UPDATE_DROP_CREATE.equals(schemaOperation)
                || ProcessEngineConfigurationImpl.DB_SCHEMA_UPDATE_CREATE.equals(schemaOperation)) {
            processSchemaManager.schemaCreate();

        } else if (mobius.engine.ProcessEngineConfiguration.DB_SCHEMA_UPDATE_FALSE.equals(schemaOperation)) {
            processSchemaManager.schemaCheckVersion();

        } else if (ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE.equals(schemaOperation)) {
            processSchemaManager.schemaUpdate();
        }

        return null;
    }

}
