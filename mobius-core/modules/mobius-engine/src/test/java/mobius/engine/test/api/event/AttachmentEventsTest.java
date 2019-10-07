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

import java.io.ByteArrayInputStream;

import mobius.common.engine.api.delegate.event.FlowableEngineEntityEvent;
import mobius.common.engine.api.delegate.event.FlowableEngineEventType;
import mobius.common.engine.api.delegate.event.FlowableEvent;
import mobius.common.engine.impl.history.HistoryLevel;
import mobius.common.engine.impl.identity.Authentication;
import mobius.engine.impl.test.HistoryTestHelper;
import mobius.engine.impl.test.PluggableFlowableTestCase;
import mobius.engine.runtime.ProcessInstance;
import mobius.engine.task.Attachment;
import mobius.engine.test.Deployment;
import mobius.task.api.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test case for all {@link FlowableEvent}s related to attachments.
 * 
 *
 */
public class AttachmentEventsTest extends PluggableFlowableTestCase {

    private TestFlowableEntityEventListener listener;

    /**
     * Test create, update and delete events of attachments on a task/process.
     */
    @Test
    @Deployment(resources = { "mobius/engine/test/api/runtime/oneTaskProcess.bpmn20.xml" })
    public void testAttachmentEntityEvents() throws Exception {

        if (HistoryTestHelper.isHistoryLevelAtLeast(HistoryLevel.ACTIVITY, processEngineConfiguration)) {
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("oneTaskProcess");

            Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
            assertNotNull(task);

            // Create link-attachment
            Attachment attachment = taskService.createAttachment("test", task.getId(), processInstance.getId(), "attachment name", "description", "http://flowable.org");
            assertNull(attachment.getUserId());
            assertEquals(2, listener.getEventsReceived().size());
            FlowableEngineEntityEvent event = (FlowableEngineEntityEvent) listener.getEventsReceived().get(0);
            assertEquals(FlowableEngineEventType.ENTITY_CREATED, event.getType());
            assertEquals(processInstance.getId(), event.getProcessInstanceId());
            assertEquals(processInstance.getId(), event.getExecutionId());
            assertEquals(processInstance.getProcessDefinitionId(), event.getProcessDefinitionId());
            Attachment attachmentFromEvent = (Attachment) event.getEntity();
            assertEquals(attachment.getId(), attachmentFromEvent.getId());
            event = (FlowableEngineEntityEvent) listener.getEventsReceived().get(1);
            assertEquals(FlowableEngineEventType.ENTITY_INITIALIZED, event.getType());
            assertEquals(processInstance.getId(), event.getProcessInstanceId());
            assertEquals(processInstance.getId(), event.getExecutionId());
            assertEquals(processInstance.getProcessDefinitionId(), event.getProcessDefinitionId());
            attachmentFromEvent = (Attachment) event.getEntity();
            assertEquals(attachment.getId(), attachmentFromEvent.getId());
            listener.clearEventsReceived();

            // Create binary attachment
            Authentication.setAuthenticatedUserId("testuser");
            attachment = taskService.createAttachment("test", task.getId(), processInstance.getId(), "attachment name", "description", new ByteArrayInputStream("test".getBytes()));
            assertNotNull(attachment.getUserId());
            assertEquals("testuser", attachment.getUserId());
            assertEquals(2, listener.getEventsReceived().size());
            event = (FlowableEngineEntityEvent) listener.getEventsReceived().get(0);
            assertEquals(FlowableEngineEventType.ENTITY_CREATED, event.getType());
            assertEquals(processInstance.getId(), event.getProcessInstanceId());
            assertEquals(processInstance.getId(), event.getExecutionId());
            assertEquals(processInstance.getProcessDefinitionId(), event.getProcessDefinitionId());
            attachmentFromEvent = (Attachment) event.getEntity();
            assertEquals(attachment.getId(), attachmentFromEvent.getId());

            event = (FlowableEngineEntityEvent) listener.getEventsReceived().get(1);
            assertEquals(FlowableEngineEventType.ENTITY_INITIALIZED, event.getType());
            listener.clearEventsReceived();

            // Update attachment
            attachment = taskService.getAttachment(attachment.getId());
            attachment.setDescription("Description");
            taskService.saveAttachment(attachment);

            assertEquals(1, listener.getEventsReceived().size());
            event = (FlowableEngineEntityEvent) listener.getEventsReceived().get(0);
            assertEquals(FlowableEngineEventType.ENTITY_UPDATED, event.getType());
            assertEquals(processInstance.getId(), event.getProcessInstanceId());
            assertEquals(processInstance.getId(), event.getExecutionId());
            assertEquals(processInstance.getProcessDefinitionId(), event.getProcessDefinitionId());
            attachmentFromEvent = (Attachment) event.getEntity();
            assertEquals(attachment.getId(), attachmentFromEvent.getId());
            assertEquals("Description", attachmentFromEvent.getDescription());
            listener.clearEventsReceived();

            // Finally, delete attachment
            taskService.deleteAttachment(attachment.getId());
            assertEquals(1, listener.getEventsReceived().size());
            event = (FlowableEngineEntityEvent) listener.getEventsReceived().get(0);
            assertEquals(FlowableEngineEventType.ENTITY_DELETED, event.getType());
            assertEquals(processInstance.getId(), event.getProcessInstanceId());
            assertEquals(processInstance.getId(), event.getExecutionId());
            assertEquals(processInstance.getProcessDefinitionId(), event.getProcessDefinitionId());
            attachmentFromEvent = (Attachment) event.getEntity();
            assertEquals(attachment.getId(), attachmentFromEvent.getId());
        }
    }

