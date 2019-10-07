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

import mobius.common.engine.impl.interceptor.Command;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.idm.engine.impl.db.IdmDbSchemaManager;
import mobius.idm.engine.impl.util.CommandContextUtil;

/**
 * @author Tijs Rademakers
 *
 */
public final class SchemaOperationsIdmEngineBuild implements Command<Void> {

    @Override
    public Void execute(CommandContext commandContext) {
        IdmDbSchemaManager idmDbSchemaManager = (IdmDbSchemaManager) CommandContextUtil.getIdmEngineConfiguration(commandContext).getSchemaManager();
        idmDbSchemaManager.performSchemaOperationsIdmEngineBuild();
        return null;
    }
}