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
package mobius.engine.impl.cmd;

import java.io.Serializable;

import mobius.common.engine.api.FlowableIllegalArgumentException;
import mobius.common.engine.impl.interceptor.Command;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.engine.impl.persistence.deploy.DeploymentManager;
import mobius.engine.impl.util.CommandContextUtil;

/**
 *
 */
public class GetAppResourceObjectCmd implements Command<Object>, Serializable {

    private static final long serialVersionUID = 1L;

    protected String deploymentId;

    public GetAppResourceObjectCmd(String deploymentId) {
        this.deploymentId = deploymentId;
    }

    @Override
    public Object execute(CommandContext commandContext) {
        if (deploymentId == null) {
            throw new FlowableIllegalArgumentException("deploymentId is null");
        }

        DeploymentManager deploymentManager = CommandContextUtil.getProcessEngineConfiguration().getDeploymentManager();
        return deploymentManager.getAppResourceObject(deploymentId);
    }
}