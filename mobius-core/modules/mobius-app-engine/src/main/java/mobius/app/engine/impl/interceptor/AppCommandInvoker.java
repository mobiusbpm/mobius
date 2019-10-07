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
package mobius.app.engine.impl.interceptor;

import mobius.common.engine.impl.context.Context;
import mobius.common.engine.impl.interceptor.AbstractCommandInterceptor;
import mobius.common.engine.impl.interceptor.Command;
import mobius.common.engine.impl.interceptor.CommandConfig;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.common.engine.impl.interceptor.CommandInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class AppCommandInvoker extends AbstractCommandInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(AppCommandInvoker.class);

    @SuppressWarnings("unchecked")
    @Override
    public <T> T execute(final CommandConfig config, final Command<T> command) {
        final CommandContext commandContext = Context.getCommandContext();
        commandContext.setResult(command.execute(commandContext));
        
        return (T) commandContext.getResult();
    }

    @Override
    public void setNext(CommandInterceptor next) {
        throw new UnsupportedOperationException("CommandInvoker must be the last interceptor in the chain");
    }
    
}
