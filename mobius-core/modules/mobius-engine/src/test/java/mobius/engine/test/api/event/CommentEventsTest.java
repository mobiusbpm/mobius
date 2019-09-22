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

import mobius.common.engine.api.delegate.event.FlowableEngineEntityEvent;
import mobius.common.engine.api.delegate.event.FlowableEngineEventType;
import mobius.common.engine.api.delegate.event.FlowableEvent;
import mobius.common.engine.impl.history.HistoryLevel;
import mobius.engine.impl.test.HistoryTestHelper;
import mobius.engine.impl.test.PluggableFlowableTestCase;
import mobius.engine.runtime.ProcessInstance;
import mobius.engine.task.Comment;
import mobius.engine.test.Deployment;
import mobius.task.api.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test case for all {@link FlowableEvent}s related to comments.
 * 
 * @author Frederik Heremans
 */
public class CommentEventsTest extends PluggableFlowableTestCase {

    private TestFlowableEntityEventListener listener;

    /**
     * Test create, update and delete events of comments on a task/process.
     */
    @Test
    @Deployment(resources = { "mobius/engine/test/api/runtime/oneTaskProcess.bpmn20.xml" })
    public void testCommentEntityEvents() throws Exception {
        if (HistoryTestHelper.isHistoryLevelAtLeast(HistoryLevel.AUDIT, processEngineConfiguration)) {
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("oneTaskProcess");

            Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
            assertNotNull(task);

            // Create link-comment
            Comment comment = taskService.addComment(task.getId(), task.getProcessInstanceId(), "comment");
            assertEquals(2, listener.getEventsReceived().size());
            FlowableEngineEntityEvent event = (FlowableEngineEntityEvent) listener.getEventsReceived().get(0);
            assertEquals(FlowableEngineEventType.ENTITY_CREATED, event.getType());
            assertEquals(processInstance.getId(), event.getProcessInstanceId());
            assertEquals(processInstance.getId(), event.getExecutionId());
            assertEquals(processInstance.getProcessDefinitionId(), event.getProcessDefinitionId());
            Comment commentFromEvent = (Comment) event.getEntity();
            assertEquals(comment.getId(), commentFromEvent.getId());

            event = (FlowableEngineEntityEvent) listener.getEventsReceived().get(1);
            assertEquals(FlowableEngineEventType.ENTITY_INITIALIZED, event.getType());
            listener.clearEventsReceived();

            // Finally, delete comment
            taskService.deleteComment(comment.getId());
            assertEquals(1, listener.getEventsReceived().size());
            event = (FlowableEngineEntityEvent) listener.getEventsReceived().get(0);
            assertEquals(FlowableEngineEventType.ENTITY_DELETED, event.getType());
            assertEquals(processInstance.getId(), event.getProcessInstanceId());
            assertEquals(processInstance.getId(), event.getExecutionId());
            assertEquals(processInstance.getProcessDefinitionId(), event.getProcessDefinitionId());
            commentFromEvent = (Comment) event.getEntity();
            assertEquals(comment.getId(), commentFromEvent.getId());
        }
    }

    @Test
    public void testCommentEntityEventsStandaloneTask() throws Exception {
        if (HistoryTestHelper.isHistoryLevelAtLeast(HistoryLevel.AUDIT, processEngineConfiguration)) {
            Task task = null;
            try {
                task = taskService.newTask();
                taskService.saveTask(task);
                assertNotNull(task);

                // Create link-comment
                Comment comment = taskService.addComment(task.getId(), null, "comment");
                assertEquals(2, listener.getEventsReceived().size());
                FlowableEngineEntityEvent event = (FlowableEngineEntityEvent) listener.getEventsReceived().get(0);
                assertEquals(FlowableEngineEventType.ENTITY_CREATED, event.getType());
                assertNull(event.getProcessInstanceId());
                assertNull(event.getExecutionId());
                assertNull(event.getProcessDefinitionId());
                Comment commentFromEvent = (Comment) event.getEntity();
                assertEquals(comment.getId(), commentFromEvent.getId());

                event = (FlowableEngineEntityEvent) listener.getEventsReceived().get(1);
                assertEquals(FlowableEngineEventType.ENTITY_INITIALIZED, event.getType());
                listener.clearEventsReceived();

                // Finally, delete comment
                taskService.deleteComment(comment.getId());
                assertEquals(1, listener.getEventsReceived().size());
                event = (FlowableEngineEntityEvent) listener.getEventsReceived().get(0);
                assertEquals(FlowableEngineEventType.ENTITY_DELETED, event.getType());
                assertNull(event.getProcessInstanceId());
                assertNull(event.getExecutionId());
                assertNull(event.getProcessDefinitionId());
                commentFromEvent = (Comment) event.getEntity();
                assertEquals(comment.getId(), commentFromEvent.getId());

            } finally {
                if (task != null && task.getId() != null) {
                    taskService.deleteTask(task.getId(), true);
                }
            }
        }
    }

    @BeforeEach
    protected void setUp() throws Exception {
        listener = new TestFlowableEntityEventListener(Comment.class);
        processEngineConfiguration.getEventDispatcher().addEventListener(listener);
    }

    @AfterEach
    protected void tearDown() throws Exception {

        if (listener != null) {
            processEngineConfiguration.getEventDispatcher().removeEventListener(listener);
        }
    }
}
