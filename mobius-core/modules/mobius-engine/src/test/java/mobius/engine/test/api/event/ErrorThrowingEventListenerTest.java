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
package mobius.engine.test.api.event;

import mobius.common.engine.api.delegate.event.FlowableEngineEventType;
import mobius.common.engine.api.delegate.event.FlowableEvent;
import mobius.common.engine.api.delegate.event.FlowableEventListener;
import mobius.engine.impl.bpmn.helper.ErrorThrowingEventListener;
import mobius.engine.impl.test.PluggableFlowableTestCase;
import mobius.engine.runtime.ProcessInstance;
import mobius.engine.test.Deployment;
import mobius.task.api.Task;
import org.junit.jupiter.api.Test;

/**
 * Test case for all {@link FlowableEventListener}s that throws an error BPMN event when an {@link FlowableEvent} has been dispatched.
 * 
 * @author Frederik Heremans
 */
public class ErrorThrowingEventListenerTest extends PluggableFlowableTestCase {

    @Test
    @Deployment
    public void testThrowError() throws Exception {
        ErrorThrowingEventListener listener = null;
        try {
            listener = new ErrorThrowingEventListener();

            processEngineConfiguration.getEventDispatcher().addEventListener(listener, FlowableEngineEventType.TASK_ASSIGNED);

            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("testError");
            assertNotNull(processInstance);

            // Fetch the task and assign it. Should cause error-event to be dispatched
            Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).taskDefinitionKey("userTask").singleResult();
            assertNotNull(task);
            taskService.setAssignee(task.getId(), "kermit");

            // Error-handling should have been called, and "escalate" task
            // should be available instead of original one
            task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).taskDefinitionKey("escalatedTask").singleResult();
            assertNotNull(task);
            
            waitForHistoryJobExecutorToProcessAllJobs(7000, 100);

        } finally {
            processEngineConfiguration.getEventDispatcher().removeEventListener(listener);
        }
    }

    @Test
    @Deployment
    public void testThrowErrorDefinedInProcessDefinition() throws Exception {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("testError");
        assertNotNull(processInstance);

        // Fetch the task and assign it. Should cause error-event to be
        // dispatched
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).taskDefinitionKey("userTask").singleResult();
        assertNotNull(task);
        taskService.setAssignee(task.getId(), "kermit");

        // Error-handling should have been called, and "escalate" task should be
        // available instead of original one
        task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).taskDefinitionKey("escalatedTask").singleResult();
        assertNotNull(task);
        
        waitForHistoryJobExecutorToProcessAllJobs(7000, 100);
    }

    @Test
    @Deployment
    public void testThrowErrorWithErrorcode() throws Exception {
        ErrorThrowingEventListener listener = null;
        try {
            listener = new ErrorThrowingEventListener();
            listener.setErrorCode("123");

            processEngineConfiguration.getEventDispatcher().addEventListener(listener, FlowableEngineEventType.TASK_ASSIGNED);

            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("testError");
            assertNotNull(processInstance);

            // Fetch the task and assign it. Should cause error-event to be
            // dispatched
            Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).taskDefinitionKey("userTask").singleResult();
            assertNotNull(task);
            taskService.setAssignee(task.getId(), "kermit");

            // Error-handling should have been called, and "escalate" task
            // should be available instead of original one
            task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).taskDefinitionKey("escalatedTask").singleResult();
            assertNotNull(task);

            // Try with a different error-code, resulting in a different task being created
            listener.setErrorCode("456");

            processInstance = runtimeService.startProcessInstanceByKey("testError");
            assertNotNull(processInstance);

            // Fetch the task and assign it. Should cause error-event to be dispatched
            task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).taskDefinitionKey("userTask").singleResult();
            assertNotNull(task);
            taskService.setAssignee(task.getId(), "kermit");

            task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).taskDefinitionKey("escalatedTask2").singleResult();
            assertNotNull(task);
            
            waitForHistoryJobExecutorToProcessAllJobs(7000, 100);
            
        } finally {
            processEngineConfiguration.getEventDispatcher().removeEventListener(listener);
        }
    }

    @Test
    @Deployment
    public void testThrowErrorWithErrorcodeDefinedInProcessDefinition() throws Exception {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("testError");
        assertNotNull(processInstance);

        // Fetch the task and assign it. Should cause error-event to be dispatched
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).taskDefinitionKey("userTask").singleResult();
        assertNotNull(task);
        taskService.setAssignee(task.getId(), "kermit");

        // Error-handling should have been called, and "escalate" task should be
        // available instead of original one
        task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).taskDefinitionKey("escalatedTask").singleResult();
        assertNotNull(task);
        
        waitForHistoryJobExecutorToProcessAllJobs(7000, 100);
    }
}
