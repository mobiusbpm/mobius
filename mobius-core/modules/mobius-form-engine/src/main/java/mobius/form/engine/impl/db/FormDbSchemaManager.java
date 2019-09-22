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
package mobius.form.engine.impl.db;

import mobius.common.engine.impl.db.EngineDatabaseConfiguration;
import mobius.common.engine.impl.db.LiquibaseBasedSchemaManager;
import mobius.common.engine.impl.db.LiquibaseDatabaseConfiguration;
import mobius.form.engine.FormEngineConfiguration;
import mobius.form.engine.impl.util.CommandContextUtil;

public class FormDbSchemaManager extends LiquibaseBasedSchemaManager {
    
    public static String LIQUIBASE_CHANGELOG = "mobius/form/db/liquibase/flowable-form-db-changelog.xml";

    public FormDbSchemaManager() {
        super("form", LIQUIBASE_CHANGELOG, FormEngineConfiguration.LIQUIBASE_CHANGELOG_PREFIX);
    }

    @Override
    protected LiquibaseDatabaseConfiguration getDatabaseConfiguration() {
        return new EngineDatabaseConfiguration(CommandContextUtil.getFormEngineConfiguration());
    }

    public void initSchema(FormEngineConfiguration formEngineConfiguration) {
        initSchema(formEngineConfiguration.getDatabaseSchemaUpdate());
    }

}
