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
package mobius.cmmn.engine.impl.persistence.entity;

import mobius.cmmn.api.runtime.CaseInstance;
import mobius.cmmn.api.runtime.CaseInstanceQuery;
import mobius.common.engine.impl.persistence.entity.EntityManager;

import java.util.List;

/**
 * @author Joram Barrez
 */
public interface CaseInstanceEntityManager extends EntityManager<CaseInstanceEntity> {

    CaseInstanceQuery createCaseInstanceQuery();

    List<CaseInstanceEntity> findCaseInstancesByCaseDefinitionId(String caseDefinitionId);

    List<CaseInstance> findByCriteria(CaseInstanceQuery query);

    List<CaseInstance> findWithVariablesByCriteria(CaseInstanceQuery query);

    long countByCriteria(CaseInstanceQuery query);

    void delete(String caseInstanceId, boolean cascade, String deleteReason);
    
    void updateLockTime(String caseInstanceId);

    void clearLockTime(String caseInstanceId);

    void updateCaseInstanceBusinessKey(CaseInstanceEntity caseInstanceEntity, String businessKey);
}
