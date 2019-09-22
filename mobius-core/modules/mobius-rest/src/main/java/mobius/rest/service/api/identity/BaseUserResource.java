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

package mobius.rest.service.api.identity;

import mobius.rest.service.api.BpmnRestApiInterceptor;
import mobius.rest.service.api.RestResponseFactory;
import mobius.common.engine.api.FlowableObjectNotFoundException;
import mobius.engine.IdentityService;
import mobius.idm.api.User;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Frederik Heremans
 */
public class BaseUserResource {

    @Autowired
    protected RestResponseFactory restResponseFactory;

    @Autowired
    protected IdentityService identityService;
    
    @Autowired(required=false)
    protected BpmnRestApiInterceptor restApiInterceptor;

    protected User getUserFromRequest(String userId) {
        User user = identityService.createUserQuery().userId(userId).singleResult();

        if (user == null) {
            throw new FlowableObjectNotFoundException("Could not find a user with id '" + userId + "'.", User.class);
        }
        
        if (restApiInterceptor != null) {
            restApiInterceptor.accessUserInfoById(user);
        }
        
        return user;
    }
}
