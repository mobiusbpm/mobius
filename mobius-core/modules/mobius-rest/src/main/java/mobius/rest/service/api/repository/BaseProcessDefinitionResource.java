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

package mobius.rest.service.api.repository;

import mobius.common.engine.api.FlowableObjectNotFoundException;
import mobius.engine.RepositoryService;
import mobius.engine.repository.ProcessDefinition;
import mobius.rest.service.api.BpmnRestApiInterceptor;
import mobius.rest.service.api.RestResponseFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 */
public class BaseProcessDefinitionResource {

    @Autowired
    protected RestResponseFactory restResponseFactory;

    @Autowired
    protected RepositoryService repositoryService;
    
    @Autowired(required=false)
    protected BpmnRestApiInterceptor restApiInterceptor;

    /**
     * Returns the {@link ProcessDefinition} that is requested. Throws the right exceptions when bad request was made or definition was not found.
     */
    protected ProcessDefinition getProcessDefinitionFromRequest(String processDefinitionId) {
        ProcessDefinition processDefinition = repositoryService.getProcessDefinition(processDefinitionId);

        if (processDefinition == null) {
            throw new FlowableObjectNotFoundException("Could not find a process definition with id '" + processDefinitionId + "'.", ProcessDefinition.class);
        }
        
        if (restApiInterceptor != null) {
            restApiInterceptor.accessProcessDefinitionById(processDefinition);
        }
        
        return processDefinition;
    }
}
