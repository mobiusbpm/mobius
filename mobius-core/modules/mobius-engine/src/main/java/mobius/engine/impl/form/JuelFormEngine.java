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
package mobius.engine.impl.form;

import java.nio.charset.StandardCharsets;

import mobius.common.engine.api.FlowableObjectNotFoundException;
import mobius.common.engine.impl.scripting.ScriptingEngines;
import mobius.engine.form.FormData;
import mobius.engine.form.StartFormData;
import mobius.engine.form.TaskFormData;
import mobius.engine.impl.persistence.entity.ExecutionEntity;
import mobius.engine.impl.persistence.entity.ResourceEntity;
import mobius.engine.impl.util.CommandContextUtil;
import mobius.task.service.impl.persistence.entity.TaskEntity;

/**
 *
 */
public class JuelFormEngine implements FormEngine {

    @Override
    public String getName() {
        return "juel";
    }

    @Override
    public Object renderStartForm(StartFormData startForm) {
        if (startForm.getFormKey() == null) {
            return null;
        }
        String formTemplateString = getFormTemplateString(startForm, startForm.getFormKey());
        ScriptingEngines scriptingEngines = CommandContextUtil.getProcessEngineConfiguration().getScriptingEngines();
        return scriptingEngines.evaluate(formTemplateString, ScriptingEngines.DEFAULT_SCRIPTING_LANGUAGE, null);
    }

    @Override
    public Object renderTaskForm(TaskFormData taskForm) {
        if (taskForm.getFormKey() == null) {
            return null;
        }
        String formTemplateString = getFormTemplateString(taskForm, taskForm.getFormKey());
        ScriptingEngines scriptingEngines = CommandContextUtil.getProcessEngineConfiguration().getScriptingEngines();
        TaskEntity task = (TaskEntity) taskForm.getTask();
        
        ExecutionEntity executionEntity = null;
        if (task.getExecutionId() != null) {
            executionEntity = CommandContextUtil.getExecutionEntityManager().findById(task.getExecutionId());
        }
        
        return scriptingEngines.evaluate(formTemplateString, ScriptingEngines.DEFAULT_SCRIPTING_LANGUAGE, executionEntity);
    }

    protected String getFormTemplateString(FormData formInstance, String formKey) {
        String deploymentId = formInstance.getDeploymentId();

        ResourceEntity resourceStream = CommandContextUtil.getResourceEntityManager().findResourceByDeploymentIdAndResourceName(deploymentId, formKey);

        if (resourceStream == null) {
            throw new FlowableObjectNotFoundException("Form with formKey '" + formKey + "' does not exist", String.class);
        }

        return new String(resourceStream.getBytes(), StandardCharsets.UTF_8);
    }
}
