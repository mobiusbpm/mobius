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
import mobius.engine.history.HistoricActivityInstance;
import mobius.engine.impl.HistoricActivityInstanceQueryImpl;
import mobius.engine.impl.persistence.entity.HistoricActivityInstanceEntity;

/**
 *
 */
public interface HistoricActivityInstanceDataManager extends DataManager<HistoricActivityInstanceEntity> {

    List<HistoricActivityInstanceEntity> findUnfinishedHistoricActivityInstancesByExecutionAndActivityId(String executionId, String activityId);
    
    List<HistoricActivityInstanceEntity> findHistoricActivityInstancesByExecutionIdAndActivityId(String executionId, String activityId);

    List<HistoricActivityInstanceEntity> findUnfinishedHistoricActivityInstancesByProcessInstanceId(String processInstanceId);

    void deleteHistoricActivityInstancesByProcessInstanceId(String historicProcessInstanceId);

    long findHistoricActivityInstanceCountByQueryCriteria(HistoricActivityInstanceQueryImpl historicActivityInstanceQuery);

    List<HistoricActivityInstance> findHistoricActivityInstancesByQueryCriteria(HistoricActivityInstanceQueryImpl historicActivityInstanceQuery);

    List<HistoricActivityInstance> findHistoricActivityInstancesByNativeQuery(Map<String, Object> parameterMap);

    long findHistoricActivityInstanceCountByNativeQuery(Map<String, Object> parameterMap);

}
