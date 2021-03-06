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

package mobius.engine.impl.persistence.entity;

import static java.util.Comparator.comparing;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

import mobius.common.engine.impl.db.HasRevision;
import mobius.common.engine.impl.persistence.entity.AlwaysUpdatedPersistentObject;
import mobius.common.engine.impl.persistence.entity.Entity;
import mobius.engine.delegate.DelegateExecution;
import mobius.engine.runtime.Execution;
import mobius.engine.runtime.ProcessInstance;
import mobius.eventsubscription.service.impl.persistence.entity.EventSubscriptionEntity;
import mobius.identitylink.service.impl.persistence.entity.IdentityLinkEntity;
import mobius.job.service.impl.persistence.entity.JobEntity;
import mobius.job.service.impl.persistence.entity.TimerJobEntity;
import mobius.task.service.impl.persistence.entity.TaskEntity;

/**
 *
 * @author Daniel Meyer
 * @author Falko Menge
 * @author Saeid Mirzaei
 *
 */

public interface ExecutionEntity extends DelegateExecution, Execution, ProcessInstance, Entity, AlwaysUpdatedPersistentObject, HasRevision {

    Comparator<ExecutionEntity> EXECUTION_ENTITY_START_TIME_ASC_COMPARATOR = comparing(ProcessInstance::getStartTime);

    void setBusinessKey(String businessKey);

    void setProcessDefinitionId(String processDefinitionId);

    void setProcessDefinitionKey(String processDefinitionKey);

    void setProcessDefinitionName(String processDefinitionName);

    void setProcessDefinitionVersion(Integer processDefinitionVersion);

    void setDeploymentId(String deploymentId);

    ExecutionEntity getProcessInstance();

    void setProcessInstance(ExecutionEntity processInstance);

    @Override
    ExecutionEntity getParent();

    void setParent(ExecutionEntity parent);

    ExecutionEntity getSuperExecution();

    void setSuperExecution(ExecutionEntity superExecution);

    ExecutionEntity getSubProcessInstance();

    void setSubProcessInstance(ExecutionEntity subProcessInstance);

    void setRootProcessInstanceId(String rootProcessInstanceId);

    ExecutionEntity getRootProcessInstance();

    void setRootProcessInstance(ExecutionEntity rootProcessInstance);

    @Override
    List<? extends ExecutionEntity> getExecutions();

    void addChildExecution(ExecutionEntity executionEntity);

    List<TaskEntity> getTasks();

    List<EventSubscriptionEntity> getEventSubscriptions();

    List<JobEntity> getJobs();

    List<TimerJobEntity> getTimerJobs();

    List<IdentityLinkEntity> getIdentityLinks();

    void setProcessInstanceId(String processInstanceId);

    void setParentId(String parentId);

    void setEnded(boolean isEnded);

    String getDeleteReason();

    void setDeleteReason(String deleteReason);

    int getSuspensionState();

    void setSuspensionState(int suspensionState);

    boolean isEventScope();

    void setEventScope(boolean isEventScope);

    void setName(String name);

    void setDescription(String description);

    void setLocalizedName(String localizedName);

    void setLocalizedDescription(String localizedDescription);

    void setTenantId(String tenantId);

    Date getLockTime();

    void setLockTime(Date lockTime);

    void forceUpdate();
    
    String getStartActivityId();

    void setStartActivityId(String startActivityId);

    void setStartUserId(String startUserId);

    void setStartTime(Date startTime);
    
    void setCallbackId(String callbackId);
    
    void setCallbackType(String callbackType);
    
    void setVariable(String variableName, Object value, ExecutionEntity sourceExecution, boolean fetchAllVariables);
    
    Object setVariableLocal(String variableName, Object value, ExecutionEntity sourceExecution, boolean fetchAllVariables);

}
