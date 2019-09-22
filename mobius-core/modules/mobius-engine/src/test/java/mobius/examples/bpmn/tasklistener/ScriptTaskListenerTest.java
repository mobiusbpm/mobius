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
package mobius.examples.bpmn.tasklistener;

import mobius.common.engine.impl.history.HistoryLevel;
import mobius.engine.impl.test.HistoryTestHelper;
import mobius.engine.impl.test.PluggableFlowableTestCase;
import mobius.engine.runtime.ProcessInstance;
import mobius.engine.test.Deployment;
import mobius.task.api.Task;
import mobius.task.api.history.HistoricTaskInstance;
import org.junit.jupiter.api.Test;

/**
 * @author Rich Kroll, Hai
 */
public class ScriptTaskListenerTest extends PluggableFlowableTestCase {

    @Test
    @Deployment(resources = { "mobius/examples/bpmn/tasklistener/ScriptTaskListenerTest.bpmn20.xml" })
    public void testScriptTaskListener() {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("scriptTaskListenerProcess");
        Task task = taskService.createTaskQuery().singleResult();
        assertEquals("Name does not match", "All your base are belong to us", task.getName());

        taskService.complete(task.getId());

        if (HistoryTestHelper.isHistoryLevelAtLeast(HistoryLevel.AUDIT, processEngineConfiguration)) {
            HistoricTaskInstance historicTask = historyService.createHistoricTaskInstanceQuery().taskId(task.getId()).singleResult();
            assertEquals("kermit", historicTask.getOwner());

            task = taskService.createTaskQuery().singleResult();
            assertEquals("Task name not set with 'bar' variable", "BAR", task.getName());
        }

        Object bar = runtimeService.getVariable(processInstance.getId(), "bar");
        assertNull("Expected 'bar' variable to be local to script", bar);

        Object foo = runtimeService.getVariable(processInstance.getId(), "foo");
        assertEquals("Could not find the 'foo' variable in variable scope", "FOO", foo);
    }

}
