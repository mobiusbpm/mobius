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
package mobius.engine.impl.bpmn.parser.handler;

import java.util.List;

import mobius.bpmn.model.BaseElement;
import mobius.bpmn.model.BoundaryEvent;
import mobius.bpmn.model.BpmnModel;
import mobius.bpmn.model.ExtensionElement;
import mobius.bpmn.model.IntermediateCatchEvent;
import mobius.bpmn.model.Message;
import mobius.bpmn.model.MessageEventDefinition;
import mobius.engine.impl.bpmn.parser.BpmnParse;

/**
 *
 * @author Tijs Rademakers
 */
public class MessageEventDefinitionParseHandler extends AbstractBpmnParseHandler<MessageEventDefinition> {

    @Override
    public Class<? extends BaseElement> getHandledType() {
        return MessageEventDefinition.class;
    }

    @Override
    protected void executeParse(BpmnParse bpmnParse, MessageEventDefinition messageDefinition) {
        BpmnModel bpmnModel = bpmnParse.getBpmnModel();
        String messageRef = messageDefinition.getMessageRef();
        if (bpmnModel.containsMessageId(messageRef)) {
            Message message = bpmnModel.getMessage(messageRef);
            messageDefinition.setMessageRef(message.getName());

            for(List<ExtensionElement> extensionElementList : message.getExtensionElements().values()) {
                for(ExtensionElement extensionElement : extensionElementList) {
                    messageDefinition.addExtensionElement(extensionElement);
                }
            }
        }

        if (bpmnParse.getCurrentFlowElement() instanceof IntermediateCatchEvent) {
            IntermediateCatchEvent intermediateCatchEvent = (IntermediateCatchEvent) bpmnParse.getCurrentFlowElement();
            intermediateCatchEvent.setBehavior(bpmnParse.getActivityBehaviorFactory().createIntermediateCatchMessageEventActivityBehavior(intermediateCatchEvent, messageDefinition));

        } else if (bpmnParse.getCurrentFlowElement() instanceof BoundaryEvent) {
            BoundaryEvent boundaryEvent = (BoundaryEvent) bpmnParse.getCurrentFlowElement();
            boundaryEvent.setBehavior(bpmnParse.getActivityBehaviorFactory().createBoundaryMessageEventActivityBehavior(boundaryEvent, messageDefinition, boundaryEvent.isCancelActivity()));
        }

        else {
            // What to do here?
        }

    }

}
