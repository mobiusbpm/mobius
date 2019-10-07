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

import java.util.Map;

import mobius.common.engine.api.FlowableException;
import mobius.common.engine.api.FlowableIllegalArgumentException;
import mobius.common.engine.api.FlowableObjectNotFoundException;
import mobius.common.engine.impl.interceptor.Command;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.engine.impl.persistence.deploy.DeploymentManager;
import mobius.engine.impl.runtime.ProcessInstanceBuilderImpl;
import mobius.engine.impl.util.CommandContextUtil;
import mobius.engine.impl.util.ProcessInstanceHelper;
import mobius.engine.repository.ProcessDefinition;
import mobius.engine.runtime.ProcessInstance;
import mobius.eventsubscription.service.impl.persistence.entity.MessageEventSubscriptionEntity;

/**
 *
 *
 */
public class StartProcessInstanceByMessageCmd implements Command<ProcessInstance> {

    protected String messageName;
    protected String businessKey;
    protected Map<String, Object> processVariables;
    protected Map<String, Object> transientVariables;
    protected String callbackId;
    protected String callbackType;
    protected String tenantId;

    public StartProcessInstanceByMessageCmd(String messageName, String businessKey, Map<String, Object> processVariables, String tenantId) {
        this.messageName = messageName;
        this.businessKey = businessKey;
        this.processVariables = processVariables;
        this.tenantId = tenantId;
    }

    public StartProcessInstanceByMessageCmd(ProcessInstanceBuilderImpl processInstanceBuilder) {
        this(processInstanceBuilder.getMessageName(),
             processInstanceBuilder.getBusinessKey(),
             processInstanceBuilder.getVariables(),
             processInstanceBuilder.getTenantId());
        this.transientVariables = processInstanceBuilder.getTransientVariables();
        this.callbackId = processInstanceBuilder.getCallbackId();
        this.callbackType = processInstanceBuilder.getCallbackType();
    }

    @Override
    public ProcessInstance execute(CommandContext commandContext) {

        if (messageName == null) {
            throw new FlowableIllegalArgumentException("Cannot start process instance by message: message name is null");
        }

        MessageEventSubscriptionEntity messageEventSubscription = CommandContextUtil.getEventSubscriptionService(commandContext).findMessageStartEventSubscriptionByName(messageName, tenantId);

        if (messageEventSubscription == null) {
            throw new FlowableObjectNotFoundException("Cannot start process instance by message: no subscription to message with name '" + messageName + "' found.", MessageEventSubscriptionEntity.class);
        }

        String processDefinitionId = messageEventSubscription.getConfiguration();
        if (processDefinitionId == null) {
            throw new FlowableException("Cannot start process instance by message: subscription to message with name '" + messageName + "' is not a message start event.");
        }

        DeploymentManager deploymentCache = CommandContextUtil.getProcessEngineConfiguration(commandContext).getDeploymentManager();

        ProcessDefinition processDefinition = deploymentCache.findDeployedProcessDefinitionById(processDefinitionId);
        if (processDefinition == null) {
            throw new FlowableObjectNotFoundException("No process definition found for id '" + processDefinitionId + "'", ProcessDefinition.class);
        }

        ProcessInstanceHelper processInstanceHelper = CommandContextUtil.getProcessEngineConfiguration(commandContext).getProcessInstanceHelper();
        ProcessInstance processInstance = processInstanceHelper.createAndStartProcessInstanceByMessage(processDefinition, 
                                                                                                       messageName, 
                                                                                                       businessKey, 
                                                                                                       processVariables, 
                                                                                                       transientVariables,
                                                                                                       callbackId,
                                                                                                       callbackType);

        return processInstance;
    }

}
