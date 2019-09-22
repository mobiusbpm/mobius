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
package mobius.standalone.parsing;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import mobius.bpmn.converter.BpmnXMLConverter;
import mobius.bpmn.model.BpmnModel;
import mobius.common.engine.impl.util.io.InputStreamSource;
import mobius.common.engine.impl.util.io.StreamSource;
import mobius.engine.impl.test.ResourceFlowableTestCase;
import mobius.engine.repository.Deployment;
import org.junit.jupiter.api.Test;

public class ChineseConverterTest extends ResourceFlowableTestCase {

    public ChineseConverterTest() {
        super("mobius/standalone/parsing/encoding.flowable.cfg.xml");
    }

    @Test
    public void testConvertXMLToModel() throws Exception {
        BpmnModel bpmnModel = readXMLFile();
        bpmnModel = exportAndReadXMLFile(bpmnModel);
        deployProcess(bpmnModel);
    }

    protected String getResource() {
        return "mobius/standalone/parsing/chinese.bpmn";
    }

    protected BpmnModel readXMLFile() throws Exception {
        InputStream xmlStream = this.getClass().getClassLoader().getResourceAsStream(getResource());
        StreamSource xmlSource = new InputStreamSource(xmlStream);
        BpmnModel bpmnModel = new BpmnXMLConverter().convertToBpmnModel(xmlSource, false, false, processEngineConfiguration.getXmlEncoding());
        return bpmnModel;
    }

    protected BpmnModel exportAndReadXMLFile(BpmnModel bpmnModel) throws Exception {
        byte[] xml = new BpmnXMLConverter().convertToXML(bpmnModel, processEngineConfiguration.getXmlEncoding());
        StreamSource xmlSource = new InputStreamSource(new ByteArrayInputStream(xml));
        BpmnModel parsedModel = new BpmnXMLConverter().convertToBpmnModel(xmlSource, false, false, processEngineConfiguration.getXmlEncoding());
        return parsedModel;
    }

    protected void deployProcess(BpmnModel bpmnModel) {
        byte[] xml = new BpmnXMLConverter().convertToXML(bpmnModel);
        try {
            Deployment deployment = processEngine.getRepositoryService().createDeployment().name("test").addString("test.bpmn20.xml", new String(xml)).deploy();
            processEngine.getRepositoryService().deleteDeployment(deployment.getId());
        } finally {
            processEngine.close();
        }
    }
}