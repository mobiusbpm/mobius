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
package mobius.form.rest;

import mobius.form.api.FormDefinition;
import mobius.form.api.FormDefinitionQuery;
import mobius.form.api.FormDeployment;
import mobius.form.api.FormDeploymentBuilder;
import mobius.form.api.FormDeploymentQuery;
import mobius.form.api.FormInfo;
import mobius.form.api.FormInstance;
import mobius.form.api.FormInstanceQuery;
import mobius.form.rest.service.api.form.FormInstanceQueryRequest;
import mobius.form.rest.service.api.form.FormRequest;

public interface FormRestApiInterceptor {
    
    void accessFormInfoById(FormInfo formInfo, FormRequest formRequest);

    void accessFormInstanceById(FormInstance formInstance);
    
    void accessFormInstanceInfoWithQuery(FormInstanceQuery formInstanceQuery, FormInstanceQueryRequest request);
    
    void storeFormInstance(FormRequest formRequest);
    
    void accessFormDefinitionInfoById(FormDefinition formDefinition);
    
    void accessFormDefinitionInfoWithQuery(FormDefinitionQuery formDefinitionQuery);
    
    void accessDeploymentById(FormDeployment deployment);
    
    void accessDeploymentsWithQuery(FormDeploymentQuery deploymentQuery);
    
    void executeNewDeploymentForTenantId(String tenantId);

    void enhanceDeployment(FormDeploymentBuilder deploymentBuilder);
    
    void deleteDeployment(FormDeployment deployment);
    
    void accessFormManagementInfo();
}
