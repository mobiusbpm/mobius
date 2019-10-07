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

package mobius.cmmn.rest.api.repository;

import mobius.cmmn.api.CmmnRepositoryService;
import mobius.cmmn.api.repository.CmmnDeployment;
import mobius.cmmn.rest.api.CmmnRestApiInterceptor;
import mobius.common.engine.api.FlowableObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 */
public class BaseDeploymentResource {

    @Autowired
    protected CmmnRepositoryService repositoryService;
    
    @Autowired(required=false)
    protected CmmnRestApiInterceptor restApiInterceptor;

    protected CmmnDeployment getCmmnDeployment(String deploymentId) {
        CmmnDeployment deployment = repositoryService.createDeploymentQuery().deploymentId(deploymentId).singleResult();

        if (deployment == null) {
            throw new FlowableObjectNotFoundException("Could not find a deployment with id '" + deploymentId + "'.", CmmnDeployment.class);
        }
        
        if (restApiInterceptor != null) {
            restApiInterceptor.accessDeploymentById(deployment);
        }
        
        return deployment;
    }
}
