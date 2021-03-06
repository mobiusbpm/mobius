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
package mobius.cmmn.engine.impl.cmd;

import java.io.Serializable;

import mobius.cmmn.engine.impl.persistence.entity.CmmnDeploymentEntity;
import mobius.cmmn.engine.impl.util.CommandContextUtil;
import mobius.common.engine.api.FlowableIllegalArgumentException;
import mobius.common.engine.api.FlowableObjectNotFoundException;
import mobius.common.engine.impl.interceptor.Command;
import mobius.common.engine.impl.interceptor.CommandContext;

/**
 *
 */
public class SetDeploymentParentDeploymentIdCmd implements Command<Void>, Serializable {

    private static final long serialVersionUID = 1L;

    protected String deploymentId;
    protected String newParentDeploymentId;

    public SetDeploymentParentDeploymentIdCmd(String deploymentId, String newParentDeploymentId) {
        this.deploymentId = deploymentId;
        this.newParentDeploymentId = newParentDeploymentId;
    }

    @Override
    public Void execute(CommandContext commandContext) {
        if (deploymentId == null) {
            throw new FlowableIllegalArgumentException("deploymentId is null");
        }

        // Update all entities

        CmmnDeploymentEntity deployment = CommandContextUtil.getCmmnDeploymentEntityManager(commandContext).findById(deploymentId);
        if (deployment == null) {
            throw new FlowableObjectNotFoundException("Could not find deployment with id " + deploymentId);
        }

        deployment.setParentDeploymentId(newParentDeploymentId);

        CommandContextUtil.getCmmnDeploymentEntityManager(commandContext).update(deployment);

        return null;

    }

}
