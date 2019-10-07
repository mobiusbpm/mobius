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
package mobius.examples.bpmn.executionlistener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mobius.common.engine.impl.history.HistoryLevel;
import mobius.engine.impl.test.HistoryTestHelper;
import mobius.engine.impl.test.PluggableFlowableTestCase;
import mobius.engine.runtime.ProcessInstance;
import mobius.engine.test.Deployment;
import mobius.variable.api.history.HistoricVariableInstance;
import org.junit.jupiter.api.Test;

/**
 *
 */
public class ScriptExecutionListenerTest extends PluggableFlowableTestCase {

    @Test
    @Deployment(resources = { "mobius/examples/bpmn/executionlistener/ScriptExecutionListenerTest.bpmn20.xml" })
    public void testScriptExecutionListener() {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("scriptExecutionListenerProcess");

        if (HistoryTestHelper.isHistoryLevelAtLeast(HistoryLevel.ACTIVITY, processEngineConfiguration)) {
            List<HistoricVariableInstance> historicVariables = historyService.createHistoricVariableInstanceQuery().processInstanceId(processInstance.getId()).list();
            Map<String, Object> varMap = new HashMap<>();
            for (HistoricVariableInstance historicVariableInstance : historicVariables) {
                varMap.put(historicVariableInstance.getVariableName(), historicVariableInstance.getValue());
            }

            assertTrue(varMap.containsKey("foo"));
            assertEquals("FOO", varMap.get("foo"));
            assertTrue(varMap.containsKey("var1"));
            assertEquals("test", varMap.get("var1"));
            assertFalse(varMap.containsKey("bar"));
            assertTrue(varMap.containsKey("myVar"));
            assertEquals("BAR", varMap.get("myVar"));
        }
    }

}
