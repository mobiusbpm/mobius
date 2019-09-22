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
package mobius.cmmn.engine.impl.persistence.entity.data;

import mobius.cmmn.api.history.HistoricCaseInstance;
import mobius.cmmn.engine.impl.history.HistoricCaseInstanceQueryImpl;
import mobius.cmmn.engine.impl.persistence.entity.HistoricCaseInstanceEntity;
import mobius.common.engine.impl.persistence.entity.data.DataManager;

import java.util.List;

/**
 * @author Joram Barrez
 */
public interface HistoricCaseInstanceDataManager extends DataManager<HistoricCaseInstanceEntity> {
    
    List<HistoricCaseInstanceEntity> findHistoricCaseInstancesByCaseDefinitionId(String caseDefinitionId);
    
    List<HistoricCaseInstance> findByCriteria(HistoricCaseInstanceQueryImpl query);
    
    long countByCriteria(HistoricCaseInstanceQueryImpl query);

    List<HistoricCaseInstance> findWithVariablesByQueryCriteria(HistoricCaseInstanceQueryImpl historicCaseInstanceQuery);

    void deleteByCaseDefinitionId(String caseDefinitionId);
    
}
