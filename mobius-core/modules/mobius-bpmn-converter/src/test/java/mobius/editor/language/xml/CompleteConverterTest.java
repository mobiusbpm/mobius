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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import mobius.bpmn.model.BpmnModel;
import mobius.bpmn.model.FlowElement;
import mobius.bpmn.model.IntermediateCatchEvent;
import mobius.bpmn.model.ReceiveTask;
import mobius.bpmn.model.SignalEventDefinition;
import mobius.bpmn.model.SubProcess;
import mobius.bpmn.model.UserTask;
import org.junit.Test;

public class CompleteConverterTest extends AbstractConverterTest {

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

    @Override
    protected String getResource() {
        return "completemodel.bpmn";
    }

    private void validateModel(BpmnModel model) {
        FlowElement flowElement = model.getMainProcess().getFlowElement("userTask1");
        assertNotNull(flowElement);
        assertTrue(flowElement instanceof UserTask);
        assertEquals("userTask1", flowElement.getId());

        flowElement = model.getMainProcess().getFlowElement("catchsignal");
        assertNotNull(flowElement);
        assertTrue(flowElement instanceof IntermediateCatchEvent);
        assertEquals("catchsignal", flowElement.getId());
        IntermediateCatchEvent catchEvent = (IntermediateCatchEvent) flowElement;
        assertEquals(1, catchEvent.getEventDefinitions().size());
        assertTrue(catchEvent.getEventDefinitions().get(0) instanceof SignalEventDefinition);
        SignalEventDefinition signalEvent = (SignalEventDefinition) catchEvent.getEventDefinitions().get(0);
        assertEquals("testSignal", signalEvent.getSignalRef());

        flowElement = model.getMainProcess().getFlowElement("subprocess");
        assertNotNull(flowElement);
        assertTrue(flowElement instanceof SubProcess);
        assertEquals("subprocess", flowElement.getId());
        SubProcess subProcess = (SubProcess) flowElement;

        flowElement = subProcess.getFlowElement("receiveTask");
        assertNotNull(flowElement);
        assertTrue(flowElement instanceof ReceiveTask);
        assertEquals("receiveTask", flowElement.getId());
    }
}
