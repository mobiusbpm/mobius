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
package mobius.engine.impl.bpmn.listener;

import mobius.common.engine.impl.cfg.TransactionListener;
import mobius.common.engine.impl.cfg.TransactionPropagation;
import mobius.common.engine.impl.interceptor.Command;
import mobius.common.engine.impl.interceptor.CommandConfig;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.common.engine.impl.interceptor.CommandExecutor;
import mobius.engine.delegate.ExecutionListener;
import mobius.engine.delegate.TransactionDependentExecutionListener;

/**
 * A {@link TransactionListener} that invokes an {@link ExecutionListener}.
 * 
 *
 */
public class ExecuteExecutionListenerTransactionListener implements TransactionListener {

    protected TransactionDependentExecutionListener listener;
    protected TransactionDependentExecutionListenerExecutionScope scope;
    protected CommandExecutor commandExecutor;

    public ExecuteExecutionListenerTransactionListener(TransactionDependentExecutionListener listener,
            TransactionDependentExecutionListenerExecutionScope scope, CommandExecutor commandExecutor) {
        this.listener = listener;
        this.scope = scope;
        this.commandExecutor = commandExecutor;
    }

    @Override
    public void execute(CommandContext commandContext) {
        CommandConfig commandConfig = new CommandConfig(false, TransactionPropagation.REQUIRES_NEW);
        commandExecutor.execute(commandConfig, new Command<Void>() {
            @Override
            public Void execute(CommandContext commandContext) {
                listener.notify(scope.getProcessInstanceId(), scope.getExecutionId(), scope.getFlowElement(),
                        scope.getExecutionVariables(), scope.getCustomPropertiesMap());
                return null;
            }
        });
    }

}
