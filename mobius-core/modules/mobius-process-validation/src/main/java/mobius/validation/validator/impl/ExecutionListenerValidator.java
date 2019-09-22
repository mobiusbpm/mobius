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

import mobius.bpmn.model.BaseElement;
import mobius.bpmn.model.BpmnModel;
import mobius.bpmn.model.FlowElement;
import mobius.bpmn.model.FlowableListener;
import mobius.bpmn.model.ImplementationType;
import mobius.bpmn.model.Process;
import mobius.validation.ValidationError;
import mobius.validation.validator.Problems;
import mobius.validation.validator.ProcessLevelValidator;

/**
 * @author jbarrez
 */
public class ExecutionListenerValidator extends ProcessLevelValidator {

    @Override
    protected void executeValidation(BpmnModel bpmnModel, Process process, List<ValidationError> errors) {

        validateListeners(process, process, process.getExecutionListeners(), errors);

        for (FlowElement flowElement : process.getFlowElements()) {
            validateListeners(process, flowElement, flowElement.getExecutionListeners(), errors);
        }
    }

    protected void validateListeners(Process process, BaseElement baseElement, List<FlowableListener> listeners, List<ValidationError> errors) {
        if (listeners != null) {
            for (FlowableListener listener : listeners) {
                if (listener.getImplementation() == null || listener.getImplementationType() == null) {
                    addError(errors, Problems.EXECUTION_LISTENER_IMPLEMENTATION_MISSING, process, baseElement, "Element 'class' or 'expression' is mandatory on executionListener");
                }
                if (listener.getOnTransaction() != null && ImplementationType.IMPLEMENTATION_TYPE_EXPRESSION.equals(listener.getImplementationType())) {
                    addError(errors, Problems.EXECUTION_LISTENER_INVALID_IMPLEMENTATION_TYPE, process, baseElement, "Expression cannot be used when using 'onTransaction'");
                }
            }
        }
    }
}
