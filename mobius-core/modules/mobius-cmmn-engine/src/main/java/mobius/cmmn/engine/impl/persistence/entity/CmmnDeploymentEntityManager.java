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

import mobius.cmmn.api.repository.CmmnDeployment;
import mobius.cmmn.api.repository.CmmnDeploymentQuery;
import mobius.common.engine.impl.persistence.entity.EntityManager;

import java.util.List;

/**
 *
 */
public interface CmmnDeploymentEntityManager extends EntityManager<CmmnDeploymentEntity> {

    CmmnDeploymentEntity findLatestDeploymentByName(String deploymentName);
    
    List<String> getDeploymentResourceNames(String deploymentId);

    void deleteDeploymentAndRelatedData(String deploymentId, boolean cascade);
    
    CmmnDeploymentQuery createDeploymentQuery();
    
    List<CmmnDeployment> findDeploymentsByQueryCriteria(CmmnDeploymentQuery deploymentQuery);
    
    long findDeploymentCountByQueryCriteria(CmmnDeploymentQuery deploymentQuery);

}