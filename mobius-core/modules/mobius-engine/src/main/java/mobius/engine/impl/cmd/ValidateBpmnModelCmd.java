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
package mobius.engine.impl.cmd;

import java.util.List;

import mobius.bpmn.model.BpmnModel;
import mobius.common.engine.api.FlowableException;
import mobius.common.engine.impl.interceptor.Command;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.engine.impl.util.CommandContextUtil;
import mobius.validation.ProcessValidator;
import mobius.validation.ValidationError;

/**
 *
 */
public class ValidateBpmnModelCmd implements Command<List<ValidationError>> {

    protected BpmnModel bpmnModel;

    public ValidateBpmnModelCmd(BpmnModel bpmnModel) {
        this.bpmnModel = bpmnModel;
    }

    @Override
    public List<ValidationError> execute(CommandContext commandContext) {
        ProcessValidator processValidator = CommandContextUtil.getProcessEngineConfiguration(commandContext).getProcessValidator();
        if (processValidator == null) {
            throw new FlowableException("No process validator defined");
        }

        return processValidator.validate(bpmnModel);
    }

}
