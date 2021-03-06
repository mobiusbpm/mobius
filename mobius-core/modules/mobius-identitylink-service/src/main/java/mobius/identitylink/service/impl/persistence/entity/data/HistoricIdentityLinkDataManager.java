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
package mobius.identitylink.service.impl.persistence.entity.data;

import java.util.List;

import mobius.common.engine.impl.persistence.entity.data.DataManager;
import mobius.identitylink.service.impl.persistence.entity.HistoricIdentityLinkEntity;

/**
 *
 */
public interface HistoricIdentityLinkDataManager extends DataManager<HistoricIdentityLinkEntity> {

    List<HistoricIdentityLinkEntity> findHistoricIdentityLinksByTaskId(String taskId);

    List<HistoricIdentityLinkEntity> findHistoricIdentityLinksByProcessInstanceId(String processInstanceId);

    List<HistoricIdentityLinkEntity> findHistoricIdentityLinksByScopeIdAndScopeType(String scopeId, String scopeType);
    
    void deleteHistoricIdentityLinksByScopeIdAndType(String scopeId, String scopeType);
    
    void deleteHistoricIdentityLinksByScopeDefinitionIdAndType(String scopeDefinitionId, String scopeType);
}
