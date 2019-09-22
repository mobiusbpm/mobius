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
package mobius.engine.test.cfg.taskcount;

import mobius.common.engine.impl.interceptor.Command;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.engine.ProcessEngineConfiguration;
import mobius.engine.impl.cfg.ProcessEngineConfigurationImpl;
import mobius.engine.impl.cmd.ValidateTaskRelatedEntityCountCfgCmd;
import mobius.engine.impl.persistence.entity.PropertyEntity;
import mobius.engine.impl.test.ResourceFlowableTestCase;
import mobius.engine.impl.util.CommandContextUtil;
import mobius.engine.runtime.ProcessInstance;
import mobius.engine.test.Deployment;
import mobius.task.api.Task;
import mobius.task.service.impl.persistence.CountingTaskEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@DisabledIfSystemProperty(named = "database", matches = "cockroachdb") // Disabled due to having a retry interceptor for CRDB and barriers in this test
public class ChangeTaskCountConfigAndRebootEngineTest extends ResourceFlowableTestCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChangeTaskCountConfigAndRebootEngineTest.class);

    protected boolean newTaskRelationshipCountValue;

    public ChangeTaskCountConfigAndRebootEngineTest() {

        // Simply boot up the same engine with the usual config file
        // This way, database tests work. the only thing we have to make
        // sure is to give the process engine a name so it is
        // registered and unregistered separately.
        super("flowable.cfg.xml", ChangeTaskCountConfigAndRebootEngineTest.class.getName());
    }

    @Override
    protected void additionalConfiguration(ProcessEngineConfiguration processEngineConfiguration) {
        LOGGER.info("Applying additional config: setting schema update to true and enabling task relationship count");
        processEngineConfiguration.setDatabaseSchemaUpdate("true");
        ((ProcessEngineConfigurationImpl) processEngineConfiguration).setEnableTaskRelationshipCounts(newTaskRelationshipCountValue);
    }

    protected void rebootEngine(boolean newTaskRelationshipCountValue) {
        LOGGER.info("Rebooting engine");
        this.newTaskRelationshipCountValue = newTaskRelationshipCountValue;
        rebootEngine();
    }

    @Test
    @Deployment
    public void testChangeTaskCountSettingAndRebootengine() {

        rebootFlagNotChanged(true);

        rebootFlagNotChanged(false);

        checkEnableFlagBetweenTasks();

        checkDisableFlagBetweenTasks();

    }

    private void checkEnableFlagBetweenTasks() {
        rebootEngine(false);
        assertConfigProperty(false);

        // Start a new process
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("twoTasksProcess");
        Task firstTask = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        assertTaskCountFlag(firstTask, false);

        // Reboot, enabling the config property. however, the task won't get the flag now
        rebootEngine(true);
        assertConfigProperty(true);

        // re-fetch the task
        firstTask = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        assertTaskCountFlag(firstTask, false);

        // complete the first task, move to the next one
        taskService.complete(firstTask.getId());

        // second task created with the new flag (true)
        Task secondTask = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        assertTaskCountFlag(secondTask, true);

        finishProcessInstance(processInstance);
    }

    private void checkDisableFlagBetweenTasks() {
        rebootEngine(true);
        assertConfigProperty(true);

        // Start a new process
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("twoTasksProcess");
        Task firstTask = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        assertTaskCountFlag(firstTask, true);

        // Reboot, disabling the config property. The existing task will have the flag updated.
        rebootEngine(false);
        assertConfigProperty(false);

        firstTask = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        assertTaskCountFlag(firstTask, false);

        // complete the first task, move to the next one
        taskService.complete(firstTask.getId());

        // second task created with flag false
        Task secondTask = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        assertTaskCountFlag(secondTask, false);

        finishProcessInstance(processInstance);
    }

    private void rebootFlagNotChanged(boolean enableTaskCountFlag) {
        rebootEngine(enableTaskCountFlag);
        assertConfigProperty(enableTaskCountFlag);

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("twoTasksProcess");
        Task firstTask = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();

        assertTaskCountFlag(firstTask, enableTaskCountFlag);

        // Reboot with same settings. Nothing should have changed
        rebootEngine(enableTaskCountFlag);
        assertConfigProperty(enableTaskCountFlag);
        assertTaskCountFlag(firstTask, enableTaskCountFlag);

        // The second task should have the same count flag
        taskService.complete(firstTask.getId());
        Task secondTask = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        assertTaskCountFlag(secondTask, enableTaskCountFlag);

        // See if we can finish the process
        finishProcessInstance(processInstance);
    }

    /**
     * Check the DB property against Process Engine flag.
     */
    protected void assertConfigProperty(boolean expectedValue) {
        PropertyEntity propertyEntity = managementService.executeCommand(new Command<PropertyEntity>() {
            @Override
            public PropertyEntity execute(CommandContext commandContext) {
                return CommandContextUtil.getPropertyEntityManager(commandContext).findById(
                        ValidateTaskRelatedEntityCountCfgCmd.PROPERTY_TASK_RELATED_ENTITY_COUNT);
            }
        });
        assertEquals(expectedValue, Boolean.parseBoolean(propertyEntity.getValue()));
    }

    protected void assertTaskCountFlag(Task task, boolean enableTaskCountFlag) {
        assertEquals(((CountingTaskEntity) task).isCountEnabled(), enableTaskCountFlag);
    }

    protected void finishProcessInstance(ProcessInstance processInstance) {
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        taskService.complete(task.getId());
        assertProcessEnded(processInstance.getId());
    }
}