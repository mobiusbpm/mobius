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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import mobius.common.engine.api.delegate.event.FlowableEvent;
import mobius.common.engine.api.delegate.event.FlowableEventListener;
import mobius.crystalball.simulator.ReplaySimulationRun;
import mobius.crystalball.simulator.SimulationDebugger;
import mobius.crystalball.simulator.SimulationEvent;
import mobius.crystalball.simulator.SimulationEventHandler;
import mobius.crystalball.simulator.delegate.UserTaskExecutionListener;
import mobius.crystalball.simulator.delegate.event.Function;
import mobius.crystalball.simulator.delegate.event.impl.InMemoryRecordFlowableEventListener;
import mobius.crystalball.simulator.delegate.event.impl.ProcessInstanceCreateTransformer;
import mobius.crystalball.simulator.delegate.event.impl.UserTaskCompleteTransformer;
import mobius.crystalball.simulator.impl.StartReplayProcessEventHandler;
import mobius.crystalball.simulator.impl.bpmn.parser.handler.AddListenerUserTaskParseHandler;
import mobius.crystalball.simulator.impl.playback.PlaybackUserTaskCompleteEventHandler;
import mobius.engine.ProcessEngines;
import mobius.engine.RuntimeService;
import mobius.engine.TaskService;
import mobius.engine.delegate.TaskListener;
import mobius.engine.impl.ProcessEngineImpl;
import mobius.engine.impl.cfg.ProcessEngineConfigurationImpl;
import mobius.engine.parse.BpmnParseHandler;
import mobius.engine.runtime.ProcessInstance;
import mobius.task.api.Task;
import mobius.variable.service.impl.el.NoExecutionVariableScope;
import org.junit.Test;

/**
 * @author martin.grofcik
 */
public class ReplayRunTest {

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

    protected static InMemoryRecordFlowableEventListener listener = new InMemoryRecordFlowableEventListener(getTransformers());

    private static final String THE_USERTASK_PROCESS = "mobius/crystalball/simulator/impl/playback/PlaybackProcessStartTest.testUserTask.bpmn20.xml";

    @Test
    public void testProcessInstanceStartEvents() throws Exception {
        ProcessEngineImpl processEngine = initProcessEngine();

        TaskService taskService = processEngine.getTaskService();
        RuntimeService runtimeService = processEngine.getRuntimeService();

        Map<String, Object> variables = new HashMap<>();
        variables.put(TEST_VARIABLE, TEST_VALUE);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(USERTASK_PROCESS, BUSINESS_KEY, variables);

        Task task = taskService.createTaskQuery().taskDefinitionKey("userTask").singleResult();
        TimeUnit.MILLISECONDS.sleep(50);
        taskService.complete(task.getId());

        final SimulationDebugger simRun = new ReplaySimulationRun(processEngine, getReplayHandlers(processInstance.getId()));

        simRun.init(new NoExecutionVariableScope());

        // original process is finished - there should not be any running process instance/task
        assertEquals(0, runtimeService.createProcessInstanceQuery().processDefinitionKey(USERTASK_PROCESS).count());
        assertEquals(0, taskService.createTaskQuery().taskDefinitionKey("userTask").count());

        simRun.step();

        // replay process was started
        assertEquals(1, runtimeService.createProcessInstanceQuery().processDefinitionKey(USERTASK_PROCESS).count());
        // there should be one task
        assertEquals(1, taskService.createTaskQuery().taskDefinitionKey("userTask").count());

        simRun.step();

        // userTask was completed - replay process was finished
        assertEquals(0, runtimeService.createProcessInstanceQuery().processDefinitionKey(USERTASK_PROCESS).count());
        assertEquals(0, taskService.createTaskQuery().taskDefinitionKey("userTask").count());

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
        configuration.setHistory("full").setDatabaseSchemaUpdate("true");
        configuration.setCustomDefaultBpmnParseHandlers(
                Collections.<BpmnParseHandler>singletonList(
                        new AddListenerUserTaskParseHandler(TaskListener.EVENTNAME_CREATE,
                                new UserTaskExecutionListener(USER_TASK_COMPLETED_EVENT_TYPE, USER_TASK_COMPLETED_EVENT_TYPE, listener.getSimulationEvents()))));
        configuration.setEventListeners(Collections.<FlowableEventListener>singletonList(listener));
        return configuration;
    }

    private static List<Function<FlowableEvent, SimulationEvent>> getTransformers() {
        List<Function<FlowableEvent, SimulationEvent>> transformers = new ArrayList<>();
        transformers.add(new ProcessInstanceCreateTransformer(PROCESS_INSTANCE_START_EVENT_TYPE, PROCESS_DEFINITION_ID_KEY, BUSINESS_KEY, VARIABLES_KEY));
        transformers.add(new UserTaskCompleteTransformer(USER_TASK_COMPLETED_EVENT_TYPE));
        return transformers;
    }

    public static Map<String, SimulationEventHandler> getReplayHandlers(String processInstanceId) {
        Map<String, SimulationEventHandler> handlers = new HashMap<>();
        handlers.put(PROCESS_INSTANCE_START_EVENT_TYPE,
                new StartReplayProcessEventHandler(processInstanceId, PROCESS_INSTANCE_START_EVENT_TYPE, PROCESS_INSTANCE_START_EVENT_TYPE, listener.getSimulationEvents(), PROCESS_DEFINITION_ID_KEY, BUSINESS_KEY, VARIABLES_KEY));
        handlers.put(USER_TASK_COMPLETED_EVENT_TYPE, new PlaybackUserTaskCompleteEventHandler());
        return handlers;
    }
}
