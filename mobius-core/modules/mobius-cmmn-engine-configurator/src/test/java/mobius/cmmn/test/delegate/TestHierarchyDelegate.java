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
package mobius.cmmn.test.delegate;

import mobius.cmmn.api.delegate.DelegatePlanItemInstance;
import mobius.cmmn.api.delegate.PlanItemJavaDelegate;
import mobius.cmmn.engine.CmmnEngineConfiguration;
import mobius.cmmn.engine.impl.util.CommandContextUtil;
import mobius.common.engine.api.scope.ScopeTypes;
import mobius.common.engine.impl.interceptor.EngineConfigurationConstants;
import mobius.entitylink.api.EntityLink;
import mobius.entitylink.api.EntityLinkService;
import mobius.entitylink.api.EntityLinkType;
import mobius.entitylink.service.EntityLinkServiceConfiguration;

import java.util.List;

public class TestHierarchyDelegate implements PlanItemJavaDelegate {

    @Override
    public void execute(DelegatePlanItemInstance planItemInstance) {
        CmmnEngineConfiguration cmmnEngineConfiguration = CommandContextUtil.getCmmnEngineConfiguration();
        EntityLinkServiceConfiguration entityLinkServiceConfiguration = (EntityLinkServiceConfiguration) cmmnEngineConfiguration
                        .getServiceConfigurations().get(EngineConfigurationConstants.KEY_ENTITY_LINK_SERVICE_CONFIG);
        EntityLinkService entityLinkService = entityLinkServiceConfiguration.getEntityLinkService();
        List<EntityLink> entityLinks = entityLinkService.findEntityLinksByReferenceScopeIdAndType(planItemInstance.getCaseInstanceId(), 
                        ScopeTypes.CMMN, EntityLinkType.CHILD);
        planItemInstance.setVariable("linkCount", entityLinks.size());
    }

}
