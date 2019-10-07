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
package mobius.job.service.impl.history.async;

import mobius.common.engine.impl.cfg.TransactionListener;
import mobius.common.engine.impl.cfg.TransactionPropagation;
import mobius.common.engine.impl.interceptor.Command;
import mobius.common.engine.impl.interceptor.CommandConfig;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.job.service.JobServiceConfiguration;
import mobius.job.service.impl.persistence.entity.HistoryJobEntity;
import mobius.job.service.impl.util.CommandContextUtil;

/**
 * A {@link TransactionListener} that will, typically on post-commit, trigger 
 * the async history executor to execute the provided list of {@link HistoryJobEntity} instances. 
 * 
 *
 */
public class TriggerAsyncHistoryExecutorTransactionListener implements TransactionListener {
    
    protected HistoryJobEntity historyJobEntity;
    
    protected  JobServiceConfiguration jobServiceConfiguration;
    
    public TriggerAsyncHistoryExecutorTransactionListener(CommandContext commandContext) {
        this(commandContext,null);
    }
    
    public TriggerAsyncHistoryExecutorTransactionListener(CommandContext commandContext, HistoryJobEntity historyJobEntity) {
        // The execution of this listener will reference components that might 
        // not be available when the command context is closing (when typically 
        // the history jobs are created and scheduled), so they are already referenced here.
        this.jobServiceConfiguration = CommandContextUtil.getJobServiceConfiguration(commandContext);
        this.historyJobEntity = historyJobEntity;
    }
    
    @Override
    public void execute(CommandContext commandContext) {
        jobServiceConfiguration.getCommandExecutor().execute(new CommandConfig(false, TransactionPropagation.REQUIRES_NEW), new Command<Void>() {
            @Override
            public Void execute(CommandContext commandContext) {
                jobServiceConfiguration.getAsyncHistoryExecutor().executeAsyncJob(historyJobEntity);
                return null;
            }
        });
    }

}
