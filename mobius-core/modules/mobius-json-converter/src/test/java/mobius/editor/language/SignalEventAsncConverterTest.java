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
package mobius.editor.language;

import static junit.framework.TestCase.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import mobius.bpmn.model.BpmnModel;
import mobius.bpmn.model.EventDefinition;
import mobius.bpmn.model.SignalEventDefinition;
import mobius.bpmn.model.ThrowEvent;
import org.junit.Test;

/**
 * @author Zheng Ji
 */
public class SignalEventAsncConverterTest extends AbstractConverterTest {

    @Test
    public void convertJsonToModel() throws Exception {
        BpmnModel bpmnModel = readJsonFile();
        validateModel(bpmnModel);
    }

    @Override
    protected String getResource() {
        return "test.signaleventasnc.json";
    }

    private void validateModel(BpmnModel model) {

        ThrowEvent throwEvent = (ThrowEvent) model.getMainProcess().getFlowElement("throwEvent", true);
        List<EventDefinition> eventDefinitions = throwEvent.getEventDefinitions();
        assertThat(eventDefinitions).isNotNull();
        assertThat(eventDefinitions.size()).isNotEqualTo(0);

        EventDefinition eventDefinition = eventDefinitions.get(0);
        assertThat(eventDefinitions).isNotNull();

        SignalEventDefinition signalEventDefinition= (SignalEventDefinition) eventDefinition;
        assertTrue(signalEventDefinition.isAsync());
    }

}
