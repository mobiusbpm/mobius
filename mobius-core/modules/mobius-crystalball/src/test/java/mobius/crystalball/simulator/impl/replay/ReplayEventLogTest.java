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
package mobius.crystalball.simulator.impl.replay;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import mobius.crystalball.simulator.ReplaySimulationRun;
import mobius.crystalball.simulator.SimpleEventCalendar;
import mobius.crystalball.simulator.SimulationDebugger;
import mobius.crystalball.simulator.SimulationEvent;
import mobius.crystalball.simulator.SimulationEventComparator;
import mobius.crystalball.simulator.SimulationEventHandler;
import mobius.crystalball.simulator.delegate.event.Function;
import mobius.crystalball.simulator.delegate.event.impl.EventLogProcessInstanceCreateTransformer;
import mobius.crystalball.simulator.delegate.event.impl.EventLogTransformer;
import mobius.crystalball.simulator.delegate.event.impl.EventLogUserTaskCompleteTransformer;
import mobius.crystalball.simulator.impl.StartReplayLogEventHandler;
import mobius.engine.HistoryService;
import mobius.engine.ManagementService;
import mobius.engine.ProcessEngines;
import mobius.engine.RuntimeService;
import mobius.engine.TaskService;
import mobius.engine.event.EventLogEntry;
import mobius.engine.impl.ProcessEngineImpl;
import mobius.engine.impl.cfg.ProcessEngineConfigurationImpl;
import mobius.engine.runtime.ProcessInstance;
import mobius.task.api.Task;
import mobius.variable.api.history.HistoricVariableInstance;
import mobius.variable.service.impl.el.NoExecutionVariableScope;
import org.junit.Test;

/**
 * @author martin.grofcik
 */
public class ReplayEventLogTest {

    // Process instance start event
    private static final String PROCESS_INSTANCE_START_EVENT_TYPE = "PROCESS_INSTANCE_START";
    private static final String PROCESS_DEFINITION_ID_KEY = "processDefinitionId";
    private static final String VARIABLES_KEY = "variables";
    // User task completed event
    private static final String USER_TASK_COMPLETED_EVENT_TYPE = "USER_TASK_COMPLETED";

    private static final String USERTASK_PROCESS = "oneTaskProcess";
    private static final String BUSINESS_KEY = "testBusinessKey";
    private static final String TEST_VALUE = "TestValue";
    private static final String TEST_VARIABLE = "testVariable";
    private static final String TASK_TEST_VALUE = "TaskTestValue";
    private static final String TASK_TEST_VARIABLE = "taskTestVariable";

    private static final String THE_USERTASK_PROCESS = "mobius/crystalball/simulator/impl/playback/PlaybackProcessStartTest.testUserTask.bpmn20.xml";

    @Test
    public void testProcessInstanceStartEvents() throws Exception {
        ProcessEngineImpl processEngine = initProcessEngine();

        TaskService taskService = processEngine.getTaskService();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        ManagementService managementService = processEngine.getManagementService();
        HistoryService historyService = processEngine.getHistoryService();

        // record events
        Map<String, Object> variables = new HashMap<>();
        variables.put(TEST_VARIABLE, TEST_VALUE);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(USERTASK_PROCESS, BUSINESS_KEY, variables);

        Task task = taskService.createTaskQuery().taskDefinitionKey("userTask").singleResult();
        TimeUnit.MILLISECONDS.sleep(50);
        variables = new HashMap<>();
        variables.put(TASK_TEST_VARIABLE, TASK_TEST_VALUE);
        taskService.complete(task.getId(), variables);

        // transform log events
        List<EventLogEntry> eventLogEntries = managementService.getEventLogEntries(null, null);

        EventLogTransformer transformer = new EventLogTransformer(getTransformers());

        List<SimulationEvent> simulationEvents = transformer.transform(eventLogEntries);

        SimpleEventCalendar eventCalendar = new SimpleEventCalendar(processEngine.getProcessEngineConfiguration().getClock(), new SimulationEventComparator());
        eventCalendar.addEvents(simulationEvents);

        // replay process instance run
        final SimulationDebugger simRun = new ReplaySimulationRun(processEngine, eventCalendar, getReplayHandlers(processInstance.getId()));

        simRun.init(new NoExecutionVariableScope());

        // original process is finished - there should not be any running process instance/task
        assertEquals(0, runtimeService.createProcessInstanceQuery().processDefinitionKey(USERTASK_PROCESS).count());
        assertEquals(0, taskService.createTaskQuery().taskDefinitionKey("userTask").count());

        simRun.step();

        // replay process was started
        ProcessInstance replayProcessInstance = runtimeService.createProcessInstanceQuery()
                .processDefinitionKey(USERTASK_PROCESS)
                .singleResult();
        assertNotNull(replayProcessInstance);
        assertNotEquals(replayProcessInstance.getId(), processInstance.getId());
        assertEquals(TEST_VALUE, runtimeService.getVariable(replayProcessInstance.getId(), TEST_VARIABLE));
        // there should be one task
        assertEquals(1, taskService.createTaskQuery().taskDefinitionKey("userTask").count());

        simRun.step();

        // userTask was completed - replay process was finished
        assertEquals(0, runtimeService.createProcessInstanceQuery().processDefinitionKey(USERTASK_PROCESS).count());
        assertEquals(0, taskService.createTaskQuery().taskDefinitionKey("userTask").count());
        HistoricVariableInstance variableInstance = historyService.createHistoricVariableInstanceQuery()
                .processInstanceId(replayProcessInstance.getId())
                .variableName(TASK_TEST_VARIABLE)
                .singleResult();
        assertNotNull(variableInstance);
        assertEquals(TASK_TEST_VALUE, variableInstance.getValue());

        // close simulation
        simRun.close();
        processEngine.close();
        ProcessEngines.destroy();
    }

    private ProcessEngineImpl initProcessEngine() {
        ProcessEngineConfigurationImpl configuration = getProcessEngineConfiguration();
        ProcessEngineImpl processEngine = (ProcessEngineImpl) configuration.buildProcessEngine();

        processEngine.getRepositoryService().createDeployment().addClasspathResource(THE_USERTASK_PROCESS).deploy();
        return processEngine;
    }

    private ProcessEngineConfigurationImpl getProcessEngineConfiguration() {
        ProcessEngineConfigurationImpl configuration = new mobius.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration();
        configuration.setEnableDatabaseEventLogging(true).setDatabaseSchemaUpdate("create-drop");
        return configuration;
    }

    private static List<Function<EventLogEntry, SimulationEvent>> getTransformers() {
        List<Function<EventLogEntry, SimulationEvent>> transformers = new ArrayList<>();
        transformers.add(new EventLogProcessInstanceCreateTransformer(PROCESS_INSTANCE_START_EVENT_TYPE, PROCESS_DEFINITION_ID_KEY, BUSINESS_KEY, VARIABLES_KEY));
        transformers.add(new EventLogUserTaskCompleteTransformer(USER_TASK_COMPLETED_EVENT_TYPE));
        return transformers;
    }

    public static Map<String, SimulationEventHandler> getReplayHandlers(String processInstanceId) {
        Map<String, SimulationEventHandler> handlers = new HashMap<>();
        handlers.put(PROCESS_INSTANCE_START_EVENT_TYPE, new StartReplayLogEventHandler(processInstanceId, PROCESS_DEFINITION_ID_KEY, BUSINESS_KEY, VARIABLES_KEY));
        handlers.put(USER_TASK_COMPLETED_EVENT_TYPE, new ReplayUserTaskCompleteEventHandler());
        return handlers;
    }
}