    /**
     * Test create, update and delete events of users.
     */
    @Test
    public void testAttachmentEntityEventsStandaloneTask() throws Exception {
        if (HistoryTestHelper.isHistoryLevelAtLeast(HistoryLevel.ACTIVITY, processEngineConfiguration)) {
            Task task = null;
            try {
                task = taskService.newTask();
                taskService.saveTask(task);
                assertNotNull(task);

                // Create link-attachment
                Attachment attachment = taskService.createAttachment("test", task.getId(), null, "attachment name", "description", "http://flowable.org");
                assertEquals(2, listener.getEventsReceived().size());
                FlowableEngineEntityEvent event = (FlowableEngineEntityEvent) listener.getEventsReceived().get(0);
                assertEquals(FlowableEngineEventType.ENTITY_CREATED, event.getType());
                assertNull(event.getProcessInstanceId());
                assertNull(event.getExecutionId());
                assertNull(event.getProcessDefinitionId());
                Attachment attachmentFromEvent = (Attachment) event.getEntity();
                assertEquals(attachment.getId(), attachmentFromEvent.getId());
                event = (FlowableEngineEntityEvent) listener.getEventsReceived().get(1);
                assertEquals(FlowableEngineEventType.ENTITY_INITIALIZED, event.getType());
                listener.clearEventsReceived();

                // Create binary attachment
                attachment = taskService.createAttachment("test", task.getId(), null, "attachment name", "description", new ByteArrayInputStream("test".getBytes()));
                assertEquals(2, listener.getEventsReceived().size());
                event = (FlowableEngineEntityEvent) listener.getEventsReceived().get(0);
                assertEquals(FlowableEngineEventType.ENTITY_CREATED, event.getType());
                assertNull(event.getProcessInstanceId());
                assertNull(event.getExecutionId());
                assertNull(event.getProcessDefinitionId());
                attachmentFromEvent = (Attachment) event.getEntity();
                assertEquals(attachment.getId(), attachmentFromEvent.getId());

                event = (FlowableEngineEntityEvent) listener.getEventsReceived().get(1);
                assertEquals(FlowableEngineEventType.ENTITY_INITIALIZED, event.getType());
                listener.clearEventsReceived();

                // Update attachment
                attachment = taskService.getAttachment(attachment.getId());
                attachment.setDescription("Description");
                taskService.saveAttachment(attachment);

                assertEquals(1, listener.getEventsReceived().size());
                event = (FlowableEngineEntityEvent) listener.getEventsReceived().get(0);
                assertEquals(FlowableEngineEventType.ENTITY_UPDATED, event.getType());
                assertNull(event.getProcessInstanceId());
                assertNull(event.getExecutionId());
                assertNull(event.getProcessDefinitionId());
                attachmentFromEvent = (Attachment) event.getEntity();
                assertEquals(attachment.getId(), attachmentFromEvent.getId());
                assertEquals("Description", attachmentFromEvent.getDescription());
                listener.clearEventsReceived();

                // Finally, delete attachment
                taskService.deleteAttachment(attachment.getId());
                assertEquals(1, listener.getEventsReceived().size());
                event = (FlowableEngineEntityEvent) listener.getEventsReceived().get(0);
                assertEquals(FlowableEngineEventType.ENTITY_DELETED, event.getType());
                assertNull(event.getProcessInstanceId());
                assertNull(event.getExecutionId());
                assertNull(event.getProcessDefinitionId());
                attachmentFromEvent = (Attachment) event.getEntity();
                assertEquals(attachment.getId(), attachmentFromEvent.getId());

            } finally {
                if (task != null && task.getId() != null) {
                    taskService.deleteTask(task.getId(), true);
                }
            }
        }
    }

    @Test
    public void testAttachmentEntityEventsOnHistoricTaskDelete() throws Exception {
        if (HistoryTestHelper.isHistoryLevelAtLeast(HistoryLevel.ACTIVITY, processEngineConfiguration)) {
            Task task = null;
            try {
                task = taskService.newTask();
                taskService.saveTask(task);
                assertNotNull(task);

                // Create link-attachment
                Attachment attachment = taskService.createAttachment("test", task.getId(), null, "attachment name", "description", "http://flowable.org");
                listener.clearEventsReceived();

                // Delete task and historic task
                taskService.deleteTask(task.getId());
                historyService.deleteHistoricTaskInstance(task.getId());

                assertEquals(1, listener.getEventsReceived().size());
                FlowableEngineEntityEvent event = (FlowableEngineEntityEvent) listener.getEventsReceived().get(0);
                assertEquals(FlowableEngineEventType.ENTITY_DELETED, event.getType());
                assertNull(event.getProcessInstanceId());
                assertNull(event.getExecutionId());
                assertNull(event.getProcessDefinitionId());
                Attachment attachmentFromEvent = (Attachment) event.getEntity();
                assertEquals(attachment.getId(), attachmentFromEvent.getId());

            } finally {
                if (task != null && task.getId() != null) {
                    taskService.deleteTask(task.getId(), true);
                }
            }
        }
    }

    @BeforeEach
    protected void setUp() throws Exception {
        listener = new TestFlowableEntityEventListener(Attachment.class);
        processEngineConfiguration.getEventDispatcher().addEventListener(listener);
    }

    @AfterEach
    protected void tearDown() throws Exception {

        if (listener != null) {
            processEngineConfiguration.getEventDispatcher().removeEventListener(listener);
        }
    }
}
