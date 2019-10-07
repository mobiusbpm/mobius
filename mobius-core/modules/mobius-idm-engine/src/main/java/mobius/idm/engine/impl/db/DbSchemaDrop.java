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

package mobius.idm.engine.impl.db;

import mobius.common.engine.impl.interceptor.Command;
import mobius.common.engine.impl.interceptor.CommandConfig;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.common.engine.impl.interceptor.CommandExecutor;
import mobius.idm.engine.IdmEngine;
import mobius.idm.engine.IdmEngines;
import mobius.idm.engine.impl.util.CommandContextUtil;

/**
 *
 */
public class DbSchemaDrop {

    public static void main(String[] args) {
        IdmEngine idmEngine = IdmEngines.getDefaultIdmEngine();
        CommandExecutor commandExecutor = idmEngine.getIdmEngineConfiguration().getCommandExecutor();
        CommandConfig config = new CommandConfig().transactionNotSupported();
        commandExecutor.execute(config, new Command<Object>() {
            @Override
            public Object execute(CommandContext commandContext) {
                CommandContextUtil.getIdmEngineConfiguration(commandContext).getSchemaManager().schemaDrop();
                return null;
            }
        });
    }
}
