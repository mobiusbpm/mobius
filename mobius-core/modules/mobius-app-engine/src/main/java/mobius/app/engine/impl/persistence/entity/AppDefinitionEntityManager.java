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
package mobius.app.engine.impl.persistence.entity;

import java.util.List;

import mobius.app.api.repository.AppDefinition;
import mobius.app.api.repository.AppDefinitionQuery;
import mobius.common.engine.impl.persistence.entity.EntityManager;

/**
 *
 */
public interface AppDefinitionEntityManager extends EntityManager<AppDefinitionEntity> {

    AppDefinitionEntity findLatestAppDefinitionByKey(String appDefinitionKey);

    AppDefinitionEntity findLatestAppDefinitionByKeyAndTenantId(String appDefinitionKey, String tenantId);
    
    AppDefinitionEntity findAppDefinitionByDeploymentAndKey(String deploymentId, String appDefinitionKey);

    AppDefinitionEntity findAppDefinitionByDeploymentAndKeyAndTenantId(String deploymentId, String appDefinitionKey, String tenantId);

    AppDefinition findAppDefinitionByKeyAndVersionAndTenantId(String appDefinitionKey, Integer caseDefinitionVersion, String tenantId);
    
    void deleteAppDefinitionAndRelatedData(String appDefinitionId);
    
    AppDefinitionQuery createAppDefinitionQuery();
    
    List<AppDefinition> findAppDefinitionsByQueryCriteria(AppDefinitionQuery appDefinitionQuery);

    long findAppDefinitionCountByQueryCriteria(AppDefinitionQuery appDefinitionQuery);

}