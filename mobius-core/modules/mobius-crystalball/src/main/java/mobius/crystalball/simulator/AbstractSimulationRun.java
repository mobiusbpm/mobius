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
package mobius.crystalball.simulator;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import mobius.engine.ProcessEngine;
import mobius.variable.api.delegate.VariableScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements all methods for Simulation run
 *
 * @author martin.grofcik
 */
public abstract class AbstractSimulationRun implements SimulationRun, SimulationDebugger {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSimulationRun.class);

    protected String id;
    /**
     * Map for eventType -> event handlers to execute events on simulation engine
     */
    protected Map<String, SimulationEventHandler> eventHandlerMap = new HashMap<>();
    protected ProcessEngine processEngine;

    public AbstractSimulationRun(Map<String, SimulationEventHandler> eventHandlers) {
        if (eventHandlers != null && !eventHandlers.isEmpty()) {
            this.eventHandlerMap.putAll(eventHandlers);
        }
    }

    @Override
    public void execute(VariableScope execution) {
        init(execution);

        runContinue();

        close();
    }

    protected SimulationEvent removeSimulationEvent() {
        SimulationEvent event = SimulationRunContext.getEventCalendar().removeFirstEvent();
        if (event != null && event.hasSimulationTime())
            this.processEngine.getProcessEngineConfiguration().getClock().setCurrentTime(new Date(event.getSimulationTime()));
        return event;
    }

    @Override
    public void init(VariableScope execution) {
        initSimulationRunContext(execution);
        initHandlers();
    }

    @Override
    public void step() {
        SimulationEvent event = removeSimulationEvent();
        if (!simulationEnd(event)) {
            LOGGER.debug("executing simulation event {}", event);
            executeEvent(event);
            LOGGER.debug("simulation event {} execution done", event);
        } else {
            LOGGER.info("Simulation run has ended.");
        }
    }

    @Override
    public void runContinue() {
        SimulationEvent event = removeSimulationEvent();

        while (!simulationEnd(event)) {
            executeEvent(event);
            event = removeSimulationEvent();
        }
    }

    @Override
    public void runTo(long simulationTime) {
        SimulationEvent breakEvent = new SimulationEvent.Builder(SimulationConstants.TYPE_BREAK_SIMULATION).simulationTime(simulationTime).priority(SimulationConstants.PRIORITY_SYSTEM).build();
        EventCalendar calendar = SimulationRunContext.getEventCalendar();
        calendar.addEvent(breakEvent);
        runContinue();
    }

    @Override
    public void runTo(String simulationEventType) {
        EventCalendar eventCalendar = SimulationRunContext.getEventCalendar();
        SimulationEvent event = eventCalendar.peekFirstEvent();

        while (!simulationEventType.equals(event.getType()) && !simulationEnd(event)) {
            step();
            event = eventCalendar.peekFirstEvent();
        }
    }

    /**
     * close simulation run
     */
    @Override
    public abstract void close();

    protected abstract void initSimulationRunContext(VariableScope execution);

    protected void initHandlers() {
        for (SimulationEventHandler handler : eventHandlerMap.values()) {
            handler.init();
        }
    }

    protected abstract boolean simulationEnd(SimulationEvent event);

    protected void executeEvent(SimulationEvent event) {
        // set simulation time to the next event for process engine too
        LOGGER.debug("Simulation time: {}", this.processEngine.getProcessEngineConfiguration().getClock().getCurrentTime());

        SimulationEventHandler handler = eventHandlerMap.get(event.getType());
        if (handler != null) {
            LOGGER.debug("Handling event of type[{}]", event.getType());
            handler.handle(event);
        } else {
            LOGGER.warn("Event type[{}] does not have any handler assigned.", event.getType());
        }
    }

}
