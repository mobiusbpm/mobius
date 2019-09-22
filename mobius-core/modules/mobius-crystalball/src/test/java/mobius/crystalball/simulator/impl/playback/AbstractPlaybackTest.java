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
package mobius.crystalball.simulator.impl.playback;

import junit.framework.TestCase;
import mobius.common.engine.api.delegate.event.FlowableEvent;
import mobius.common.engine.impl.runtime.Clock;
import mobius.common.engine.impl.util.DefaultClockImpl;
import mobius.crystalball.simulator.*;
import mobius.crystalball.simulator.delegate.event.Function;
import mobius.crystalball.simulator.delegate.event.impl.InMemoryRecordFlowableEventListener;
import mobius.crystalball.simulator.delegate.event.impl.ProcessInstanceCreateTransformer;
import mobius.crystalball.simulator.delegate.event.impl.UserTaskCompleteTransformer;
import mobius.crystalball.simulator.impl.EventRecorderTestUtils;
import mobius.crystalball.simulator.impl.RecordableProcessEngineFactory;
import mobius.crystalball.simulator.impl.SimulationProcessEngineFactory;
import mobius.crystalball.simulator.impl.StartProcessByIdEventHandler;
import mobius.crystalball.simulator.impl.clock.DefaultClockFactory;
import mobius.crystalball.simulator.impl.clock.ThreadLocalClock;
import mobius.engine.*;
import mobius.engine.impl.ProcessEngineImpl;
import mobius.engine.impl.cfg.ProcessEngineConfigurationImpl;
import mobius.engine.impl.test.TestHelper;
import mobius.variable.service.impl.el.NoExecutionVariableScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is supper class for all Playback tests
 *
 * @author martin.grofcik
 */
public abstract class AbstractPlaybackTest extends TestCase {
    // Process instance start event
    private static final String PROCESS_INSTANCE_START_EVENT_TYPE = "PROCESS_INSTANCE_START";
    private static final String PROCESS_DEFINITION_ID_KEY = "processDefinitionId";
    private static final String VARIABLES_KEY = "variables";
    // User task completed event
    private static final String USER_TASK_COMPLETED_EVENT_TYPE = "USER_TASK_COMPLETED";

    private static final String BUSINESS_KEY = "testBusinessKey";

