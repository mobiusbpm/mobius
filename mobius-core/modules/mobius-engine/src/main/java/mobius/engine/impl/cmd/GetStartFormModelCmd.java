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

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import mobius.bpmn.model.BpmnModel;
import mobius.bpmn.model.FlowElement;
import mobius.bpmn.model.Process;
import mobius.bpmn.model.StartEvent;
import mobius.common.engine.api.FlowableIllegalArgumentException;
import mobius.common.engine.api.FlowableObjectNotFoundException;
import mobius.common.engine.impl.interceptor.Command;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.engine.impl.cfg.ProcessEngineConfigurationImpl;
import mobius.engine.impl.util.CommandContextUtil;
import mobius.engine.impl.util.ProcessDefinitionUtil;
import mobius.engine.repository.Deployment;
import mobius.engine.repository.ProcessDefinition;
import mobius.form.api.FormFieldHandler;
import mobius.form.api.FormInfo;
import mobius.form.api.FormService;

/**
 *
 */
public class GetStartFormModelCmd implements Command<FormInfo>, Serializable {

    private static final long serialVersionUID = 1L;

    protected String processDefinitionId;
    protected String processInstanceId;

    public GetStartFormModelCmd(String processDefinitionId, String processInstanceId) {
        this.processDefinitionId = processDefinitionId;
        this.processInstanceId = processInstanceId;
    }

    @Override
    public FormInfo execute(CommandContext commandContext) {
        ProcessEngineConfigurationImpl processEngineConfiguration = CommandContextUtil.getProcessEngineConfiguration(commandContext);
        FormService formService = CommandContextUtil.getFormService(commandContext);
        if (formService == null) {
            throw new FlowableIllegalArgumentException("Form engine is not initialized");
        }

        FormInfo formInfo = null;
        ProcessDefinition processDefinition = ProcessDefinitionUtil.getProcessDefinition(processDefinitionId);
        BpmnModel bpmnModel = ProcessDefinitionUtil.getBpmnModel(processDefinitionId);
        Process process = bpmnModel.getProcessById(processDefinition.getKey());
        FlowElement startElement = process.getInitialFlowElement();
        if (startElement instanceof StartEvent) {
            StartEvent startEvent = (StartEvent) startElement;
            if (StringUtils.isNotEmpty(startEvent.getFormKey())) {
                Deployment deployment = CommandContextUtil.getDeploymentEntityManager(commandContext).findById(processDefinition.getDeploymentId());
                formInfo = formService.getFormInstanceModelByKeyAndParentDeploymentId(startEvent.getFormKey(), deployment.getParentDeploymentId(), 
                                null, processInstanceId, null, processDefinition.getTenantId(), processEngineConfiguration.isFallbackToDefaultTenant());
            }
        }

        // If form does not exists, we don't want to leak out this info to just anyone
        if (formInfo == null) {
            throw new FlowableObjectNotFoundException("Form model for process definition " + processDefinitionId + " cannot be found");
        }

        FormFieldHandler formFieldHandler = processEngineConfiguration.getFormFieldHandler();
        formFieldHandler.enrichFormFields(formInfo);

        return formInfo;
    }

}
