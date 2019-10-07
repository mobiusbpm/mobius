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
package mobius.entitylink.service.impl.persistence.entity.data.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mobius.common.engine.impl.db.AbstractDataManager;
import mobius.common.engine.impl.persistence.cache.CachedEntityMatcher;
import mobius.entitylink.api.EntityLink;
import mobius.entitylink.service.impl.persistence.entity.EntityLinkEntity;
import mobius.entitylink.service.impl.persistence.entity.EntityLinkEntityImpl;
import mobius.entitylink.service.impl.persistence.entity.data.EntityLinkDataManager;
import mobius.entitylink.service.impl.persistence.entity.data.impl.cachematcher.EntityLinksByReferenceScopeIdAndTypeMatcher;
import mobius.entitylink.service.impl.persistence.entity.data.impl.cachematcher.EntityLinksByScopeIdAndTypeMatcher;

/**
 *
 */
public class MybatisEntityLinkDataManager extends AbstractDataManager<EntityLinkEntity> implements EntityLinkDataManager {

    protected CachedEntityMatcher<EntityLinkEntity> entityLinksByScopeIdAndTypeMatcher = new EntityLinksByScopeIdAndTypeMatcher();
    protected CachedEntityMatcher<EntityLinkEntity> entityLinksByReferenceScopeIdAndTypeMatcher = new EntityLinksByReferenceScopeIdAndTypeMatcher();

    @Override
    public Class<? extends EntityLinkEntity> getManagedEntityClass() {
        return EntityLinkEntityImpl.class;
    }

    @Override
    public EntityLinkEntity create() {
        return new EntityLinkEntityImpl();
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<EntityLink> findEntityLinksByScopeIdAndType(String scopeId, String scopeType, String linkType) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("scopeId", scopeId);
        parameters.put("scopeType", scopeType);
        parameters.put("linkType", linkType);
        return (List) getList("selectEntityLinksByScopeIdAndType", parameters, entityLinksByScopeIdAndTypeMatcher, true);
    }
    
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<EntityLink> findEntityLinksByReferenceScopeIdAndType(String referenceScopeId, String referenceScopeType, String linkType) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("referenceScopeId", referenceScopeId);
        parameters.put("referenceScopeType", referenceScopeType);
        parameters.put("linkType", linkType);
        return (List) getList("selectEntityLinksByReferenceScopeIdAndType", parameters, entityLinksByReferenceScopeIdAndTypeMatcher, true);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<EntityLink> findEntityLinksByScopeDefinitionIdAndType(String scopeDefinitionId, String scopeType, String linkType) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("scopeDefinitionId", scopeDefinitionId);
        parameters.put("scopeType", scopeType);
        parameters.put("linkType", linkType);
        return getDbSqlSession().selectList("selectEntityLinksByScopeDefinitionAndType", parameters);
    }

    @Override
    public void deleteEntityLinksByScopeIdAndScopeType(String scopeId, String scopeType) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("scopeId", scopeId);
        parameters.put("scopeType", scopeType);
        bulkDelete("deleteEntityLinksByScopeIdAndScopeType", entityLinksByScopeIdAndTypeMatcher, parameters);
    }
    
    @Override
    public void deleteEntityLinksByScopeDefinitionIdAndScopeType(String scopeDefinitionId, String scopeType) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("scopeDefinitionId", scopeDefinitionId);
        parameters.put("scopeType", scopeType);
        getDbSqlSession().delete("deleteEntityLinksByScopeDefinitionIdAndScopeType", parameters, EntityLinkEntityImpl.class);
    }

}
