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
package mobius.job.service.impl.asyncexecutor;

import mobius.common.engine.impl.cfg.TransactionListener;
import mobius.common.engine.impl.cfg.TransactionPropagation;
import mobius.common.engine.impl.interceptor.Command;
import mobius.common.engine.impl.interceptor.CommandConfig;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.common.engine.impl.interceptor.CommandExecutor;
import mobius.job.service.impl.persistence.entity.JobInfoEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class JobAddedTransactionListener implements TransactionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobAddedTransactionListener.class);

    protected JobInfoEntity job;
    protected AsyncExecutor asyncExecutor;
    protected CommandExecutor commandExecutor;

    public JobAddedTransactionListener(JobInfoEntity job, AsyncExecutor asyncExecutor, CommandExecutor commandExecutor) {
        this.job = job;
        this.asyncExecutor = asyncExecutor;
        this.commandExecutor = commandExecutor;
    }

    @Override
    public void execute(CommandContext commandContext) {
        CommandConfig commandConfig = new CommandConfig(false, TransactionPropagation.REQUIRES_NEW);
        commandExecutor.execute(commandConfig, new Command<Void>() {
            @Override
            public Void execute(CommandContext commandContext) {
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("notifying job executor of new job");
                }
                asyncExecutor.executeAsyncJob(job);
                return null;
            }
        });
    }
}
