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
import mobius.bpmn.model.ConditionalEventDefinition;
import mobius.bpmn.model.ErrorEventDefinition;
import mobius.bpmn.model.EscalationEventDefinition;
import mobius.bpmn.model.EventDefinition;
import mobius.bpmn.model.EventSubProcess;
import mobius.bpmn.model.MessageEventDefinition;
import mobius.bpmn.model.Process;
import mobius.bpmn.model.SignalEventDefinition;
import mobius.bpmn.model.StartEvent;
import mobius.bpmn.model.TimerEventDefinition;
import mobius.validation.ValidationError;
import mobius.validation.validator.Problems;
import mobius.validation.validator.ProcessLevelValidator;

/**
 * @author jbarrez
 */
public class EventSubprocessValidator extends ProcessLevelValidator {

    @Override
    protected void executeValidation(BpmnModel bpmnModel, Process process, List<ValidationError> errors) {
        List<EventSubProcess> eventSubprocesses = process.findFlowElementsOfType(EventSubProcess.class);
        for (EventSubProcess eventSubprocess : eventSubprocesses) {

            List<StartEvent> startEvents = process.findFlowElementsInSubProcessOfType(eventSubprocess, StartEvent.class);
            for (StartEvent startEvent : startEvents) {
                if (startEvent.getEventDefinitions() != null && !startEvent.getEventDefinitions().isEmpty()) {
                    EventDefinition eventDefinition = startEvent.getEventDefinitions().get(0);
                    if (!(eventDefinition instanceof ConditionalEventDefinition) &&
                            !(eventDefinition instanceof ErrorEventDefinition) &&
                            !(eventDefinition instanceof EscalationEventDefinition) &&
                            !(eventDefinition instanceof MessageEventDefinition) &&
                            !(eventDefinition instanceof SignalEventDefinition) &&
                            !(eventDefinition instanceof TimerEventDefinition)) {

                        addError(errors, Problems.EVENT_SUBPROCESS_INVALID_START_EVENT_DEFINITION, process, eventSubprocess,
                                "start event of event subprocess must be of type 'error', 'timer', 'message' or 'signal'");
                    }
                }
            }

        }
    }

}
