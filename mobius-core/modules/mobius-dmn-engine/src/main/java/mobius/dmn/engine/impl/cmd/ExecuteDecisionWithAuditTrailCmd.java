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

import java.util.Map;

import mobius.common.engine.api.FlowableException;
import mobius.common.engine.api.FlowableIllegalArgumentException;
import mobius.common.engine.impl.interceptor.Command;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.dmn.api.DecisionExecutionAuditContainer;
import mobius.dmn.api.DmnDecisionTable;
import mobius.dmn.engine.impl.ExecuteDecisionBuilderImpl;
import mobius.dmn.engine.impl.util.CommandContextUtil;
import mobius.dmn.model.Decision;

/**
 * @author Tijs Rademakers
 * @author Yvo Swillens
 */
public class ExecuteDecisionWithAuditTrailCmd extends AbstractExecuteDecisionCmd implements Command<DecisionExecutionAuditContainer> {
    
    private static final long serialVersionUID = 1L;

    public ExecuteDecisionWithAuditTrailCmd(ExecuteDecisionBuilderImpl decisionBuilder) {
        super(decisionBuilder);
    }
    
    public ExecuteDecisionWithAuditTrailCmd(String decisionKey, Map<String, Object> variables) {
        super(decisionKey, variables);
    }

    public ExecuteDecisionWithAuditTrailCmd(String decisionKey, String parentDeploymentId, Map<String, Object> variables) {
        this(decisionKey, variables);
        executeDecisionInfo.setParentDeploymentId(parentDeploymentId);
    }

    public ExecuteDecisionWithAuditTrailCmd(String decisionKey, String parentDeploymentId, Map<String, Object> variables, String tenantId) {
        this(decisionKey, parentDeploymentId, variables);
        executeDecisionInfo.setTenantId(tenantId);
    }

    @Override
    public DecisionExecutionAuditContainer execute(CommandContext commandContext) {
        if (executeDecisionInfo.getDecisionKey() == null) {
            throw new FlowableIllegalArgumentException("decisionKey is null");
        }

        Decision decision = null;
        try {
            DmnDecisionTable decisionTable = resolveDecisionTable();
            decision = resolveDecision(decisionTable);
            
        } catch (FlowableException e) {
            DecisionExecutionAuditContainer container = new DecisionExecutionAuditContainer();
            container.setFailed();
            container.setExceptionMessage(e.getMessage());
            return container;
        }

        return CommandContextUtil.getDmnEngineConfiguration().getRuleEngineExecutor().execute(decision, executeDecisionInfo);
    }

}
