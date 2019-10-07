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
package mobius.dmn.engine.impl.cmd;

import java.io.Serializable;
import java.util.List;

import mobius.common.engine.api.FlowableIllegalArgumentException;
import mobius.common.engine.api.FlowableObjectNotFoundException;
import mobius.common.engine.impl.interceptor.Command;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.dmn.api.DmnDecisionTable;
import mobius.dmn.engine.impl.DecisionTableQueryImpl;
import mobius.dmn.engine.impl.persistence.entity.DmnDeploymentEntity;
import mobius.dmn.engine.impl.util.CommandContextUtil;

/**
 *
 */
public class SetDeploymentTenantIdCmd implements Command<Void>, Serializable {

    private static final long serialVersionUID = 1L;

    protected String deploymentId;
    protected String newTenantId;

    public SetDeploymentTenantIdCmd(String deploymentId, String newTenantId) {
        this.deploymentId = deploymentId;
        this.newTenantId = newTenantId;
    }

    @Override
    public Void execute(CommandContext commandContext) {
        if (deploymentId == null) {
            throw new FlowableIllegalArgumentException("deploymentId is null");
        }

        // Update all entities

        DmnDeploymentEntity deployment = CommandContextUtil.getDeploymentEntityManager(commandContext).findById(deploymentId);
        if (deployment == null) {
            throw new FlowableObjectNotFoundException("Could not find deployment with id " + deploymentId);
        }

        deployment.setTenantId(newTenantId);

        // Doing process instances, executions and tasks with direct SQL updates
        // (otherwise would not be performant)
        CommandContextUtil.getDecisionTableEntityManager(commandContext).updateDecisionTableTenantIdForDeployment(deploymentId, newTenantId);

        // Doing decision tables in memory, cause we need to clear the decision table cache
        List<DmnDecisionTable> decisionTables = new DecisionTableQueryImpl().deploymentId(deploymentId).list();
        for (DmnDecisionTable decisionTable : decisionTables) {
            CommandContextUtil.getDmnEngineConfiguration().getDecisionCache().remove(decisionTable.getId());
        }

        CommandContextUtil.getDeploymentEntityManager(commandContext).update(deployment);

        return null;

    }

}
