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
package mobius.engine.impl.event.logger;

import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.engine.impl.event.logger.handler.EventLoggerEventHandler;
import mobius.engine.impl.persistence.entity.EventLogEntryEntityManager;
import mobius.engine.impl.util.CommandContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class DatabaseEventFlusher extends AbstractEventFlusher {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseEventFlusher.class);

    @Override
    public void closing(CommandContext commandContext) {

        if (commandContext.getException() != null) {
            return; // Not interested in events about exceptions
        }

        EventLogEntryEntityManager eventLogEntryEntityManager = CommandContextUtil.getEventLogEntryEntityManager(commandContext);
        for (EventLoggerEventHandler eventHandler : eventHandlers) {
            try {
                eventLogEntryEntityManager.insert(eventHandler.generateEventLogEntry(commandContext), false);
            } catch (Exception e) {
                LOGGER.warn("Could not create event log", e);
            }
        }
    }

    @Override
    public void afterSessionsFlush(CommandContext commandContext) {

    }

    @Override
    public void closeFailure(CommandContext commandContext) {

    }

}
