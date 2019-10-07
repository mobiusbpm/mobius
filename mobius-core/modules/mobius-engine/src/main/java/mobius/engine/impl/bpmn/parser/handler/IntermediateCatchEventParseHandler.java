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

import mobius.bpmn.model.BaseElement;
import mobius.bpmn.model.ConditionalEventDefinition;
import mobius.bpmn.model.EventDefinition;
import mobius.bpmn.model.IntermediateCatchEvent;
import mobius.bpmn.model.MessageEventDefinition;
import mobius.bpmn.model.SignalEventDefinition;
import mobius.bpmn.model.TimerEventDefinition;
import mobius.engine.impl.bpmn.parser.BpmnParse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Tijs Rademakers
 */
public class IntermediateCatchEventParseHandler extends AbstractFlowNodeBpmnParseHandler<IntermediateCatchEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(IntermediateCatchEventParseHandler.class);

    @Override
    public Class<? extends BaseElement> getHandledType() {
        return IntermediateCatchEvent.class;
    }

    @Override
    protected void executeParse(BpmnParse bpmnParse, IntermediateCatchEvent event) {
        EventDefinition eventDefinition = null;
        if (!event.getEventDefinitions().isEmpty()) {
            eventDefinition = event.getEventDefinitions().get(0);
        }

        if (eventDefinition == null) {
            event.setBehavior(bpmnParse.getActivityBehaviorFactory().createIntermediateCatchEventActivityBehavior(event));

        } else {
            if (eventDefinition instanceof TimerEventDefinition || eventDefinition instanceof SignalEventDefinition || 
                            eventDefinition instanceof MessageEventDefinition || eventDefinition instanceof ConditionalEventDefinition) {

                bpmnParse.getBpmnParserHandlers().parseElement(bpmnParse, eventDefinition);

            } else {
                LOGGER.warn("Unsupported intermediate catch event type for event {}", event.getId());
            }
        }
    }

}
