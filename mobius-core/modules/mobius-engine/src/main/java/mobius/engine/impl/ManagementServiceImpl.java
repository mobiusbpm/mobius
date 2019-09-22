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
package mobius.engine.impl;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import mobius.common.engine.api.FlowableException;
import mobius.common.engine.api.FlowableIllegalArgumentException;
import mobius.common.engine.api.management.TableMetaData;
import mobius.common.engine.api.management.TablePageQuery;
import mobius.common.engine.impl.cmd.CustomSqlExecution;
import mobius.common.engine.impl.db.DbSqlSession;
import mobius.common.engine.impl.db.DbSqlSessionFactory;
import mobius.common.engine.impl.interceptor.Command;
import mobius.common.engine.impl.interceptor.CommandConfig;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.common.engine.impl.service.CommonEngineServiceImpl;
import mobius.engine.ManagementService;
import mobius.engine.event.EventLogEntry;
import mobius.engine.impl.cfg.ProcessEngineConfigurationImpl;
import mobius.engine.impl.cmd.DeleteEventLogEntry;
import mobius.engine.impl.cmd.ExecuteCustomSqlCmd;
import mobius.engine.impl.cmd.GetEventLogEntriesCmd;
import mobius.engine.impl.cmd.GetPropertiesCmd;
import mobius.engine.impl.cmd.GetTableCountCmd;
import mobius.engine.impl.cmd.GetTableMetaDataCmd;
import mobius.engine.impl.cmd.GetTableNameCmd;
import mobius.engine.impl.cmd.RescheduleTimerJobCmd;
import mobius.engine.impl.util.CommandContextUtil;
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
import mobius.job.service.impl.cmd.DeleteHistoryJobCmd;
import mobius.job.service.impl.cmd.DeleteJobCmd;
import mobius.job.service.impl.cmd.DeleteSuspendedJobCmd;
import mobius.job.service.impl.cmd.DeleteTimerJobCmd;
import mobius.job.service.impl.cmd.ExecuteHistoryJobCmd;
import mobius.job.service.impl.cmd.ExecuteJobCmd;
import mobius.job.service.impl.cmd.GetJobExceptionStacktraceCmd;
import mobius.job.service.impl.cmd.JobType;
import mobius.job.service.impl.cmd.MoveDeadLetterJobToExecutableJobCmd;
import mobius.job.service.impl.cmd.MoveJobToDeadLetterJobCmd;
import mobius.job.service.impl.cmd.MoveSuspendedJobToExecutableJobCmd;
import mobius.job.service.impl.cmd.MoveTimerToExecutableJobCmd;
import mobius.job.service.impl.cmd.SetJobRetriesCmd;
import mobius.job.service.impl.cmd.SetTimerJobRetriesCmd;

/**
 * @author Tom Baeyens
 * @author Joram Barrez
 * @author Falko Menge
 * @author Saeid Mizaei
 */
public class ManagementServiceImpl extends CommonEngineServiceImpl<ProcessEngineConfigurationImpl> implements ManagementService {
    
    @Override
    public Map<String, Long> getTableCount() {
        return commandExecutor.execute(new GetTableCountCmd());
    }

    @Override
    public String getTableName(Class<?> entityClass) {
        return commandExecutor.execute(new GetTableNameCmd(entityClass));
    }

    @Override
    public String getTableName(Class<?> entityClass, boolean includePrefix) {
        return commandExecutor.execute(new GetTableNameCmd(entityClass, includePrefix));
    }

    @Override
    public TableMetaData getTableMetaData(String tableName) {
        return commandExecutor.execute(new GetTableMetaDataCmd(tableName));
    }

    @Override
    public void executeJob(String jobId) {
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
    public Job moveSuspendedJobToExecutableJob(String jobId) {
        return commandExecutor.execute(new MoveSuspendedJobToExecutableJobCmd(jobId));
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
    public void deleteHistoryJob(String jobId) {
        commandExecutor.execute(new DeleteHistoryJobCmd(jobId));
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
    public Job rescheduleTimeDateJob(String jobId, String timeDate) {
        return commandExecutor.execute(new RescheduleTimerJobCmd(jobId, timeDate, null, null, null, null));
    }

    @Override
    public Job rescheduleTimeDurationJob(String jobId, String timeDuration) {
        return commandExecutor.execute(new RescheduleTimerJobCmd(jobId, null, timeDuration, null, null, null));
    }

    @Override
    public Job rescheduleTimeCycleJob(String jobId, String timeCycle) {
        return commandExecutor.execute(new RescheduleTimerJobCmd(jobId, null, null, timeCycle, null, null));
    }

    @Override
    public Job rescheduleTimerJob(String jobId, String timeDate, String timeDuration, String timeCycle, String endDate, String calendarName) {
        return commandExecutor.execute(new RescheduleTimerJobCmd(jobId, timeDate, timeDuration, timeCycle, endDate, calendarName));
    }

    @Override
    public TablePageQuery createTablePageQuery() {
        return new TablePageQueryImpl(commandExecutor);
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
    public HistoryJobQuery createHistoryJobQuery() {
        return new HistoryJobQueryImpl(commandExecutor);
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
    public Map<String, String> getProperties() {
        return commandExecutor.execute(new GetPropertiesCmd());
    }

    @Override
    public String databaseSchemaUpgrade(final Connection connection, final String catalog, final String schema) {
        CommandConfig config = commandExecutor.getDefaultConfig().transactionNotSupported();
        return commandExecutor.execute(config, new Command<String>() {
            @Override
            public String execute(CommandContext commandContext) {
                DbSqlSessionFactory dbSqlSessionFactory = (DbSqlSessionFactory) commandContext.getSessionFactories().get(DbSqlSession.class);
                DbSqlSession dbSqlSession = new DbSqlSession(dbSqlSessionFactory, CommandContextUtil.getEntityCache(commandContext), connection, catalog, schema);
                commandContext.getSessions().put(DbSqlSession.class, dbSqlSession);
                return CommandContextUtil.getProcessEngineConfiguration(commandContext).getSchemaManager().schemaUpdate();
            }
        });
    }

    @Override
    public <T> T executeCommand(Command<T> command) {
        if (command == null) {
            throw new FlowableIllegalArgumentException("The command is null");
        }
        return commandExecutor.execute(command);
    }

    @Override
    public <T> T executeCommand(CommandConfig config, Command<T> command) {
        if (config == null) {
            throw new FlowableIllegalArgumentException("The config is null");
        }
        if (command == null) {
            throw new FlowableIllegalArgumentException("The command is null");
        }
        return commandExecutor.execute(config, command);
    }

    @Override
    public <MapperType, ResultType> ResultType executeCustomSql(CustomSqlExecution<MapperType, ResultType> customSqlExecution) {
        Class<MapperType> mapperClass = customSqlExecution.getMapperClass();
        return commandExecutor.execute(new ExecuteCustomSqlCmd<>(mapperClass, customSqlExecution));
    }

    @Override
    public List<EventLogEntry> getEventLogEntries(Long startLogNr, Long pageSize) {
        return commandExecutor.execute(new GetEventLogEntriesCmd(startLogNr, pageSize));
    }

    @Override
    public List<EventLogEntry> getEventLogEntriesByProcessInstanceId(String processInstanceId) {
        return commandExecutor.execute(new GetEventLogEntriesCmd(processInstanceId));
    }

    @Override
    public void deleteEventLogEntry(long logNr) {
        commandExecutor.execute(new DeleteEventLogEntry(logNr));
    }

}