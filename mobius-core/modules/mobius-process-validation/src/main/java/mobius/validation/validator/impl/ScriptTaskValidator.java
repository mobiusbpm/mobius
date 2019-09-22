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

import org.apache.commons.lang3.StringUtils;
import mobius.bpmn.model.BpmnModel;
import mobius.bpmn.model.Process;
import mobius.bpmn.model.ScriptTask;
import mobius.validation.ValidationError;
import mobius.validation.validator.Problems;
import mobius.validation.validator.ProcessLevelValidator;

/**
 * @author jbarrez
 */
public class ScriptTaskValidator extends ProcessLevelValidator {

    @Override
    protected void executeValidation(BpmnModel bpmnModel, Process process, List<ValidationError> errors) {
        List<ScriptTask> scriptTasks = process.findFlowElementsOfType(ScriptTask.class);
        for (ScriptTask scriptTask : scriptTasks) {
            if (StringUtils.isEmpty(scriptTask.getScript())) {
                addError(errors, Problems.SCRIPT_TASK_MISSING_SCRIPT, process, scriptTask, "No script provided for script task");
            }
        }
    }

}
