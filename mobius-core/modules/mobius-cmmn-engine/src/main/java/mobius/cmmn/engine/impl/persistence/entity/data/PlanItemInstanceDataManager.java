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

import mobius.cmmn.api.runtime.PlanItemInstance;
import mobius.cmmn.engine.impl.persistence.entity.PlanItemInstanceEntity;
import mobius.cmmn.engine.impl.runtime.PlanItemInstanceQueryImpl;
import mobius.common.engine.impl.persistence.entity.data.DataManager;

import java.util.List;

/**
 *
 */
public interface PlanItemInstanceDataManager extends DataManager<PlanItemInstanceEntity> {
    
    List<PlanItemInstanceEntity> findByCaseInstanceId(String caseInstanceId);

    List<PlanItemInstanceEntity> findByCaseInstanceIdAndPlanItemId(String caseInstanceId, String planitemId);
    
    List<PlanItemInstance> findByCriteria(PlanItemInstanceQueryImpl planItemInstanceQuery);
    
    long countByCriteria(PlanItemInstanceQueryImpl planItemInstanceQuery);
    
    void deleteByCaseDefinitionId(String caseDefinitionId);
    
    void deleteByStageInstanceId(String stageInstanceId);
    
    void deleteByCaseInstanceId(String caseInstanceId);
    
}
