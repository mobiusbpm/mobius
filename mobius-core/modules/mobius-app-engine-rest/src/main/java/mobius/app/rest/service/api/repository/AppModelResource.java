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
package mobius.app.rest.service.api.repository;

import io.swagger.annotations.*;
import mobius.app.api.AppRepositoryService;
import mobius.app.api.repository.AppDefinition;
import mobius.app.rest.AppRestApiInterceptor;
import mobius.common.engine.api.FlowableObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Tijs Rademakers
 */
@RestController
@Api(tags = { "App Definitions" }, description = "Manage App Definitions", authorizations = { @Authorization(value = "basicAuth") })
public class AppModelResource {
    
    @Autowired
    protected AppRepositoryService appRepositoryService;
    
    @Autowired(required=false)
    protected AppRestApiInterceptor restApiInterceptor;

    @ApiOperation(value = "Get an App model", tags = { "App Definitions" }, nickname = "getAppModel")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Indicates the app model was found returned."),
            @ApiResponse(code = 404, message = "Indicates the app model was not found.")
    })
    @GetMapping(value = "/app-repository/app-definitions/{appDefinitionId}/model", produces = "application/json")
    public String getModelJsonResource(@ApiParam(name = "appDefinitionId") @PathVariable String appDefinitionId) {
        AppDefinition appDefinition = appRepositoryService.getAppDefinition(appDefinitionId);

        if (appDefinition == null) {
            throw new FlowableObjectNotFoundException("Could not find an app definition with id '" + appDefinitionId + "'.", AppDefinition.class);
        }
        
        if (restApiInterceptor != null) {
            restApiInterceptor.accessAppDefinitionInfoById(appDefinition);
        }
        
        String appModelJson = appRepositoryService.convertAppModelToJson(appDefinitionId);

        if (appModelJson == null) {
            throw new FlowableObjectNotFoundException("Could not find a app model json with id '" + appDefinitionId);
        }

        return appModelJson;
    }
}
