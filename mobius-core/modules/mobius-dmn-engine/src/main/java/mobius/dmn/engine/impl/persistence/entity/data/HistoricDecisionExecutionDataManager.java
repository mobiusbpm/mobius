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
package mobius.dmn.engine.impl.persistence.entity.data;

import java.util.List;
import java.util.Map;

import mobius.common.engine.impl.persistence.entity.data.DataManager;
import mobius.dmn.api.DmnHistoricDecisionExecution;
import mobius.dmn.engine.impl.HistoricDecisionExecutionQueryImpl;
import mobius.dmn.engine.impl.persistence.entity.HistoricDecisionExecutionEntity;

/**
 *
 */
public interface HistoricDecisionExecutionDataManager extends DataManager<HistoricDecisionExecutionEntity> {
    
    void deleteHistoricDecisionExecutionsByDeploymentId(String deploymentId);

    List<DmnHistoricDecisionExecution> findHistoricDecisionExecutionsByQueryCriteria(HistoricDecisionExecutionQueryImpl decisionExecutionQuery);

    long findHistoricDecisionExecutionCountByQueryCriteria(HistoricDecisionExecutionQueryImpl decisionExecutionQuery);

    List<DmnHistoricDecisionExecution> findHistoricDecisionExecutionsByNativeQuery(Map<String, Object> parameterMap);

    long findHistoricDecisionExecutionCountByNativeQuery(Map<String, Object> parameterMap);
}
