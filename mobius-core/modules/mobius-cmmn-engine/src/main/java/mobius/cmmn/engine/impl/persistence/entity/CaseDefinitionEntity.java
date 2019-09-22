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

import mobius.cmmn.api.repository.CaseDefinition;
import mobius.common.engine.impl.persistence.entity.Entity;
import mobius.identitylink.service.impl.persistence.entity.IdentityLinkEntity;

import java.util.List;

public interface CaseDefinitionEntity extends Entity, CaseDefinition {
    
    void setName(String name);
    
    void setDescription(String description);
    
    void setCategory(String category);

    void setVersion(int version);
    
    void setKey(String key);
    
    void setResourceName(String resourceName);
    
    void setDeploymentId(String deploymentId);
    
    void setHasGraphicalNotation(boolean hasGraphicalNotation);
    
    void setDiagramResourceName(String diagramResourceName);
    
    void setHasStartFormKey(boolean hasStartFormKey);
    
    void setTenantId(String tenantId);

    List<IdentityLinkEntity> getIdentityLinks();
}
