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
package mobius.cmmn.engine.impl.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import mobius.common.engine.api.scope.ScopeTypes;
import mobius.entitylink.api.EntityLink;
import mobius.entitylink.api.EntityLinkService;
import mobius.entitylink.api.EntityLinkType;
import mobius.entitylink.api.HierarchyType;
import mobius.entitylink.service.impl.persistence.entity.EntityLinkEntity;

/**
 * @author Tijs Rademakers
 */
public class EntityLinkUtil {

    public static void copyExistingEntityLinks(String scopeId, String referenceScopeId, String referenceScopeType) {
        EntityLinkService entityLinkService = CommandContextUtil.getEntityLinkService();
        List<EntityLink> entityLinks = entityLinkService.findEntityLinksByReferenceScopeIdAndType(scopeId, ScopeTypes.CMMN, EntityLinkType.CHILD);
        List<String> parentIds = new ArrayList<>();
        for (EntityLink entityLink : entityLinks) {
            if (!parentIds.contains(entityLink.getScopeId())) {
                EntityLinkEntity newEntityLink = (EntityLinkEntity) entityLinkService.createEntityLink();
                newEntityLink.setLinkType(EntityLinkType.CHILD);
                newEntityLink.setScopeId(entityLink.getScopeId());
                newEntityLink.setScopeType(entityLink.getScopeType());
                newEntityLink.setScopeDefinitionId(entityLink.getScopeDefinitionId());
                newEntityLink.setReferenceScopeId(referenceScopeId);
                newEntityLink.setReferenceScopeType(referenceScopeType);
                if (HierarchyType.ROOT.equals(entityLink.getHierarchyType())) {
                    newEntityLink.setHierarchyType(entityLink.getHierarchyType());
                }
                entityLinkService.insertEntityLink(newEntityLink);

                CommandContextUtil.getCmmnHistoryManager().recordEntityLinkCreated(newEntityLink);

                parentIds.add(entityLink.getScopeId());
            }
        }
    }

    public static void createNewEntityLink(String scopeId, String referenceScopeId, String referenceScopeType) {
        EntityLinkService entityLinkService = CommandContextUtil.getEntityLinkService();

        // Check if existing links already have root, if not, current is root
        Optional<EntityLink> entityLinkWithRoot = entityLinkService
            .findEntityLinksByReferenceScopeIdAndType(scopeId, ScopeTypes.CMMN, EntityLinkType.CHILD)
            .stream()
            .filter(e -> HierarchyType.ROOT.equals(e.getHierarchyType()))
            .findFirst();

        EntityLinkEntity newEntityLink = (EntityLinkEntity) entityLinkService.createEntityLink();
        newEntityLink.setLinkType(EntityLinkType.CHILD);
        newEntityLink.setScopeId(scopeId);
        newEntityLink.setScopeType(ScopeTypes.CMMN);
        newEntityLink.setReferenceScopeId(referenceScopeId);
        newEntityLink.setReferenceScopeType(referenceScopeType);
        if (!entityLinkWithRoot.isPresent()) {
            newEntityLink.setHierarchyType(HierarchyType.ROOT);
        } else {
            newEntityLink.setHierarchyType(HierarchyType.PARENT);
        }
        entityLinkService.insertEntityLink(newEntityLink);

        CommandContextUtil.getCmmnHistoryManager().recordEntityLinkCreated(newEntityLink);
    }

}