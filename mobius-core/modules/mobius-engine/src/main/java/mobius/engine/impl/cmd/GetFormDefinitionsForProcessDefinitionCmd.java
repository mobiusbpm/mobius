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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import mobius.bpmn.model.BpmnModel;
import mobius.bpmn.model.StartEvent;
import mobius.bpmn.model.UserTask;
import mobius.common.engine.api.FlowableException;
import mobius.common.engine.api.FlowableObjectNotFoundException;
import mobius.common.engine.impl.interceptor.Command;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.engine.impl.util.CommandContextUtil;
import mobius.engine.impl.util.ProcessDefinitionUtil;
import mobius.engine.repository.Deployment;
import mobius.engine.repository.ProcessDefinition;
import mobius.form.api.FormDefinition;
import mobius.form.api.FormDefinitionQuery;
import mobius.form.api.FormDeployment;
import mobius.form.api.FormRepositoryService;

/**
 * @author Yvo Swillens
 */
public class GetFormDefinitionsForProcessDefinitionCmd implements Command<List<FormDefinition>>, Serializable {

    private static final long serialVersionUID = 1L;
    protected String processDefinitionId;
    protected FormRepositoryService formRepositoryService;

    public GetFormDefinitionsForProcessDefinitionCmd(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    @Override
    public List<FormDefinition> execute(CommandContext commandContext) {
        ProcessDefinition processDefinition = ProcessDefinitionUtil.getProcessDefinition(processDefinitionId);

        if (processDefinition == null) {
            throw new FlowableObjectNotFoundException("Cannot find process definition for id: " + processDefinitionId, ProcessDefinition.class);
        }

        BpmnModel bpmnModel = ProcessDefinitionUtil.getBpmnModel(processDefinitionId);

        if (bpmnModel == null) {
            throw new FlowableObjectNotFoundException("Cannot find bpmn model for process definition id: " + processDefinitionId, BpmnModel.class);
        }

        if (CommandContextUtil.getFormRepositoryService() == null) {
            throw new FlowableException("Form repository service is not available");
        }

        formRepositoryService = CommandContextUtil.getFormRepositoryService();
        List<FormDefinition> formDefinitions = getFormDefinitionsFromModel(bpmnModel, processDefinition);

        return formDefinitions;
    }

    protected List<FormDefinition> getFormDefinitionsFromModel(BpmnModel bpmnModel, ProcessDefinition processDefinition) {
        Set<String> formKeys = new HashSet<>();
        List<FormDefinition> formDefinitions = new ArrayList<>();

        // for all start events
        List<StartEvent> startEvents = bpmnModel.getMainProcess().findFlowElementsOfType(StartEvent.class, true);

        for (StartEvent startEvent : startEvents) {
            if (StringUtils.isNotEmpty(startEvent.getFormKey())) {
                formKeys.add(startEvent.getFormKey());
            }
        }

        // for all user tasks
        List<UserTask> userTasks = bpmnModel.getMainProcess().findFlowElementsOfType(UserTask.class, true);

        for (UserTask userTask : userTasks) {
            if (StringUtils.isNotEmpty(userTask.getFormKey())) {
                formKeys.add(userTask.getFormKey());
            }
        }

        for (String formKey : formKeys) {
            addFormDefinitionToCollection(formDefinitions, formKey, processDefinition);
        }

        return formDefinitions;
    }

    protected void addFormDefinitionToCollection(List<FormDefinition> formDefinitions, String formKey, ProcessDefinition processDefinition) {
        FormDefinitionQuery formDefinitionQuery = formRepositoryService.createFormDefinitionQuery().formDefinitionKey(formKey);
        Deployment deployment = CommandContextUtil.getDeploymentEntityManager().findById(processDefinition.getDeploymentId());
        if (deployment.getParentDeploymentId() != null) {
            List<FormDeployment> formDeployments = formRepositoryService.createDeploymentQuery().parentDeploymentId(deployment.getParentDeploymentId()).list();
            
            if (formDeployments != null && formDeployments.size() > 0) {
                formDefinitionQuery.deploymentId(formDeployments.get(0).getId());
            } else {
                formDefinitionQuery.latestVersion();
            }
            
        } else {
            formDefinitionQuery.latestVersion();
        }
        
        FormDefinition formDefinition = formDefinitionQuery.singleResult();
        
        if (formDefinition != null) {
            formDefinitions.add(formDefinition);
        }
    }
}
