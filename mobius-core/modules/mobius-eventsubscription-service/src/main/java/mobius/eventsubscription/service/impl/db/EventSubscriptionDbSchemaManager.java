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
package mobius.eventsubscription.service.impl.db;

import mobius.common.engine.impl.db.ServiceSqlScriptBasedDbSchemaManager;

/**
 *
 */
public class EventSubscriptionDbSchemaManager extends ServiceSqlScriptBasedDbSchemaManager {
    
    private static final String TABLE = "ACT_RU_EVENT_SUBSCR";
    private static final String VERSION_PROPERTY = "eventsubscription.schema.version";
    private static final String SCHEMA_COMPONENT = "eventsubscription";
    
    public EventSubscriptionDbSchemaManager() {
        super(TABLE, SCHEMA_COMPONENT, null, VERSION_PROPERTY);
    }

    @Override
    protected String getResourcesRootDirectory() {
        return "mobius/eventsubscription/service/db/";
    }

}
