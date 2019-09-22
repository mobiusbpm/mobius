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

import java.io.Serializable;

import mobius.common.engine.api.FlowableIllegalArgumentException;
import mobius.common.engine.api.FlowableObjectNotFoundException;
import mobius.common.engine.api.delegate.event.FlowableEngineEventType;
import mobius.common.engine.api.delegate.event.FlowableEventDispatcher;
import mobius.common.engine.impl.interceptor.Command;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.job.api.Job;
import mobius.job.service.event.impl.FlowableJobEventBuilder;
import mobius.job.service.impl.persistence.entity.HistoryJobEntity;
import mobius.job.service.impl.util.CommandContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Tijs Rademakers
 */

public class DeleteHistoryJobCmd implements Command<Object>, Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteHistoryJobCmd.class);
    private static final long serialVersionUID = 1L;

    protected String historyJobId;

    public DeleteHistoryJobCmd(String historyJobId) {
        this.historyJobId = historyJobId;
    }

    @Override
    public Object execute(CommandContext commandContext) {
        HistoryJobEntity jobToDelete = getJobToDelete(commandContext);

        sendCancelEvent(jobToDelete);

        CommandContextUtil.getHistoryJobEntityManager(commandContext).delete(jobToDelete);
        return null;
    }

    protected void sendCancelEvent(HistoryJobEntity jobToDelete) {
        FlowableEventDispatcher eventDispatcher = CommandContextUtil.getJobServiceConfiguration().getEventDispatcher();
        if (eventDispatcher != null && eventDispatcher.isEnabled()) {
            eventDispatcher
                .dispatchEvent(FlowableJobEventBuilder.createEntityEvent(FlowableEngineEventType.JOB_CANCELED, jobToDelete));
        }
    }

    protected HistoryJobEntity getJobToDelete(CommandContext commandContext) {
        if (historyJobId == null) {
            throw new FlowableIllegalArgumentException("jobId is null");
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Deleting job {}", historyJobId);
        }

        HistoryJobEntity job = CommandContextUtil.getHistoryJobEntityManager(commandContext).findById(historyJobId);
        if (job == null) {
            throw new FlowableObjectNotFoundException("No history job found with id '" + historyJobId + "'", Job.class);
        }

        return job;
    }

}
