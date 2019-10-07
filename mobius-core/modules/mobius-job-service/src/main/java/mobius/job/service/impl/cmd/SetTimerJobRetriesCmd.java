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
import mobius.job.service.impl.persistence.entity.TimerJobEntity;
import mobius.job.service.impl.util.CommandContextUtil;

/**
 *
 */
public class SetTimerJobRetriesCmd implements Command<Void>, Serializable {

    private static final long serialVersionUID = 1L;

    private final String jobId;
    private final int retries;

    public SetTimerJobRetriesCmd(String jobId, int retries) {
        if (jobId == null || jobId.length() < 1) {
            throw new FlowableIllegalArgumentException("The job id is mandatory, but '" + jobId + "' has been provided.");
        }
        if (retries < 0) {
            throw new FlowableIllegalArgumentException("The number of job retries must be a non-negative Integer, but '" + retries + "' has been provided.");
        }
        this.jobId = jobId;
        this.retries = retries;
    }

    @Override
    public Void execute(CommandContext commandContext) {
        TimerJobEntity job = CommandContextUtil.getTimerJobEntityManager(commandContext).findById(jobId);
        if (job != null) {

            job.setRetries(retries);

            FlowableEventDispatcher eventDispatcher = CommandContextUtil.getEventDispatcher(commandContext);
            if (eventDispatcher != null && eventDispatcher.isEnabled()) {
                eventDispatcher.dispatchEvent(FlowableJobEventBuilder.createEntityEvent(FlowableEngineEventType.ENTITY_UPDATED, job));
            }
        } else {
            throw new FlowableObjectNotFoundException("No timer job found with id '" + jobId + "'.", Job.class);
        }
        return null;
    }
}
