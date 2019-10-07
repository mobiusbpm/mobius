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
import mobius.dmn.engine.DmnEngineConfiguration;
import mobius.dmn.engine.impl.ExecuteDecisionBuilderImpl;
import mobius.dmn.engine.impl.util.CommandContextUtil;
import mobius.dmn.model.Decision;
/**
 *
 * @author Yvo Swillens
 */
public class ExecuteDecisionSingleResultCmd extends AbstractExecuteDecisionCmd implements Command<Map<String, Object>> {
    
    private static final long serialVersionUID = 1L;

    public ExecuteDecisionSingleResultCmd(ExecuteDecisionBuilderImpl decisionBuilder) {
        super(decisionBuilder);
    }
    
    public ExecuteDecisionSingleResultCmd(String decisionKey, Map<String, Object> variables) {
        super(decisionKey, variables);
    }

    public ExecuteDecisionSingleResultCmd(String decisionKey, String parentDeploymentId, Map<String, Object> variables) {
        this(decisionKey, variables);
        executeDecisionInfo.setParentDeploymentId(parentDeploymentId);
    }

    public ExecuteDecisionSingleResultCmd(String decisionKey, String parentDeploymentId, Map<String, Object> variables, String tenantId) {
        this(decisionKey, parentDeploymentId, variables);
        executeDecisionInfo.setTenantId(tenantId);
    }

    @Override
    public Map<String, Object> execute(CommandContext commandContext) {
        if (executeDecisionInfo.getDecisionKey() == null) {
            throw new FlowableIllegalArgumentException("decisionKey is null");
        }

        DmnEngineConfiguration dmnEngineConfiguration = CommandContextUtil.getDmnEngineConfiguration();
        DmnDecisionTable decisionTable = resolveDecisionTable();
        Decision decision = resolveDecision(decisionTable);

        DecisionExecutionAuditContainer executionResult = dmnEngineConfiguration.getRuleEngineExecutor().execute(decision, executeDecisionInfo);

        Map<String, Object> decisionResult = null;
        if (executionResult != null && executionResult.getDecisionResult() != null && !executionResult.getDecisionResult().isEmpty()) {
            if (executionResult.getDecisionResult().size() > 1) {
                throw new FlowableException("more than one result");
            }
            decisionResult = executionResult.getDecisionResult().get(0);
        }

        return decisionResult;
    }

}
