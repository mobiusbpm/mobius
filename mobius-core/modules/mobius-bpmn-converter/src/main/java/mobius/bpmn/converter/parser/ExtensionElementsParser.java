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
package mobius.bpmn.converter.parser;

import java.util.List;

import javax.xml.stream.XMLStreamReader;

import mobius.bpmn.constants.BpmnXMLConstants;
import mobius.bpmn.converter.child.FlowableEventListenerParser;
import mobius.bpmn.converter.child.ExecutionListenerParser;
import mobius.bpmn.converter.util.BpmnXMLUtil;
import mobius.bpmn.model.BaseElement;
import mobius.bpmn.model.BpmnModel;
import mobius.bpmn.model.ExtensionElement;
import mobius.bpmn.model.Process;
import mobius.bpmn.model.SubProcess;

/**
 *
 */
public class ExtensionElementsParser implements BpmnXMLConstants {

    public void parse(XMLStreamReader xtr, List<SubProcess> activeSubProcessList, Process activeProcess, BpmnModel model) throws Exception {
        BaseElement parentElement = null;
        if (!activeSubProcessList.isEmpty()) {
            parentElement = activeSubProcessList.get(activeSubProcessList.size() - 1);

        } else {
            parentElement = activeProcess;
        }

        boolean readyWithChildElements = false;
        while (!readyWithChildElements && xtr.hasNext()) {
            xtr.next();
            if (xtr.isStartElement()) {
                if (ELEMENT_EXECUTION_LISTENER.equals(xtr.getLocalName())) {
                    new ExecutionListenerParser().parseChildElement(xtr, parentElement, model);
                } else if (ELEMENT_EVENT_LISTENER.equals(xtr.getLocalName())) {
                    new FlowableEventListenerParser().parseChildElement(xtr, parentElement, model);
                } else if (ELEMENT_POTENTIAL_STARTER.equals(xtr.getLocalName())) {
                    new PotentialStarterParser().parse(xtr, activeProcess);
                } else {
                    ExtensionElement extensionElement = BpmnXMLUtil.parseExtensionElement(xtr);
                    parentElement.addExtensionElement(extensionElement);
                }

            } else if (xtr.isEndElement()) {
                if (ELEMENT_EXTENSIONS.equals(xtr.getLocalName())) {
                    readyWithChildElements = true;
                }
            }
        }
    }
}
