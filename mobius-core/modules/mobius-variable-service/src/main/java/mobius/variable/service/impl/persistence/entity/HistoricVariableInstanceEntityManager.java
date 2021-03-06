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
package mobius.variable.service.impl.persistence.entity;

import java.util.Date;
import java.util.List;
import java.util.Map;

import mobius.common.engine.impl.persistence.entity.EntityManager;
import mobius.variable.api.history.HistoricVariableInstance;
import mobius.variable.service.impl.HistoricVariableInstanceQueryImpl;

/**
 *
 */
public interface HistoricVariableInstanceEntityManager extends EntityManager<HistoricVariableInstanceEntity> {

    HistoricVariableInstanceEntity createAndInsert(VariableInstanceEntity variableInstance, Date createTime);

    void copyVariableValue(HistoricVariableInstanceEntity historicVariableInstance, VariableInstanceEntity variableInstance, Date updateTime);

    List<HistoricVariableInstance> findHistoricVariableInstancesByQueryCriteria(HistoricVariableInstanceQueryImpl historicProcessVariableQuery);

    HistoricVariableInstanceEntity findHistoricVariableInstanceByVariableInstanceId(String variableInstanceId);
    
    List<HistoricVariableInstanceEntity> findHistoricalVariableInstancesByScopeIdAndScopeType(String subScopeId, String scopeType);
    
    List<HistoricVariableInstanceEntity> findHistoricalVariableInstancesBySubScopeIdAndScopeType(String scopeId, String scopeType);

    long findHistoricVariableInstanceCountByQueryCriteria(HistoricVariableInstanceQueryImpl historicProcessVariableQuery);

    List<HistoricVariableInstance> findHistoricVariableInstancesByNativeQuery(Map<String, Object> parameterMap);

    long findHistoricVariableInstanceCountByNativeQuery(Map<String, Object> parameterMap);

    void deleteHistoricVariableInstancesByTaskId(String taskId);

    void deleteHistoricVariableInstanceByProcessInstanceId(String historicProcessInstanceId);

}