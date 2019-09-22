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
package mobius.validation.validator;

import mobius.validation.validator.impl.AssociationValidator;
import mobius.validation.validator.impl.BoundaryEventValidator;
import mobius.validation.validator.impl.BpmnModelValidator;
import mobius.validation.validator.impl.DataObjectValidator;
import mobius.validation.validator.impl.DiagramInterchangeInfoValidator;
import mobius.validation.validator.impl.EndEventValidator;
import mobius.validation.validator.impl.ErrorValidator;
import mobius.validation.validator.impl.EventGatewayValidator;
import mobius.validation.validator.impl.EventSubprocessValidator;
import mobius.validation.validator.impl.EventValidator;
import mobius.validation.validator.impl.ExclusiveGatewayValidator;
import mobius.validation.validator.impl.ExecutionListenerValidator;
import mobius.validation.validator.impl.FlowElementValidator;
import mobius.validation.validator.impl.FlowableEventListenerValidator;
import mobius.validation.validator.impl.IntermediateCatchEventValidator;
import mobius.validation.validator.impl.IntermediateThrowEventValidator;
import mobius.validation.validator.impl.MessageValidator;
import mobius.validation.validator.impl.OperationValidator;
import mobius.validation.validator.impl.ScriptTaskValidator;
import mobius.validation.validator.impl.SendTaskValidator;
import mobius.validation.validator.impl.SequenceflowValidator;
import mobius.validation.validator.impl.ServiceTaskValidator;
import mobius.validation.validator.impl.SignalValidator;
import mobius.validation.validator.impl.StartEventValidator;
import mobius.validation.validator.impl.SubprocessValidator;
import mobius.validation.validator.impl.UserTaskValidator;

/**
 * @author jbarrez
 */
public class ValidatorSetFactory {

    public ValidatorSet createFlowableExecutableProcessValidatorSet() {
        ValidatorSet validatorSet = new ValidatorSet(ValidatorSetNames.FLOWABLE_EXECUTABLE_PROCESS);

        validatorSet.addValidator(new AssociationValidator());
        validatorSet.addValidator(new SignalValidator());
        validatorSet.addValidator(new OperationValidator());
        validatorSet.addValidator(new ErrorValidator());
        validatorSet.addValidator(new DataObjectValidator());

        validatorSet.addValidator(new BpmnModelValidator());
        validatorSet.addValidator(new FlowElementValidator());

        validatorSet.addValidator(new StartEventValidator());
        validatorSet.addValidator(new SequenceflowValidator());
        validatorSet.addValidator(new UserTaskValidator());
        validatorSet.addValidator(new ServiceTaskValidator());
        validatorSet.addValidator(new ScriptTaskValidator());
        validatorSet.addValidator(new SendTaskValidator());
        validatorSet.addValidator(new ExclusiveGatewayValidator());
        validatorSet.addValidator(new EventGatewayValidator());
        validatorSet.addValidator(new SubprocessValidator());
        validatorSet.addValidator(new EventSubprocessValidator());
        validatorSet.addValidator(new BoundaryEventValidator());
        validatorSet.addValidator(new IntermediateCatchEventValidator());
        validatorSet.addValidator(new IntermediateThrowEventValidator());
        validatorSet.addValidator(new MessageValidator());
        validatorSet.addValidator(new EventValidator());
        validatorSet.addValidator(new EndEventValidator());

        validatorSet.addValidator(new ExecutionListenerValidator());
        validatorSet.addValidator(new FlowableEventListenerValidator());

        validatorSet.addValidator(new DiagramInterchangeInfoValidator());

        return validatorSet;
    }

}
