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
package mobius.engine.impl.persistence.entity.data;

import java.util.List;
import java.util.Map;

import mobius.common.engine.impl.persistence.entity.data.DataManager;
import mobius.engine.history.HistoricDetail;
import mobius.engine.impl.HistoricDetailQueryImpl;
import mobius.engine.impl.persistence.entity.HistoricDetailAssignmentEntity;
import mobius.engine.impl.persistence.entity.HistoricDetailEntity;
import mobius.engine.impl.persistence.entity.HistoricDetailVariableInstanceUpdateEntity;
import mobius.engine.impl.persistence.entity.HistoricFormPropertyEntity;

/**
 * @author Joram Barrez
 */
public interface HistoricDetailDataManager extends DataManager<HistoricDetailEntity> {

    HistoricDetailAssignmentEntity createHistoricDetailAssignment();

    HistoricDetailVariableInstanceUpdateEntity createHistoricDetailVariableInstanceUpdate();

    HistoricFormPropertyEntity createHistoricFormProperty();

    List<HistoricDetailEntity> findHistoricDetailsByProcessInstanceId(String processInstanceId);

    List<HistoricDetailEntity> findHistoricDetailsByTaskId(String taskId);

    long findHistoricDetailCountByQueryCriteria(HistoricDetailQueryImpl historicVariableUpdateQuery);

    List<HistoricDetail> findHistoricDetailsByQueryCriteria(HistoricDetailQueryImpl historicVariableUpdateQuery);

    List<HistoricDetail> findHistoricDetailsByNativeQuery(Map<String, Object> parameterMap);

    long findHistoricDetailCountByNativeQuery(Map<String, Object> parameterMap);

}
