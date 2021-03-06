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
package mobius.entitylink.service.impl;

import java.util.List;

import mobius.common.engine.impl.service.CommonServiceImpl;
import mobius.entitylink.api.history.HistoricEntityLink;
import mobius.entitylink.api.history.HistoricEntityLinkService;
import mobius.entitylink.service.EntityLinkServiceConfiguration;
import mobius.entitylink.service.impl.persistence.entity.HistoricEntityLinkEntity;
import mobius.entitylink.service.impl.persistence.entity.HistoricEntityLinkEntityManager;

/**
 *
 */
public class HistoricEntityLinkServiceImpl extends CommonServiceImpl<EntityLinkServiceConfiguration> implements HistoricEntityLinkService {

    public HistoricEntityLinkServiceImpl(EntityLinkServiceConfiguration entityLinkServiceConfiguration) {
        super(entityLinkServiceConfiguration);
    }
    
    @Override
    public HistoricEntityLink getHistoricEntityLink(String id) {
        return getHistoricEntityLinkEntityManager().findById(id);
    }
    
    @Override
    public List<HistoricEntityLink> findHistoricEntityLinksByScopeIdAndScopeType(String scopeId, String scopeType, String linkType) {
        return getHistoricEntityLinkEntityManager().findHistoricEntityLinksByScopeIdAndScopeType(scopeId, scopeType, linkType);
    }

    @Override
    public List<HistoricEntityLink> findHistoricEntityLinksByReferenceScopeIdAndType(String referenceScopeId, String scopeType, String linkType) {
        return getHistoricEntityLinkEntityManager().findHistoricEntityLinksByReferenceScopeIdAndType(referenceScopeId, scopeType, linkType);
    }
    
    @Override
    public List<HistoricEntityLink> findHistoricEntityLinksByScopeDefinitionIdAndScopeType(String scopeDefinitionId, String scopeType, String linkType) {
        return getHistoricEntityLinkEntityManager().findHistoricEntityLinksByScopeDefinitionIdAndScopeType(scopeDefinitionId, scopeType, linkType);
    }
    
    @Override
    public HistoricEntityLink createHistoricEntityLink() {
        return getHistoricEntityLinkEntityManager().create();
    }
    
    @Override
    public void insertHistoricEntityLink(HistoricEntityLink entityLink, boolean fireCreateEvent) {
        getHistoricEntityLinkEntityManager().insert((HistoricEntityLinkEntity) entityLink, fireCreateEvent);
    }
    
    @Override
    public void deleteHistoricEntityLink(String id) {
        getHistoricEntityLinkEntityManager().delete(id);
    }
    
    @Override
    public void deleteHistoricEntityLink(HistoricEntityLink entityLink) {
        getHistoricEntityLinkEntityManager().delete((HistoricEntityLinkEntity) entityLink);
    }
    
    @Override
    public void deleteHistoricEntityLinksByScopeIdAndScopeType(String scopeId, String scopeType) {
        getHistoricEntityLinkEntityManager().deleteHistoricEntityLinksByScopeIdAndScopeType(scopeId, scopeType);
    }
    
    @Override
    public void deleteHistoricEntityLinksByScopeDefinitionIdAndScopeType(String scopeDefinitionId, String scopeType) {
        getHistoricEntityLinkEntityManager().deleteHistoricEntityLinksByScopeDefinitionIdAndScopeType(scopeDefinitionId, scopeType);
    }

    public HistoricEntityLinkEntityManager getHistoricEntityLinkEntityManager() {
        return configuration.getHistoricEntityLinkEntityManager();
    }
}
