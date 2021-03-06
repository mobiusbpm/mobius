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
package mobius.entitylink.service.impl.persistence.entity.data;

import java.util.List;

import mobius.common.engine.impl.persistence.entity.data.DataManager;
import mobius.entitylink.api.EntityLink;
import mobius.entitylink.service.impl.persistence.entity.EntityLinkEntity;

/**
 *
 */
public interface EntityLinkDataManager extends DataManager<EntityLinkEntity> {

    List<EntityLink> findEntityLinksByScopeIdAndType(String scopeId, String scopeType, String linkType);
    
    List<EntityLink> findEntityLinksByReferenceScopeIdAndType(String referenceScopeId, String scopeType, String linkType);
    
    List<EntityLink> findEntityLinksByScopeDefinitionIdAndType(String scopeDefinitionId, String scopeType, String linkType);

    void deleteEntityLinksByScopeIdAndScopeType(String scopeId, String scopeType);
    
    void deleteEntityLinksByScopeDefinitionIdAndScopeType(String scopeDefinitionId, String scopeType);

}
