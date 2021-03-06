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
import mobius.common.engine.impl.interceptor.Command;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.job.api.JobNotFoundException;
import mobius.job.service.impl.persistence.entity.JobEntity;
import mobius.job.service.impl.persistence.entity.TimerJobEntity;
import mobius.job.service.impl.util.CommandContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class MoveTimerToExecutableJobCmd implements Command<JobEntity>, Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LoggerFactory.getLogger(MoveTimerToExecutableJobCmd.class);

    protected String jobId;

    public MoveTimerToExecutableJobCmd(String jobId) {
        this.jobId = jobId;
    }

    @Override
    public JobEntity execute(CommandContext commandContext) {

        if (jobId == null) {
            throw new FlowableIllegalArgumentException("jobId and job is null");
        }

        TimerJobEntity timerJob = CommandContextUtil.getTimerJobEntityManager(commandContext).findById(jobId);

        if (timerJob == null) {
            throw new JobNotFoundException(jobId);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Executing timer job {}", timerJob.getId());
        }

        return CommandContextUtil.getJobManager(commandContext).moveTimerJobToExecutableJob(timerJob);
    }

    public String getJobId() {
        return jobId;
    }

}
