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
package mobius.dmn.engine.impl.persistence.entity;

import java.util.List;
import java.util.Map;

import mobius.common.engine.impl.persistence.entity.EntityManager;
import mobius.dmn.api.DmnDeployment;
import mobius.dmn.engine.impl.DmnDeploymentQueryImpl;

/**
 *
 */
public interface DmnDeploymentEntityManager extends EntityManager<DmnDeploymentEntity> {

    List<DmnDeployment> findDeploymentsByQueryCriteria(DmnDeploymentQueryImpl deploymentQuery);

    List<String> getDeploymentResourceNames(String deploymentId);

    List<DmnDeployment> findDeploymentsByNativeQuery(Map<String, Object> parameterMap);

    long findDeploymentCountByNativeQuery(Map<String, Object> parameterMap);

    long findDeploymentCountByQueryCriteria(DmnDeploymentQueryImpl deploymentQuery);

    void deleteDeployment(String deploymentId);

}