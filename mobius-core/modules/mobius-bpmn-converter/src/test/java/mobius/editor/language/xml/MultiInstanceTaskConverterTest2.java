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
import mobius.bpmn.model.MultiInstanceLoopCharacteristics;
import mobius.bpmn.model.Process;
import mobius.bpmn.model.StartEvent;
import mobius.bpmn.model.SubProcess;
import mobius.bpmn.model.UserTask;
import org.junit.Test;

/**
 * @see <a href="https://github.com/flowable/flowable-engine/issues/474">Issue 474</a>
 */
public class MultiInstanceTaskConverterTest2 extends AbstractConverterTest {
    private static final String PARTICIPANT_VALUE = "[\n" +
"                   {\n" +
"                     \"principalType\" : \"User\",\n" +
"                     \"role\" : \"PotentialOwner\",\n" +
"                     \"principal\" : \"wfuser1\",\n" +
"                     \"version\" : 1\n" +
"                   },\n" +
"                   {\n" +
"                     \"principalType\" : \"User\",\n" +
"                     \"role\" : \"PotentialOwner\",\n" +
"                     \"principal\" : \"wfuser2\",\n" +
"                     \"version\" : 1\n" +
"                   }\n" +
"                 ]";

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
        return "multiinstancemodel2.bpmn";
    }

    private void validateModel(BpmnModel model) {
        Process main = model.getMainProcess();

        // verify start
        FlowElement flowElement = main.getFlowElement("start1");
        assertNotNull(flowElement);
        assertTrue(flowElement instanceof StartEvent);

        // verify user task
        flowElement = main.getFlowElement("userTask1");
        assertNotNull(flowElement);
        assertEquals("User task 1", flowElement.getName());
        assertTrue(flowElement instanceof UserTask);

        UserTask task = (UserTask) flowElement;
        MultiInstanceLoopCharacteristics loopCharacteristics = task.getLoopCharacteristics();
        assertEquals("participant", loopCharacteristics.getElementVariable());
        assertEquals(PARTICIPANT_VALUE, loopCharacteristics.getCollectionString().trim());
        assertEquals("delegateExpression", loopCharacteristics.getHandler().getImplementationType());
        assertEquals("${collectionHandler}", loopCharacteristics.getHandler().getImplementation());

        // verify subprocess
        flowElement = main.getFlowElement("subprocess1");
        assertNotNull(flowElement);
        assertEquals("subProcess", flowElement.getName());
        assertTrue(flowElement instanceof SubProcess);

        SubProcess subProcess = (SubProcess) flowElement;
        loopCharacteristics = subProcess.getLoopCharacteristics();
        assertTrue(loopCharacteristics.isSequential());
        assertEquals("10", loopCharacteristics.getLoopCardinality());
        assertEquals(5, subProcess.getFlowElements().size());

        // verify user task in subprocess
        flowElement = subProcess.getFlowElement("subUserTask1");
        assertNotNull(flowElement);
        assertEquals("User task 2", flowElement.getName());
        assertTrue(flowElement instanceof UserTask);

        task = (UserTask) flowElement;
        loopCharacteristics = task.getLoopCharacteristics();
        assertEquals("participant", loopCharacteristics.getElementVariable());
        assertEquals("${potentialOwnerList}", loopCharacteristics.getInputDataItem());
        assertEquals("delegateExpression", loopCharacteristics.getHandler().getImplementationType());
        assertEquals("${collectionHandler}", loopCharacteristics.getHandler().getImplementation());
    }
}