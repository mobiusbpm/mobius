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
package mobius.cmmn.test.listener;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mobius.cmmn.api.runtime.CaseInstance;
import mobius.cmmn.engine.CmmnEngineConfiguration;
import mobius.cmmn.engine.test.CmmnDeployment;
import mobius.cmmn.test.impl.CustomCmmnConfigurationFlowableTestCase;
import mobius.task.api.Task;
import mobius.task.api.TaskInfo;
import mobius.task.service.delegate.DelegateTask;
import mobius.task.service.delegate.TaskListener;
import org.junit.Test;

/**
 *
 */
public class TaskListenerTest extends CustomCmmnConfigurationFlowableTestCase {

    @Override
    protected String getEngineName() {
        return this.getClass().getName();
    }

    @Override
    protected void configureConfiguration(CmmnEngineConfiguration cmmnEngineConfiguration) {
        Map<Object, Object> beans = new HashMap<>();
        cmmnEngineConfiguration.setBeans(beans);

        beans.put("taskListenerCreateBean", new TestDelegateTaskListener());
        beans.put("taskListenerCompleteBean", new TestDelegateTaskListener());
        beans.put("taskListenerAssignBean", new TestDelegateTaskListener());
    }

    @Test
    @CmmnDeployment
    public void testCreateEvent() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testTaskListeners").start();
        assertVariable(caseInstance, "variableFromClassDelegate", "Hello World from class delegate");
        assertVariable(caseInstance, "variableFromDelegateExpression", "Hello World from delegate expression");
        assertVariable(caseInstance, "expressionVariable", "Hello World from expression");
    }

    @Test
    @CmmnDeployment
    public void testCompleteEvent() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testTaskListeners").start();
        List<Task> tasks = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).list();
        for (Task task : tasks) {
            if (!task.getName().equals("Keepalive")) {
                cmmnTaskService.complete(task.getId());
            }
        }
        assertVariable(caseInstance, "variableFromClassDelegate", "Hello World from class delegate");
        assertVariable(caseInstance, "variableFromDelegateExpression", "Hello World from delegate expression");
        assertVariable(caseInstance, "expressionVariable", "Hello World from expression");
    }


    @Test
    @CmmnDeployment
    public void testAssignEvent() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testTaskListeners").start();
        List<Task> tasks = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).list();
        for (Task task : tasks) {
            if (!task.getName().equals("Keepalive")) {
                cmmnTaskService.setAssignee(task.getId(), "testAssignee");
            }
        }
        assertVariable(caseInstance, "variableFromClassDelegate", "Hello World from class delegate");
        assertVariable(caseInstance, "variableFromDelegateExpression", "Hello World from delegate expression");
        assertVariable(caseInstance, "expressionVariable", "Hello World from expression");
    }

    @Test
    @CmmnDeployment
    public void testAssignEventOriginalAssignee() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testTaskListeners").start();
        Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        cmmnTaskService.setAssignee(task.getId(), "testAssignee");

        assertVariable(task, "taskId", task.getId());
        assertVariable(task, "previousAssignee", "defaultAssignee");
        assertVariable(task, "currentAssignee", "testAssignee");
    }

    @Test
    @CmmnDeployment(resources = "mobius/cmmn/test/listener/TaskListenerTest.testAssignEventOriginalAssignee.cmmn")
    public void testAssignEventOnCreateByHumanTaskActivityBehaviour() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testTaskListeners").start();
        Task task = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();

        assertVariable(task, "taskId", task.getId());
        assertVariable(task, "previousAssignee", "defaultAssignee");
        assertVariable(task, "currentAssignee", "defaultAssignee");
    }

    private void assertVariable(CaseInstance caseInstance, String varName, String value) {
        String variable = (String) cmmnRuntimeService.getVariable(caseInstance.getId(), varName);
        assertThat(variable).isEqualTo(value);
    }

    private void assertVariable(TaskInfo task, String varName, String value) {
        String variable = (String) cmmnTaskService.getVariable(task.getId(), varName);
        assertThat(variable).isEqualTo(value);
    }

    static class TestDelegateTaskListener implements TaskListener {

        @Override
        public void notify(DelegateTask delegateTask) {
            delegateTask.setVariable("variableFromDelegateExpression", "Hello World from delegate expression");
        }
    }

}
