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
package mobius.standalone.event;

import mobius.common.engine.api.delegate.event.FlowableEngineEventType;
import mobius.common.engine.api.delegate.event.FlowableEvent;
import mobius.engine.delegate.event.impl.FlowableProcessEventImpl;
import mobius.engine.impl.test.ResourceFlowableTestCase;
import mobius.engine.test.api.event.TestFlowableEventListener;
import org.junit.jupiter.api.Test;

/**
 * Test to verify event-listeners, which are configured in the cfg.xml, are notified.
 * 
 *
 */
public class TypedEventListenersConfigurationTest extends ResourceFlowableTestCase {

    public TypedEventListenersConfigurationTest() {
        super("mobius/standalone/event/flowable-typed-eventlistener.cfg.xml");
    }

    @Test
    public void testEventListenerConfiguration() {
        // Fetch the listener to check received events
        TestFlowableEventListener listener = (TestFlowableEventListener) processEngineConfiguration.getBeans().get("eventListener");
        assertNotNull(listener);

        // Clear any events received (eg. engine initialisation)
        listener.clearEventsReceived();

        // Dispatch a custom event
        FlowableEvent event = new FlowableProcessEventImpl(FlowableEngineEventType.CUSTOM);
        processEngineConfiguration.getEventDispatcher().dispatchEvent(event);

        assertEquals(1, listener.getEventsReceived().size());
        assertEquals(event, listener.getEventsReceived().get(0));
        listener.clearEventsReceived();

        // Dispatch another event the listener is registered for
        event = new FlowableProcessEventImpl(FlowableEngineEventType.ENTITY_DELETED);
        processEngineConfiguration.getEventDispatcher().dispatchEvent(event);
        event = new FlowableProcessEventImpl(FlowableEngineEventType.ENTITY_UPDATED);
        processEngineConfiguration.getEventDispatcher().dispatchEvent(event);

        assertEquals(2, listener.getEventsReceived().size());
        assertEquals(FlowableEngineEventType.ENTITY_DELETED, listener.getEventsReceived().get(0).getType());
        assertEquals(FlowableEngineEventType.ENTITY_UPDATED, listener.getEventsReceived().get(1).getType());
        listener.clearEventsReceived();

        // Dispatch an event that is NOT part of the types configured
        event = new FlowableProcessEventImpl(FlowableEngineEventType.ENTITY_CREATED);
        processEngineConfiguration.getEventDispatcher().dispatchEvent(event);
        assertTrue(listener.getEventsReceived().isEmpty());
    }
}
