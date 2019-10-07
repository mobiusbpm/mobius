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
package mobius.entitylink.service.impl.persistence.entity.data.impl.cachematcher;

import java.util.Map;

import mobius.common.engine.impl.persistence.cache.CachedEntityMatcherAdapter;
import mobius.entitylink.service.impl.persistence.entity.EntityLinkEntity;

/**
 *
 */
public class EntityLinksByScopeIdAndTypeMatcher extends CachedEntityMatcherAdapter<EntityLinkEntity> {

    @Override
    public boolean isRetained(EntityLinkEntity entity, Object parameter) {
        @SuppressWarnings("unchecked")
        Map<String, String> parameterMap = (Map<String, String>) parameter;
        return entity.getScopeId() != null && entity.getScopeId().equals(parameterMap.get("scopeId")) &&
                        entity.getScopeType() != null && entity.getScopeType().equals(parameterMap.get("scopeType")) &&
                        entity.getLinkType() != null && entity.getLinkType().equals(parameterMap.get("linkType"));
    }

}