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
package mobius.job.service.impl.cmd;

import mobius.common.engine.api.FlowableException;
import mobius.common.engine.api.FlowableIllegalArgumentException;
import mobius.common.engine.impl.interceptor.Command;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.job.api.HistoryJob;
import mobius.job.api.JobNotFoundException;
import mobius.job.service.impl.persistence.entity.HistoryJobEntity;
import mobius.job.service.impl.util.CommandContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Executes a {@link HistoryJob} directly (not through the async history executor).
 * 
 *
 */
public class ExecuteHistoryJobCmd implements Command<Void> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecuteHistoryJobCmd.class);

    protected String historyJobId;

    public ExecuteHistoryJobCmd(String historyJobId) {
        this.historyJobId = historyJobId;
    }

    @Override
    public Void execute(CommandContext commandContext) {
        if (historyJobId == null) {
            throw new FlowableIllegalArgumentException("historyJobId is null");
        }

        HistoryJobEntity historyJobEntity = CommandContextUtil.getHistoryJobEntityManager(commandContext).findById(historyJobId);
        if (historyJobEntity == null) {
            throw new JobNotFoundException(historyJobId);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Executing historyJob {}", historyJobEntity.getId());
        }

        try {
            CommandContextUtil.getJobManager(commandContext).execute(historyJobEntity);
        } catch (Throwable exception) {
            // Finally, Throw the exception to indicate the failure
            throw new FlowableException("HistoryJob " + historyJobId + " failed", exception);
        }

        return null;
    }

}
