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
import mobius.bpmn.model.Lane;
import mobius.bpmn.model.Pool;
import mobius.bpmn.model.Process;
import mobius.bpmn.model.SequenceFlow;
import org.junit.Test;

public class PoolsConverterTest extends AbstractConverterTest {

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
        return "pools.bpmn";
    }

    private void validateModel(BpmnModel model) {
        assertEquals(1, model.getPools().size());
        Pool pool = model.getPools().get(0);
        assertEquals("pool1", pool.getId());
        assertEquals("Pool", pool.getName());
        Process process = model.getProcess(pool.getId());
        assertNotNull(process);
        assertEquals(2, process.getLanes().size());
        Lane lane = process.getLanes().get(0);
        assertEquals("lane1", lane.getId());
        assertEquals("Lane 1", lane.getName());
        assertEquals(2, lane.getFlowReferences().size());
        lane = process.getLanes().get(1);
        assertEquals("lane2", lane.getId());
        assertEquals("Lane 2", lane.getName());
        assertEquals(2, lane.getFlowReferences().size());
        FlowElement flowElement = process.getFlowElement("flow1");
        assertNotNull(flowElement);
        assertTrue(flowElement instanceof SequenceFlow);
    }
}
