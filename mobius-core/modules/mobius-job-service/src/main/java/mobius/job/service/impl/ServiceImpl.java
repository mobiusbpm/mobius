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
package mobius.job.service.impl;

import mobius.common.engine.api.delegate.event.FlowableEventDispatcher;
import mobius.common.engine.impl.interceptor.CommandExecutor;
import mobius.common.engine.impl.service.CommonServiceImpl;
import mobius.job.service.JobServiceConfiguration;
import mobius.job.service.impl.asyncexecutor.JobManager;
import mobius.job.service.impl.persistence.entity.DeadLetterJobEntityManager;
import mobius.job.service.impl.persistence.entity.HistoryJobEntityManager;
import mobius.job.service.impl.persistence.entity.JobEntityManager;
import mobius.job.service.impl.persistence.entity.SuspendedJobEntityManager;
import mobius.job.service.impl.persistence.entity.TimerJobEntityManager;

/**
 *
 */
public class ServiceImpl extends CommonServiceImpl<JobServiceConfiguration> {

    public ServiceImpl() {

    }

    public ServiceImpl(JobServiceConfiguration configuration) {
        super(configuration);
    }
    
    public FlowableEventDispatcher getEventDispatcher() {
        return configuration.getEventDispatcher();
    }
    
    public JobManager getJobManager() {
        return configuration.getJobManager();
    }

    public JobEntityManager getJobEntityManager() {
        return configuration.getJobEntityManager();
    }
    
    public DeadLetterJobEntityManager getDeadLetterJobEntityManager() {
        return configuration.getDeadLetterJobEntityManager();
    }
    
    public SuspendedJobEntityManager getSuspendedJobEntityManager() {
        return configuration.getSuspendedJobEntityManager();
    }
    
    public TimerJobEntityManager getTimerJobEntityManager() {
        return configuration.getTimerJobEntityManager();
    }
    
    public HistoryJobEntityManager getHistoryJobEntityManager() {
        return configuration.getHistoryJobEntityManager();
    }
    
    public CommandExecutor getCommandExecutor() {
        return configuration.getCommandExecutor();
    }
}
