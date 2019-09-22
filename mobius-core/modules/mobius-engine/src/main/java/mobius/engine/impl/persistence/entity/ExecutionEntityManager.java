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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import mobius.bpmn.model.FlowElement;
import mobius.common.engine.impl.persistence.entity.EntityManager;
import mobius.engine.impl.ExecutionQueryImpl;
import mobius.engine.impl.ProcessInstanceQueryImpl;
import mobius.engine.repository.ProcessDefinition;
import mobius.engine.runtime.Execution;
import mobius.engine.runtime.ProcessInstance;

/**
 * @author Joram Barrez
 */
public interface ExecutionEntityManager extends EntityManager<ExecutionEntity> {

    ExecutionEntity createProcessInstanceExecution(ProcessDefinition processDefinition, String predefinedProcessInstanceId,
                    String businessKey, String processInstanceName, String callbackId, String callbackType, String tenantId, 
                    String initiatorVariableName, String startActivityId);

    ExecutionEntity createChildExecution(ExecutionEntity parentExecutionEntity);

    ExecutionEntity createSubprocessInstance(ProcessDefinition processDefinition, ExecutionEntity superExecutionEntity, 
                    String businessKey, String startActivityId);

    /**
     * Finds the {@link ExecutionEntity} for the given root process instance id. All children will have been fetched and initialized.
     */
    ExecutionEntity findByRootProcessInstanceId(String rootProcessInstanceId);

    ExecutionEntity findSubProcessInstanceBySuperExecutionId(String superExecutionId);

    List<ExecutionEntity> findChildExecutionsByParentExecutionId(String parentExecutionId);

    List<ExecutionEntity> findChildExecutionsByProcessInstanceId(String processInstanceId);

    List<ExecutionEntity> findExecutionsByParentExecutionAndActivityIds(String parentExecutionId, Collection<String> activityIds);

    long findExecutionCountByQueryCriteria(ExecutionQueryImpl executionQuery);

    List<ExecutionEntity> findExecutionsByQueryCriteria(ExecutionQueryImpl executionQuery);

    long findProcessInstanceCountByQueryCriteria(ProcessInstanceQueryImpl executionQuery);

    List<ProcessInstance> findProcessInstanceByQueryCriteria(ProcessInstanceQueryImpl executionQuery);

    List<ProcessInstance> findProcessInstanceAndVariablesByQueryCriteria(ProcessInstanceQueryImpl executionQuery);

    Collection<ExecutionEntity> findInactiveExecutionsByProcessInstanceId(String processInstanceId);

    Collection<ExecutionEntity> findInactiveExecutionsByActivityIdAndProcessInstanceId(String activityId, String processInstanceId);

    List<Execution> findExecutionsByNativeQuery(Map<String, Object> parameterMap);

    List<ProcessInstance> findProcessInstanceByNativeQuery(Map<String, Object> parameterMap);

    long findExecutionCountByNativeQuery(Map<String, Object> parameterMap);

    /**
     * Returns all child executions of a given {@link ExecutionEntity}. 
     * In the list, child executions will be behind parent executions.
     * Children include subprocessinstances and its children.
     */
    List<ExecutionEntity> collectChildren(ExecutionEntity executionEntity);

    ExecutionEntity findFirstScope(ExecutionEntity executionEntity);

    ExecutionEntity findFirstMultiInstanceRoot(ExecutionEntity executionEntity);

    void updateExecutionTenantIdForDeployment(String deploymentId, String newTenantId);

    String updateProcessInstanceBusinessKey(ExecutionEntity executionEntity, String businessKey);

    void deleteProcessInstancesByProcessDefinition(String processDefinitionId, String deleteReason, boolean cascade);

    void deleteProcessInstance(String processInstanceId, String deleteReason, boolean cascade);

    void deleteProcessInstanceExecutionEntity(String processInstanceId, String currentFlowElementId,
            String deleteReason, boolean cascade, boolean cancel, boolean fireEvents);

    void deleteChildExecutions(ExecutionEntity executionEntity, Collection<String> executionIdsNotToDelete, 
            Collection<String> executionIdsNotToSendCancelledEventsFor, String deleteReason, boolean cancel, FlowElement cancelActivity);
    
    void deleteChildExecutions(ExecutionEntity executionEntity, String deleteReason, boolean cancel);

    void deleteExecutionAndRelatedData(ExecutionEntity executionEntity, String deleteReason, boolean deleteHistory, boolean cancel, FlowElement cancelActivity);
    
    void deleteExecutionAndRelatedData(ExecutionEntity executionEntity, String deleteReason, boolean deleteHistory);
    
    void deleteRelatedDataForExecution(ExecutionEntity executionEntity, String deleteReason);

    void updateProcessInstanceLockTime(String processInstanceId);

    void clearProcessInstanceLockTime(String processInstanceId);

}