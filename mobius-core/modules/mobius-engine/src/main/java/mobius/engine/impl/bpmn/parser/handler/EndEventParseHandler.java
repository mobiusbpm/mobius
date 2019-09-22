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

import org.apache.commons.lang3.StringUtils;
import mobius.bpmn.model.BaseElement;
import mobius.bpmn.model.CancelEventDefinition;
import mobius.bpmn.model.EndEvent;
import mobius.bpmn.model.ErrorEventDefinition;
import mobius.bpmn.model.Escalation;
import mobius.bpmn.model.EscalationEventDefinition;
import mobius.bpmn.model.EventDefinition;
import mobius.bpmn.model.TerminateEventDefinition;
import mobius.engine.impl.bpmn.parser.BpmnParse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Joram Barrez
 * @author Tijs Rademakers
 */
public class EndEventParseHandler extends AbstractActivityBpmnParseHandler<EndEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EndEventParseHandler.class);

    @Override
    public Class<? extends BaseElement> getHandledType() {
        return EndEvent.class;
    }

    @Override
    protected void executeParse(BpmnParse bpmnParse, EndEvent endEvent) {

        EventDefinition eventDefinition = null;
        if (endEvent.getEventDefinitions().size() > 0) {
            eventDefinition = endEvent.getEventDefinitions().get(0);

            if (eventDefinition instanceof ErrorEventDefinition) {
                ErrorEventDefinition errorDefinition = (ErrorEventDefinition) eventDefinition;
                if (bpmnParse.getBpmnModel().containsErrorRef(errorDefinition.getErrorCode())) {
                    String errorCode = bpmnParse.getBpmnModel().getErrors().get(errorDefinition.getErrorCode());
                    if (StringUtils.isEmpty(errorCode)) {
                        LOGGER.warn("errorCode is required for an error event {}", endEvent.getId());
                    }
                }
                endEvent.setBehavior(bpmnParse.getActivityBehaviorFactory().createErrorEndEventActivityBehavior(endEvent, errorDefinition));
                
            } else if (eventDefinition instanceof EscalationEventDefinition) {
                EscalationEventDefinition escalationDefinition = (EscalationEventDefinition) eventDefinition;
                Escalation escalation = null;
                if (bpmnParse.getBpmnModel().containsEscalationRef(escalationDefinition.getEscalationCode())) {
                    escalation = bpmnParse.getBpmnModel().getEscalation(escalationDefinition.getEscalationCode());
                    String escalationCode = escalation.getEscalationCode();
                    if (StringUtils.isEmpty(escalationCode)) {
                        LOGGER.warn("escalationCode is required for an escalation event {}", endEvent.getId());
                    }
                }
                endEvent.setBehavior(bpmnParse.getActivityBehaviorFactory().createEscalationEndEventActivityBehavior(endEvent, escalationDefinition, escalation));
                
            } else if (eventDefinition instanceof TerminateEventDefinition) {
                endEvent.setBehavior(bpmnParse.getActivityBehaviorFactory().createTerminateEndEventActivityBehavior(endEvent));
            } else if (eventDefinition instanceof CancelEventDefinition) {
                endEvent.setBehavior(bpmnParse.getActivityBehaviorFactory().createCancelEndEventActivityBehavior(endEvent));
            } else {
                endEvent.setBehavior(bpmnParse.getActivityBehaviorFactory().createNoneEndEventActivityBehavior(endEvent));
            }

        } else {
            endEvent.setBehavior(bpmnParse.getActivityBehaviorFactory().createNoneEndEventActivityBehavior(endEvent));
        }
    }

}
