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
package mobius.idm.engine.impl;

import java.sql.Connection;
import java.util.Map;

import mobius.common.engine.api.management.TableMetaData;
import mobius.common.engine.api.management.TablePageQuery;
import mobius.common.engine.impl.cmd.CustomSqlExecution;
import mobius.common.engine.impl.interceptor.Command;
import mobius.common.engine.impl.interceptor.CommandConfig;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.common.engine.impl.service.CommonEngineServiceImpl;
import mobius.idm.api.IdmManagementService;
import mobius.idm.engine.IdmEngineConfiguration;
import mobius.idm.engine.impl.cmd.ExecuteCustomSqlCmd;
import mobius.idm.engine.impl.cmd.GetPropertiesCmd;
import mobius.idm.engine.impl.cmd.GetTableCountCmd;
import mobius.idm.engine.impl.cmd.GetTableMetaDataCmd;
import mobius.idm.engine.impl.cmd.GetTableNameCmd;
import mobius.idm.engine.impl.util.CommandContextUtil;

/**
 * @author Tijs Rademakers
 */
public class IdmManagementServiceImpl extends CommonEngineServiceImpl<IdmEngineConfiguration> implements IdmManagementService {

    @Override
    public Map<String, Long> getTableCount() {
        return commandExecutor.execute(new GetTableCountCmd());
    }

    @Override
    public String getTableName(Class<?> entityClass) {
        return commandExecutor.execute(new GetTableNameCmd(entityClass));
    }

    @Override
    public TableMetaData getTableMetaData(String tableName) {
        return commandExecutor.execute(new GetTableMetaDataCmd(tableName));
    }

    @Override
    public TablePageQuery createTablePageQuery() {
        return new TablePageQueryImpl(commandExecutor);
    }

    @Override
    public Map<String, String> getProperties() {
        return commandExecutor.execute(new GetPropertiesCmd());
    }

    @Override
    public String databaseSchemaUpgrade(final Connection connection, final String catalog, final String schema) {
        CommandConfig config = commandExecutor.getDefaultConfig().transactionNotSupported();
        return commandExecutor.execute(config, new Command<String>() {
            @Override
            public String execute(CommandContext commandContext) {
                return CommandContextUtil.getIdmEngineConfiguration().getSchemaManager().schemaUpdate();
            }
        });
    }

    public <MapperType, ResultType> ResultType executeCustomSql(CustomSqlExecution<MapperType, ResultType> customSqlExecution) {
        Class<MapperType> mapperClass = customSqlExecution.getMapperClass();
        return commandExecutor.execute(new ExecuteCustomSqlCmd<>(mapperClass, customSqlExecution));
    }

}
