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

import java.util.Date;
import java.util.List;
import java.util.Map;

import mobius.common.engine.impl.persistence.entity.EntityManager;
import mobius.engine.history.HistoricDetail;
import mobius.engine.impl.HistoricDetailQueryImpl;
import mobius.variable.service.impl.persistence.entity.VariableInstanceEntity;

/**
 *
 */
public interface HistoricDetailEntityManager extends EntityManager<HistoricDetailEntity> {

    HistoricFormPropertyEntity insertHistoricFormPropertyEntity(ExecutionEntity execution, String propertyId, String propertyValue, String taskId,
        Date createTime);

    HistoricDetailVariableInstanceUpdateEntity copyAndInsertHistoricDetailVariableInstanceUpdateEntity(VariableInstanceEntity variableInstance,
        Date createTime);

    long findHistoricDetailCountByQueryCriteria(HistoricDetailQueryImpl historicVariableUpdateQuery);

    List<HistoricDetail> findHistoricDetailsByQueryCriteria(HistoricDetailQueryImpl historicVariableUpdateQuery);

    void deleteHistoricDetailsByTaskId(String taskId);

    List<HistoricDetail> findHistoricDetailsByNativeQuery(Map<String, Object> parameterMap);

    long findHistoricDetailCountByNativeQuery(Map<String, Object> parameterMap);

    void deleteHistoricDetailsByProcessInstanceId(String historicProcessInstanceId);

}