    private static final String EMPTY_LINE = "\n";

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractPlaybackTest.class);

    protected InMemoryRecordFlowableEventListener listener = new InMemoryRecordFlowableEventListener(getTransformers());
    protected String deploymentIdFromDeploymentAnnotation;
    protected Throwable exception;

    protected ProcessEngine processEngine;
    protected ProcessEngineConfiguration processEngineConfiguration;
    protected RuntimeService runtimeService;
    protected TaskService taskService;
    protected HistoryService historyService;

    protected void initializeProcessEngine() {
        Clock clock = new DefaultClockImpl();

        ProcessEngineConfiguration processEngineConfiguration = ProcessEngineConfiguration.createProcessEngineConfigurationFromResourceDefault();
        processEngineConfiguration.setClock(clock);

        this.processEngine = (new RecordableProcessEngineFactory(
                (ProcessEngineConfigurationImpl) processEngineConfiguration, listener)).getObject();
    }

    protected void initializeServices() {
        this.processEngineConfiguration = processEngine.getProcessEngineConfiguration();
        this.runtimeService = processEngine.getRuntimeService();
        this.taskService = processEngine.getTaskService();
        this.historyService = processEngine.getHistoryService();
    }

    @Override
    public void runBare() throws Throwable {

        LOGGER.info("Running test {} to record events", getName());

        recordEvents();

        LOGGER.info("Events for {} recorded successfully", getName());
        LOGGER.info("Running playback simulation for {}", getName());

        runPlayback();

        LOGGER.info("Playback simulation for {} finished successfully.", getName());

    }

    private void runPlayback() throws Throwable {
        SimulationDebugger simDebugger = null;
        try {
            // init simulation run

            Clock clock = new ThreadLocalClock(new DefaultClockFactory());
            FactoryBean<ProcessEngineImpl> simulationProcessEngineFactory = new SimulationProcessEngineFactory(
                    ProcessEngineConfiguration.createProcessEngineConfigurationFromResourceDefault());

            final SimpleSimulationRun.Builder builder = new SimpleSimulationRun.Builder();
            builder.processEngine(simulationProcessEngineFactory.getObject())
                    .eventCalendar((new SimpleEventCalendarFactory(clock, new SimulationEventComparator(), listener.getSimulationEvents())).getObject())
                    .eventHandlers(getHandlers());
            simDebugger = builder.build();

            simDebugger.init(new NoExecutionVariableScope());
            this.processEngine = SimulationRunContext.getProcessEngine();
            initializeServices();
            deploymentIdFromDeploymentAnnotation = TestHelper.annotationDeploymentSetUp(processEngine, getClass(), getName());

            simDebugger.runContinue();

            _checkStatus();
        } catch (AssertionError e) {
            LOGGER.warn("Playback simulation {} has failed", getName());
            LOGGER.error(EMPTY_LINE);
            LOGGER.error("ASSERTION FAILED: {}", e, e);
            exception = e;
            throw e;

        } catch (Throwable e) {
            LOGGER.warn("Playback simulation {} has failed", getName());
            LOGGER.error(EMPTY_LINE);
            LOGGER.error("EXCEPTION: {}", e, e);
            exception = e;
            throw e;

        } finally {
            if (simDebugger != null) {
                TestHelper.annotationDeploymentTearDown(processEngine, deploymentIdFromDeploymentAnnotation, getClass(), getName());
                simDebugger.close();
            }
            this.processEngineConfiguration.getClock().reset();

            // Can't do this in the teardown, as the teardown will be called as part of the super.runBare
            closeDownProcessEngine();
        }
    }

    private void _checkStatus() throws InvocationTargetException, IllegalAccessException {
        Method method;
        try {
            method = getClass().getMethod(getName(), (Class<?>[]) null);
        } catch (Exception e) {
            LOGGER.warn("Could not get method by reflection. This could happen if you are using @Parameters in combination with annotations.", e);
            return;
        }
        CheckStatus checkStatusAnnotation = method.getAnnotation(CheckStatus.class);
        if (checkStatusAnnotation != null) {
            LOGGER.debug("annotation @CheckStatus checks status for {}.{}", getClass().getSimpleName(), getName());
            String checkStatusMethodName = checkStatusAnnotation.methodName();
            if (checkStatusMethodName.isEmpty()) {
                String name = method.getName();
                checkStatusMethodName = name + "CheckStatus";
            }

            try {
                method = getClass().getMethod(checkStatusMethodName);
            } catch (Exception e) {
                LOGGER.error("Could not get CheckStatus method: {} by reflection. This could happen if you are using @Parameters in combination with annotations.", checkStatusMethodName, e);
                throw new RuntimeException("Could not get CheckStatus method by reflection");
            }
            method.invoke(this);
        } else {
            LOGGER.warn("Check status annotation is not present - nothing is checked");
        }
    }

    private void recordEvents() throws Throwable {
        initializeProcessEngine();
        if (runtimeService == null) {
            initializeServices();
        }

        try {

            deploymentIdFromDeploymentAnnotation = TestHelper.annotationDeploymentSetUp(processEngine, getClass(), getName());

            // super.runBare();
            Throwable exception = null;
            setUp();
            try {
                runTest();
            } catch (Throwable running) {
                exception = running;
            } finally {
                try {
                    tearDown();
                } catch (Throwable tearingDown) {
                    if (exception == null)
                        exception = tearingDown;
                }
            }
            if (exception != null)
                throw exception;

            _checkStatus();
        } catch (AssertionError e) {
            LOGGER.error(EMPTY_LINE);
            LOGGER.error("ASSERTION FAILED: {}", e, e);
            exception = e;
            throw e;

        } catch (Throwable e) {
            LOGGER.warn("Record events {} has failed", getName());
            LOGGER.error(EMPTY_LINE);
            LOGGER.error("EXCEPTION: {}", e, e);
            exception = e;
            throw e;

        } finally {
            TestHelper.annotationDeploymentTearDown(processEngine, deploymentIdFromDeploymentAnnotation, getClass(), getName());
            LOGGER.info("dropping and recreating db");

            this.processEngineConfiguration.getClock().reset();

            // Can't do this in the teardown, as the teardown will be called as part of the super.runBare
            EventRecorderTestUtils.closeProcessEngine(processEngine, listener);
            closeDownProcessEngine();
        }
    }

    protected void closeDownProcessEngine() {
        ProcessEngines.destroy();
    }

    protected List<Function<FlowableEvent, SimulationEvent>> getTransformers() {
        List<Function<FlowableEvent, SimulationEvent>> transformers = new ArrayList<>();
        transformers.add(new ProcessInstanceCreateTransformer(PROCESS_INSTANCE_START_EVENT_TYPE, PROCESS_DEFINITION_ID_KEY, BUSINESS_KEY, VARIABLES_KEY));
        transformers.add(new UserTaskCompleteTransformer(USER_TASK_COMPLETED_EVENT_TYPE));
        return transformers;
    }

    protected Map<String, SimulationEventHandler> getHandlers() {
        Map<String, SimulationEventHandler> handlers = new HashMap<>();
        handlers.put(PROCESS_INSTANCE_START_EVENT_TYPE, new StartProcessByIdEventHandler(PROCESS_DEFINITION_ID_KEY, BUSINESS_KEY, VARIABLES_KEY));
        handlers.put(USER_TASK_COMPLETED_EVENT_TYPE, new PlaybackUserTaskCompleteEventHandler());
        return handlers;
    }

}
