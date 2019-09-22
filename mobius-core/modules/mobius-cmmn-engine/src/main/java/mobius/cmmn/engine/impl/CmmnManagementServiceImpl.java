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
package mobius.cmmn.engine.impl;

import java.util.Collection;
import java.util.Map;

import mobius.cmmn.api.CmmnManagementService;
import mobius.cmmn.engine.CmmnEngineConfiguration;
import mobius.cmmn.engine.impl.cmd.GetTableCountsCmd;
import mobius.cmmn.engine.impl.cmd.GetTableNamesCmd;
import mobius.common.engine.api.FlowableException;
import mobius.common.engine.api.FlowableIllegalArgumentException;
import mobius.common.engine.impl.service.CommonEngineServiceImpl;
import mobius.job.api.DeadLetterJobQuery;
import mobius.job.api.HistoryJobQuery;
import mobius.job.api.Job;
import mobius.job.api.JobQuery;
import mobius.job.api.SuspendedJobQuery;
import mobius.job.api.TimerJobQuery;
import mobius.job.service.impl.DeadLetterJobQueryImpl;
import mobius.job.service.impl.HistoryJobQueryImpl;
import mobius.job.service.impl.JobQueryImpl;
import mobius.job.service.impl.SuspendedJobQueryImpl;
import mobius.job.service.impl.TimerJobQueryImpl;
import mobius.job.service.impl.cmd.DeleteDeadLetterJobCmd;
import mobius.job.service.impl.cmd.DeleteJobCmd;
import mobius.job.service.impl.cmd.DeleteSuspendedJobCmd;
import mobius.job.service.impl.cmd.DeleteTimerJobCmd;
import mobius.job.service.impl.cmd.ExecuteHistoryJobCmd;
import mobius.job.service.impl.cmd.ExecuteJobCmd;
import mobius.job.service.impl.cmd.GetJobExceptionStacktraceCmd;
import mobius.job.service.impl.cmd.JobType;
import mobius.job.service.impl.cmd.MoveDeadLetterJobToExecutableJobCmd;
import mobius.job.service.impl.cmd.MoveJobToDeadLetterJobCmd;
import mobius.job.service.impl.cmd.MoveTimerToExecutableJobCmd;
import mobius.job.service.impl.cmd.SetJobRetriesCmd;
import mobius.job.service.impl.cmd.SetTimerJobRetriesCmd;

/**
 * @author Joram Barrez
 */
public class CmmnManagementServiceImpl extends CommonEngineServiceImpl<CmmnEngineConfiguration> implements CmmnManagementService {

    public CmmnManagementServiceImpl(CmmnEngineConfiguration engineConfiguration) {
        super(engineConfiguration);
    }

    @Override
    public Map<String, Long> getTableCounts() {
        return commandExecutor.execute(new GetTableCountsCmd());
    }

    @Override
    public Collection<String> getTableNames() {
        return commandExecutor.execute(new GetTableNamesCmd());
    }
    
    @Override
    public void executeJob(String jobId) {
        if (jobId == null) {
            throw new FlowableIllegalArgumentException("Job id is null");
        }

        try {
            commandExecutor.execute(new ExecuteJobCmd(jobId));
        } catch (RuntimeException e) {
            if (e instanceof FlowableException) {
                throw e;
            } else {
                throw new FlowableException("Job " + jobId + " failed", e);
            }
        }
    }
    
    @Override
    public void executeHistoryJob(String historyJobId) {
        commandExecutor.execute(new ExecuteHistoryJobCmd(historyJobId));
    }

    @Override
    public Job moveTimerToExecutableJob(String jobId) {
        return commandExecutor.execute(new MoveTimerToExecutableJobCmd(jobId));
    }

    @Override
    public Job moveJobToDeadLetterJob(String jobId) {
        return commandExecutor.execute(new MoveJobToDeadLetterJobCmd(jobId));
    }

    @Override
    public Job moveDeadLetterJobToExecutableJob(String jobId, int retries) {
        return commandExecutor.execute(new MoveDeadLetterJobToExecutableJobCmd(jobId, retries));
    }

    @Override
    public void deleteJob(String jobId) {
        commandExecutor.execute(new DeleteJobCmd(jobId));
    }

    @Override
    public void deleteTimerJob(String jobId) {
        commandExecutor.execute(new DeleteTimerJobCmd(jobId));
    }
    
    @Override
    public void deleteSuspendedJob(String jobId) {
        commandExecutor.execute(new DeleteSuspendedJobCmd(jobId));
    }

    @Override
    public void deleteDeadLetterJob(String jobId) {
        commandExecutor.execute(new DeleteDeadLetterJobCmd(jobId));
    }
    
    @Override
    public void setJobRetries(String jobId, int retries) {
        commandExecutor.execute(new SetJobRetriesCmd(jobId, retries));
    }

    @Override
    public void setTimerJobRetries(String jobId, int retries) {
        commandExecutor.execute(new SetTimerJobRetriesCmd(jobId, retries));
    }

    @Override
    public JobQuery createJobQuery() {
        return new JobQueryImpl(commandExecutor);
    }

    @Override
    public TimerJobQuery createTimerJobQuery() {
        return new TimerJobQueryImpl(commandExecutor);
    }

    @Override
    public SuspendedJobQuery createSuspendedJobQuery() {
        return new SuspendedJobQueryImpl(commandExecutor);
    }

    @Override
    public DeadLetterJobQuery createDeadLetterJobQuery() {
        return new DeadLetterJobQueryImpl(commandExecutor);
    }
    
    @Override
    public String getJobExceptionStacktrace(String jobId) {
        return commandExecutor.execute(new GetJobExceptionStacktraceCmd(jobId, JobType.ASYNC));
    }

    @Override
    public String getTimerJobExceptionStacktrace(String jobId) {
        return commandExecutor.execute(new GetJobExceptionStacktraceCmd(jobId, JobType.TIMER));
    }

    @Override
    public String getSuspendedJobExceptionStacktrace(String jobId) {
        return commandExecutor.execute(new GetJobExceptionStacktraceCmd(jobId, JobType.SUSPENDED));
    }

    @Override
    public String getDeadLetterJobExceptionStacktrace(String jobId) {
        return commandExecutor.execute(new GetJobExceptionStacktraceCmd(jobId, JobType.DEADLETTER));
    }
    
    @Override
    public HistoryJobQuery createHistoryJobQuery() {
        return new HistoryJobQueryImpl(commandExecutor);
    }

}
