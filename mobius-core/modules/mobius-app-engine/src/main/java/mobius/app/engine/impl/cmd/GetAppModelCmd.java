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
package mobius.app.engine.impl.cmd;

import mobius.app.engine.impl.deployer.AppDeploymentManager;
import mobius.app.engine.impl.util.CommandContextUtil;
import mobius.app.api.repository.AppDefinition;
import mobius.app.api.repository.AppModel;
import mobius.common.engine.api.FlowableIllegalArgumentException;
import mobius.common.engine.impl.interceptor.Command;
import mobius.common.engine.impl.interceptor.CommandContext;

/**
 *
 */
public class GetAppModelCmd implements Command<AppModel> {

    protected String appDefinitionId;

    public GetAppModelCmd(String appDefinitionId) {
        this.appDefinitionId = appDefinitionId;
    }

    @Override
    public AppModel execute(CommandContext commandContext) {
        if (appDefinitionId == null) {
            throw new FlowableIllegalArgumentException("appDefinitionId is null");
        }
        
        AppDeploymentManager deploymentManager = CommandContextUtil.getAppEngineConfiguration(commandContext).getDeploymentManager();
        AppDefinition appDefinition = deploymentManager.findDeployedAppDefinitionById(appDefinitionId);
        if (appDefinition != null) {
            return deploymentManager.resolveAppDefinition(appDefinition).getAppModel();
        }
        return null;
    }
}