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
package mobius.variable.service.impl.db;

import mobius.common.engine.impl.db.ServiceSqlScriptBasedDbSchemaManager;

/**
 *
 */
public class VariableDbSchemaManager extends ServiceSqlScriptBasedDbSchemaManager {
    
    private static final String VARIABLE_TABLE = "ACT_RU_VARIABLE";
    private static final String VARIABLE_VERSION_PROPERTY = "variable.schema.version";
    private static final String SCHEMA_COMPONENT = "variable";
    private static final String SCHEMA_COMPONENT_HISTORY = "variable.history";
    
    public VariableDbSchemaManager() {
        super(VARIABLE_TABLE, SCHEMA_COMPONENT, SCHEMA_COMPONENT_HISTORY, VARIABLE_VERSION_PROPERTY);
    }

    @Override
    protected String getResourcesRootDirectory() {
        return "mobius/variable/service/db/";
    }
    
}
