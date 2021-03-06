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
package mobius.engine.test.api.history;

import static org.assertj.core.api.Assertions.assertThat;

import mobius.common.engine.impl.identity.Authentication;
import mobius.engine.impl.cfg.ProcessEngineConfigurationImpl;
import mobius.engine.test.impl.CustomConfigurationFlowableTestCase;
import mobius.task.api.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * @author martin.grofcik
 */
public class HistoryServiceDisableTaskLogTest extends CustomConfigurationFlowableTestCase {

    protected Task task;

    public HistoryServiceDisableTaskLogTest() {
        super(HistoryServiceDisableTaskLogTest.class.getName());
    }

    @Override
    protected void configureConfiguration(ProcessEngineConfigurationImpl processEngineConfiguration) {
        processEngineConfiguration.setEnableHistoricTaskLogging(false);
    }

    @AfterEach
    public void deleteTasks() {
        if (task != null) {
            assertThat(historyService.createHistoricTaskLogEntryQuery().count()).isEqualTo(0l);
            taskService.deleteTask(task.getId(), true);
        }
    }

    @Test
    public void createTaskEvent() {
        task = taskService.createTaskBuilder().
            assignee("testAssignee").
            create();
    }

    @Test
    public void createTaskEventAsAuthenticatedUser() {
        String previousUserId = Authentication.getAuthenticatedUserId();
        Authentication.setAuthenticatedUserId("testUser");
        try {
            task = taskService.createTaskBuilder().
                assignee("testAssignee").
                create();
        } finally {
            Authentication.setAuthenticatedUserId(previousUserId);
        }
    }

}
