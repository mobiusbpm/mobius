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
package mobius.engine.test.jobexecutor;

import mobius.common.engine.impl.interceptor.Command;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.common.engine.impl.interceptor.CommandExecutor;
import mobius.engine.impl.test.PluggableFlowableTestCase;
import mobius.engine.impl.util.CommandContextUtil;
import mobius.job.api.Job;
import mobius.job.service.impl.persistence.entity.JobEntity;
import mobius.job.service.impl.persistence.entity.JobEntityImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 */
public class JobExecutorCmdExceptionTest extends PluggableFlowableTestCase {

    protected TweetExceptionHandler tweetExceptionHandler = new TweetExceptionHandler();

    private CommandExecutor commandExecutor;

    @BeforeEach
    public void setUp() throws Exception {
        processEngineConfiguration.addJobHandler(tweetExceptionHandler);
        this.commandExecutor = processEngineConfiguration.getCommandExecutor();
    }

    @AfterEach
    public void tearDown() throws Exception {
        processEngineConfiguration.removeJobHandler(tweetExceptionHandler.getType());
    }

    @Test
    public void testJobCommandsWith2Exceptions() {
        commandExecutor.execute(new Command<String>() {

            @Override
            public String execute(CommandContext commandContext) {
                JobEntity message = createTweetExceptionMessage();
                CommandContextUtil.getJobService(commandContext).scheduleAsyncJob(message);
                return message.getId();
            }
        });

        Job job = managementService.createJobQuery().singleResult();
        assertEquals(3, job.getRetries());

        try {
            managementService.executeJob(job.getId());
            fail("exception expected");
        } catch (Exception e) {
            // exception expected;
        }

        job = managementService.createTimerJobQuery().singleResult();
        assertEquals(2, job.getRetries());

        try {
            managementService.moveTimerToExecutableJob(job.getId());
            managementService.executeJob(job.getId());
            fail("exception expected");
        } catch (Exception e) {
            // exception expected;
        }

        job = managementService.createTimerJobQuery().singleResult();
        assertEquals(1, job.getRetries());

        managementService.moveTimerToExecutableJob(job.getId());
        managementService.executeJob(job.getId());
    }

    @Test
    public void testJobCommandsWith3Exceptions() {
        tweetExceptionHandler.setExceptionsRemaining(3);

        commandExecutor.execute(new Command<String>() {

            @Override
            public String execute(CommandContext commandContext) {
                JobEntity message = createTweetExceptionMessage();
                CommandContextUtil.getJobService(commandContext).scheduleAsyncJob(message);
                return message.getId();
            }
        });

        Job job = managementService.createJobQuery().singleResult();
        assertEquals(3, job.getRetries());

        try {
            managementService.executeJob(job.getId());
            fail("exception expected");
        } catch (Exception e) {
            // exception expected;
        }

        job = managementService.createTimerJobQuery().singleResult();
        assertEquals(2, job.getRetries());

        try {
            managementService.moveTimerToExecutableJob(job.getId());
            managementService.executeJob(job.getId());
            fail("exception expected");
        } catch (Exception e) {
            // exception expected;
        }

        job = managementService.createTimerJobQuery().singleResult();
        assertEquals(1, job.getRetries());

        try {
            managementService.moveTimerToExecutableJob(job.getId());
            managementService.executeJob(job.getId());
            fail("exception expected");
        } catch (Exception e) {
            // exception expected;
        }

        job = managementService.createDeadLetterJobQuery().singleResult();
        assertNotNull(job);

        managementService.deleteDeadLetterJob(job.getId());
    }

    protected JobEntity createTweetExceptionMessage() {
        JobEntity message = new JobEntityImpl();
        message.setJobType(JobEntity.JOB_TYPE_MESSAGE);
        message.setRetries(3);
        message.setJobHandlerType("tweet-exception");
        return message;
    }
}
