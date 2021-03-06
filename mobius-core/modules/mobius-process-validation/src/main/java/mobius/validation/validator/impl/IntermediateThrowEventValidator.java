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
package mobius.validation.validator.impl;

import java.util.List;

import mobius.bpmn.model.BpmnModel;
import mobius.bpmn.model.CompensateEventDefinition;
import mobius.bpmn.model.EscalationEventDefinition;
import mobius.bpmn.model.EventDefinition;
import mobius.bpmn.model.Process;
import mobius.bpmn.model.SignalEventDefinition;
import mobius.bpmn.model.ThrowEvent;
import mobius.validation.ValidationError;
import mobius.validation.validator.Problems;
import mobius.validation.validator.ProcessLevelValidator;

/**
 * @author jbarrez
 */
public class IntermediateThrowEventValidator extends ProcessLevelValidator {

    @Override
    protected void executeValidation(BpmnModel bpmnModel, Process process, List<ValidationError> errors) {
        List<ThrowEvent> throwEvents = process.findFlowElementsOfType(ThrowEvent.class);
        for (ThrowEvent throwEvent : throwEvents) {
            EventDefinition eventDefinition = null;
            if (!throwEvent.getEventDefinitions().isEmpty()) {
                eventDefinition = throwEvent.getEventDefinitions().get(0);
            }

            if (eventDefinition != null && !(eventDefinition instanceof SignalEventDefinition) && 
                            !(eventDefinition instanceof EscalationEventDefinition) && !(eventDefinition instanceof CompensateEventDefinition)) {
                
                addError(errors, Problems.THROW_EVENT_INVALID_EVENTDEFINITION, process, throwEvent, "Unsupported intermediate throw event type");
            }
        }
    }

}
