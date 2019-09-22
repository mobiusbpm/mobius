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

import mobius.common.engine.api.FlowableIllegalArgumentException;
import mobius.common.engine.api.FlowableObjectNotFoundException;
import mobius.common.engine.api.delegate.event.FlowableEngineEventType;
import mobius.common.engine.api.delegate.event.FlowableEventDispatcher;
import mobius.common.engine.impl.interceptor.Command;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.common.engine.impl.persistence.deploy.DeploymentCache;
import mobius.engine.compatibility.Flowable5CompatibilityHandler;
import mobius.engine.delegate.event.impl.FlowableEventBuilder;
import mobius.engine.impl.persistence.deploy.ProcessDefinitionCacheEntry;
import mobius.engine.impl.persistence.entity.ProcessDefinitionEntity;
import mobius.engine.impl.util.CommandContextUtil;
import mobius.engine.impl.util.Flowable5Util;
import mobius.engine.repository.ProcessDefinition;

/**
 * @author Joram Barrez
 */
public class SetProcessDefinitionCategoryCmd implements Command<Void> {

    protected String processDefinitionId;
    protected String category;

    public SetProcessDefinitionCategoryCmd(String processDefinitionId, String category) {
        this.processDefinitionId = processDefinitionId;
        this.category = category;
    }

    @Override
    public Void execute(CommandContext commandContext) {

        if (processDefinitionId == null) {
            throw new FlowableIllegalArgumentException("Process definition id is null");
        }

        ProcessDefinitionEntity processDefinition = CommandContextUtil.getProcessDefinitionEntityManager(commandContext).findById(processDefinitionId);

        if (processDefinition == null) {
            throw new FlowableObjectNotFoundException("No process definition found for id = '" + processDefinitionId + "'", ProcessDefinition.class);
        }

        if (Flowable5Util.isFlowable5ProcessDefinition(processDefinition, commandContext)) {
            Flowable5CompatibilityHandler compatibilityHandler = Flowable5Util.getFlowable5CompatibilityHandler();
            compatibilityHandler.setProcessDefinitionCategory(processDefinitionId, category);
            return null;
        }

        // Update category
        processDefinition.setCategory(category);

        // Remove process definition from cache, it will be refetched later
        DeploymentCache<ProcessDefinitionCacheEntry> processDefinitionCache = CommandContextUtil.getProcessEngineConfiguration(commandContext).getProcessDefinitionCache();
        if (processDefinitionCache != null) {
            processDefinitionCache.remove(processDefinitionId);
        }

        FlowableEventDispatcher eventDispatcher = CommandContextUtil.getEventDispatcher();
        if (eventDispatcher != null && eventDispatcher.isEnabled()) {
            eventDispatcher
                .dispatchEvent(FlowableEventBuilder.createEntityEvent(FlowableEngineEventType.ENTITY_UPDATED, processDefinition));
        }

        return null;
    }

    public String getProcessDefinitionId() {
        return processDefinitionId;
    }

    public void setProcessDefinitionId(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

}
