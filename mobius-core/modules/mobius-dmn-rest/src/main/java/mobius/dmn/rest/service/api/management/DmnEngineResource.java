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
package mobius.dmn.rest.service.api.management;

import io.swagger.annotations.*;
import mobius.common.engine.api.FlowableException;
import mobius.common.engine.impl.EngineInfo;
import mobius.dmn.engine.DmnEngine;
import mobius.dmn.engine.DmnEngines;
import mobius.dmn.rest.service.api.DmnRestApiInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Yvo Swillens
 */
@RestController
@Api(tags = { "Engine" }, description = "Manage DMN Engine", authorizations = { @Authorization(value = "basicAuth") })
public class DmnEngineResource {
    
    @Autowired(required=false)
    protected DmnRestApiInterceptor restApiInterceptor;

    @ApiOperation(value = "Get DMN engine info", tags = { "Engine" }, notes = "Returns a read-only view of the DMN engine that is used in this REST-service.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Indicates the engine info is returned."),
    })
    @GetMapping(value = "/dmn-management/engine", produces = "application/json")
    public DmnEngineInfoResponse getEngineInfo() {
        if (restApiInterceptor != null) {
            restApiInterceptor.accessDmnManagementInfo();
        }
        
        DmnEngineInfoResponse response = new DmnEngineInfoResponse();

        try {
            EngineInfo dmnEngineInfo = DmnEngines.getDmnEngineInfo(DmnEngines.getDefaultDmnEngine().getName());
            if (dmnEngineInfo != null) {
                response.setName(dmnEngineInfo.getName());
                response.setResourceUrl(dmnEngineInfo.getResourceUrl());
                response.setException(dmnEngineInfo.getException());
            } else {
                response.setName(DmnEngines.getDefaultDmnEngine().getName());
            }

        } catch (Exception e) {
            throw new FlowableException("Error retrieving DMN engine info", e);
        }

        response.setVersion(DmnEngine.VERSION);

        return response;
    }

}
