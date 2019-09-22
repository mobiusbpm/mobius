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
package mobius.editor.language.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import mobius.bpmn.model.BpmnModel;
import mobius.bpmn.model.ConditionalEventDefinition;
import mobius.bpmn.model.FlowElement;
import mobius.bpmn.model.IntermediateCatchEvent;
import org.junit.Test;

public class ConditionalConverterTest extends AbstractConverterTest {

    @Test
    public void convertXMLToModel() throws Exception {
        BpmnModel bpmnModel = readXMLFile();
        validateModel(bpmnModel);
    }

    @Test
    public void convertModelToXML() throws Exception {
        BpmnModel bpmnModel = readXMLFile();
        BpmnModel parsedModel = exportAndReadXMLFile(bpmnModel);
        validateModel(parsedModel);
    }

    private void validateModel(BpmnModel model) {
        FlowElement flowElement = model.getFlowElement("conditionalCatch");
        assertTrue(flowElement instanceof IntermediateCatchEvent);
        
        IntermediateCatchEvent catchEvent = (IntermediateCatchEvent) flowElement;
        assertEquals(1, catchEvent.getEventDefinitions().size());
        ConditionalEventDefinition event = (ConditionalEventDefinition) catchEvent.getEventDefinitions().get(0);
        assertEquals("${testVar == 'test'}", event.getConditionExpression());
    }

    @Override
    protected String getResource() {
        return "conditionaltest.bpmn";
    }
}
