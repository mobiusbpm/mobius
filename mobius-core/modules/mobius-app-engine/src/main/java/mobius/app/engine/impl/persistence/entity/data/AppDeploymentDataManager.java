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
package mobius.app.engine.impl.persistence.entity.data;

import java.util.List;

import mobius.app.engine.impl.repository.AppDeploymentQueryImpl;
import mobius.app.api.repository.AppDeployment;
import mobius.app.engine.impl.persistence.entity.AppDeploymentEntity;
import mobius.common.engine.impl.persistence.entity.data.DataManager;

/**
 *
 */
public interface AppDeploymentDataManager extends DataManager<AppDeploymentEntity> {

    AppDeploymentEntity findLatestDeploymentByName(String deploymentName);

    List<String> getDeploymentResourceNames(String deploymentId);
    
    long findDeploymentCountByQueryCriteria(AppDeploymentQueryImpl deploymentQuery);

    List<AppDeployment> findDeploymentsByQueryCriteria(AppDeploymentQueryImpl deploymentQuery);

}
