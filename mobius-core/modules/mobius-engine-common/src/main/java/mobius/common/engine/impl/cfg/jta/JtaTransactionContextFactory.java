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

package mobius.common.engine.impl.cfg.jta;

import javax.transaction.TransactionManager;

import mobius.common.engine.impl.cfg.TransactionContext;
import mobius.common.engine.impl.cfg.TransactionContextFactory;
import mobius.common.engine.impl.interceptor.CommandContext;

/**
 * @author Daniel Meyer
 */
public class JtaTransactionContextFactory implements TransactionContextFactory {

    protected final TransactionManager transactionManager;

    public JtaTransactionContextFactory(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    public TransactionContext openTransactionContext(CommandContext commandContext) {
        return new JtaTransactionContext(transactionManager);
    }

}
