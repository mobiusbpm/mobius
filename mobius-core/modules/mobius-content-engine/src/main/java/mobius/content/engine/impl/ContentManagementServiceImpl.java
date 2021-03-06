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
package mobius.content.engine.impl;

import java.util.Map;

import mobius.common.engine.api.management.TableMetaData;
import mobius.common.engine.api.management.TablePageQuery;
import mobius.common.engine.impl.cmd.CustomSqlExecution;
import mobius.common.engine.impl.service.CommonEngineServiceImpl;
import mobius.content.api.ContentManagementService;
import mobius.content.engine.ContentEngineConfiguration;
import mobius.content.engine.impl.cmd.ExecuteCustomSqlCmd;
import mobius.content.engine.impl.cmd.GetTableCountCmd;
import mobius.content.engine.impl.cmd.GetTableMetaDataCmd;
import mobius.content.engine.impl.cmd.GetTableNameCmd;

/**
 *
 */
public class ContentManagementServiceImpl extends CommonEngineServiceImpl<ContentEngineConfiguration> implements ContentManagementService {

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

    public <MapperType, ResultType> ResultType executeCustomSql(CustomSqlExecution<MapperType, ResultType> customSqlExecution) {
        Class<MapperType> mapperClass = customSqlExecution.getMapperClass();
        return commandExecutor.execute(new ExecuteCustomSqlCmd<>(mapperClass, customSqlExecution));
    }

}
