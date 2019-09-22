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
package mobius.app.rest.service.api.management;

import io.swagger.annotations.*;
import mobius.app.engine.AppEngine;
import mobius.app.engine.AppEngines;
import mobius.app.rest.AppRestApiInterceptor;
import mobius.common.engine.api.FlowableException;
import mobius.common.engine.impl.EngineInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;



/**
 * @author Tijs Rademakers
 */
@RestController
@Api(tags = { "Engine" }, description = "Manage App Engine", authorizations = { @Authorization(value = "basicAuth") })
public class AppEngineResource {
    
    @Autowired(required=false)
    protected AppRestApiInterceptor restApiInterceptor;

    @ApiOperation(value = "Get app engine info", tags = { "Engine" }, notes = "Returns a read-only view of the engine that is used in this REST-service.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Indicates the engine info is returned."),
    })
    @GetMapping(value = "/app-management/engine", produces = "application/json")
    public AppEngineInfoResponse getEngineInfo() {
        if (restApiInterceptor != null) {
            restApiInterceptor.accessAppManagementInfo();
        }
        
        AppEngineInfoResponse response = new AppEngineInfoResponse();

        try {
            AppEngine appEngine = AppEngines.getDefaultAppEngine();
            EngineInfo appEngineInfo = AppEngines.getAppEngineInfo(appEngine.getName());

            if (appEngineInfo != null) {
                response.setName(appEngineInfo.getName());
                response.setResourceUrl(appEngineInfo.getResourceUrl());
                response.setException(appEngineInfo.getException());
            } else {
                response.setName(appEngine.getName());
            }
            
        } catch (Exception e) {
            throw new FlowableException("Error retrieving app engine info", e);
        }

        response.setVersion(AppEngine.VERSION);

        return response;
    }
}
