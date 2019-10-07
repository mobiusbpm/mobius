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
package mobius.task.service.impl.persistence.entity;

import java.util.Date;
import java.util.List;
import java.util.Map;

import mobius.common.engine.impl.db.HasRevision;
import mobius.common.engine.impl.persistence.entity.Entity;
import mobius.identitylink.service.impl.persistence.entity.IdentityLinkEntity;
import mobius.task.api.Task;
import mobius.task.service.delegate.DelegateTask;
import mobius.variable.api.delegate.VariableScope;
import mobius.variable.service.impl.persistence.entity.VariableInstanceEntity;

/**
 *
 * @author Tijs Rademakers
 */
public interface TaskEntity extends VariableScope, Task, DelegateTask, Entity, HasRevision {

    void setExecutionId(String executionId);

    @Override
    List<IdentityLinkEntity> getIdentityLinks();

    void setCreateTime(Date createTime);

    void setTaskDefinitionId(String taskDefinitionId);
    
    void setProcessDefinitionId(String processDefinitionId);

    void setEventName(String eventName);
    
    void setEventHandlerId(String eventHandlerId);

    void setProcessInstanceId(String processInstanceId);
    
    void setScopeId(String scopeId);
    
    void setSubScopeId(String subScopeId);
    
    void setScopeType(String scopeType);
    
    void setScopeDefinitionId(String scopeDefinitionId);

    int getSuspensionState();

    void setSuspensionState(int suspensionState);

    void setTaskDefinitionKey(String taskDefinitionKey);

    Map<String, VariableInstanceEntity> getVariableInstanceEntities();

    void forceUpdate();

    boolean isCanceled();

    void setCanceled(boolean isCanceled);

    void setClaimTime(Date claimTime);
    
    void setAssigneeValue(String assignee);
    
    void setOwnerValue(String owner);
}
