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

package mobius.entitylink.service.impl.persistence.entity;

import java.util.List;

import mobius.common.engine.api.delegate.event.FlowableEngineEventType;
import mobius.common.engine.api.delegate.event.FlowableEventDispatcher;
import mobius.common.engine.impl.persistence.entity.data.DataManager;
import mobius.entitylink.api.EntityLink;
import mobius.entitylink.service.EntityLinkServiceConfiguration;
import mobius.entitylink.service.event.impl.FlowableEntityLinkEventBuilder;
import mobius.entitylink.service.impl.persistence.entity.data.EntityLinkDataManager;

/**
 *
 */
public class EntityLinkEntityManagerImpl extends AbstractEntityManager<EntityLinkEntity> implements EntityLinkEntityManager {

    protected EntityLinkDataManager entityLinkDataManager;

    public EntityLinkEntityManagerImpl(EntityLinkServiceConfiguration entityLinkServiceConfiguration, EntityLinkDataManager entityLinkDataManager) {
        super(entityLinkServiceConfiguration);
        this.entityLinkDataManager = entityLinkDataManager;
    }

    @Override
    protected DataManager<EntityLinkEntity> getDataManager() {
        return entityLinkDataManager;
    }
    
    @Override
    public EntityLinkEntity create() {
        EntityLinkEntity entityLinkEntity = super.create();
        entityLinkEntity.setCreateTime(entityLinkServiceConfiguration.getClock().getCurrentTime());
        return entityLinkEntity;
    }

    @Override
    public List<EntityLink> findEntityLinksByScopeIdAndType(String scopeId, String scopeType, String linkType) {
        return entityLinkDataManager.findEntityLinksByScopeIdAndType(scopeId, scopeType, linkType);
    }
    
    @Override
    public List<EntityLink> findEntityLinksByReferenceScopeIdAndType(String referenceScopeId, String referenceScopeType, String linkType) {
        return entityLinkDataManager.findEntityLinksByReferenceScopeIdAndType(referenceScopeId, referenceScopeType, linkType);
    }

    @Override
    public List<EntityLink> findEntityLinksByScopeDefinitionIdAndType(String scopeDefinitionId, String scopeType, String linkType) {
        return entityLinkDataManager.findEntityLinksByScopeDefinitionIdAndType(scopeDefinitionId, scopeType, linkType);
    }

    @Override
    public List<EntityLink> deleteScopeEntityLink(String scopeId, String scopeType, String linkType) {
        List<EntityLink> entityLinks = findEntityLinksByScopeIdAndType(scopeId, scopeType, linkType);

        for (EntityLink entityLink : entityLinks) {
            deleteEntityLink((EntityLinkEntity) entityLink);
        }

        return entityLinks;
    }

    @Override
    public List<EntityLink> deleteScopeDefinitionEntityLink(String scopeDefinitionId, String scopeType, String linkType) {
        List<EntityLink> entityLinks = findEntityLinksByScopeDefinitionIdAndType(scopeDefinitionId, scopeType, linkType);
        for (EntityLink entityLink : entityLinks) {
            deleteEntityLink((EntityLinkEntity) entityLink);
        }
        
        return entityLinks;
    }
    
    public void deleteEntityLink(EntityLinkEntity identityLink) {
        delete(identityLink, false);
        
        FlowableEventDispatcher eventDispatcher = getEventDispatcher();
        if (eventDispatcher != null && eventDispatcher.isEnabled()) {
            getEventDispatcher().dispatchEvent(FlowableEntityLinkEventBuilder.createEntityEvent(FlowableEngineEventType.ENTITY_DELETED, identityLink));
        }
    }

    @Override
    public void deleteEntityLinksByScopeIdAndScopeType(String scopeId, String scopeType) {
        entityLinkDataManager.deleteEntityLinksByScopeIdAndScopeType(scopeId, scopeType);
    }
    
    @Override
    public void deleteEntityLinksByScopeDefinitionIdAndScopeType(String scopeDefinitionId, String scopeType) {
        entityLinkDataManager.deleteEntityLinksByScopeDefinitionIdAndScopeType(scopeDefinitionId, scopeType);
    }

    public EntityLinkDataManager getEntityLinkDataManager() {
        return entityLinkDataManager;
    }

    public void setEntityLinkDataManager(EntityLinkDataManager entityLinkDataManager) {
        this.entityLinkDataManager = entityLinkDataManager;
    }

}
