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
import mobius.job.api.Job;
import mobius.job.service.InternalJobManager;
import mobius.job.service.impl.util.CommandContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 */
public class UnlockExclusiveJobCmd implements Command<Object>, Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LoggerFactory.getLogger(UnlockExclusiveJobCmd.class);

    protected Job job;

    public UnlockExclusiveJobCmd(Job job) {
        this.job = job;
    }

    @Override
    public Object execute(CommandContext commandContext) {

        if (job == null) {
            throw new FlowableIllegalArgumentException("job is null");
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Unlocking exclusive job {}", job.getId());
        }

        if (job.isExclusive()) {
            if (job.getProcessInstanceId() != null || job.getScopeId() != null) {
                InternalJobManager jobScopeInterface = CommandContextUtil.getJobServiceConfiguration().getInternalJobManager();
                if (jobScopeInterface != null) {
                    jobScopeInterface.clearJobScopeLock(job);
                }
            }
        }

        return null;
    }
}
