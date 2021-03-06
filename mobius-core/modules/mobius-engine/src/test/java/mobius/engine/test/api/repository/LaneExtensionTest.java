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
package mobius.engine.test.api.repository;

import java.util.List;
import java.util.Map;

import mobius.bpmn.converter.BpmnXMLConverter;
import mobius.bpmn.model.BpmnModel;
import mobius.bpmn.model.ExtensionElement;
import mobius.bpmn.model.Lane;
import mobius.bpmn.model.Process;
import mobius.engine.impl.test.PluggableFlowableTestCase;
import mobius.engine.repository.ProcessDefinition;
import mobius.engine.test.Deployment;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

/**
 * Created by P3700487 on 2/19/2015.
 */
public class LaneExtensionTest extends PluggableFlowableTestCase {

    @Test
    @Deployment
    public void testLaneExtensionElement() {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey("swimlane-extension").singleResult();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());
        byte[] xml = new BpmnXMLConverter().convertToXML(bpmnModel);
        System.out.println(new String(xml));
        Process bpmnProcess = bpmnModel.getMainProcess();
        for (Lane l : bpmnProcess.getLanes()) {
            Map<String, List<ExtensionElement>> extensions = l.getExtensionElements();
            Assert.assertTrue(extensions.size() > 0);
        }
    }

}
