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

package mobius.engine.impl.bpmn.behavior;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import mobius.bpmn.model.MapExceptionEntry;
import mobius.bpmn.model.ServiceTask;
import mobius.common.engine.api.delegate.Expression;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.engine.DynamicBpmnConstants;
import mobius.engine.delegate.BpmnError;
import mobius.engine.delegate.DelegateExecution;
import mobius.engine.impl.bpmn.helper.ErrorPropagation;
import mobius.engine.impl.bpmn.helper.SkipExpressionUtil;
import mobius.engine.impl.context.BpmnOverrideContext;
import mobius.engine.impl.persistence.entity.ExecutionEntity;
import mobius.engine.impl.util.CommandContextUtil;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * ActivityBehavior that evaluates an expression when executed. Optionally, it sets the result of the expression as a variable on the execution.
 *
 *
 * @author Christian Stettler
 *
 * @author Slawomir Wojtasiak (Patch for ACT-1159)
 * @author Falko Menge
 *
 */
public class ServiceTaskExpressionActivityBehavior extends TaskActivityBehavior {

    private static final long serialVersionUID = 1L;

    protected String serviceTaskId;
    protected Expression expression;
    protected Expression skipExpression;
    protected String resultVariable;
    protected List<MapExceptionEntry> mapExceptions;
    protected boolean useLocalScopeForResultVariable;

    public ServiceTaskExpressionActivityBehavior(ServiceTask serviceTask, Expression expression, Expression skipExpression) {

        this.serviceTaskId = serviceTask.getId();
        this.expression = expression;
        this.skipExpression = skipExpression;
        this.resultVariable = serviceTask.getResultVariableName();
        this.mapExceptions = serviceTask.getMapExceptions();
        this.useLocalScopeForResultVariable = serviceTask.isUseLocalScopeForResultVariable();
    }

    @Override
    public void execute(DelegateExecution execution) {
        Object value = null;
        try {
            CommandContext commandContext = CommandContextUtil.getCommandContext();
            String skipExpressionText = null;
            if (skipExpression != null) {
                skipExpressionText = skipExpression.getExpressionText();
            }
            boolean isSkipExpressionEnabled = SkipExpressionUtil.isSkipExpressionEnabled(skipExpressionText, serviceTaskId, execution, commandContext);
            if (!isSkipExpressionEnabled || !SkipExpressionUtil.shouldSkipFlowElement(skipExpressionText, serviceTaskId, execution, commandContext)) {

                if (CommandContextUtil.getProcessEngineConfiguration(commandContext).isEnableProcessDefinitionInfoCache()) {
                    ObjectNode taskElementProperties = BpmnOverrideContext.getBpmnOverrideElementProperties(serviceTaskId, execution.getProcessDefinitionId());
                    if (taskElementProperties != null && taskElementProperties.has(DynamicBpmnConstants.SERVICE_TASK_EXPRESSION)) {
                        String overrideExpression = taskElementProperties.get(DynamicBpmnConstants.SERVICE_TASK_EXPRESSION).asText();
                        if (StringUtils.isNotEmpty(overrideExpression) && !overrideExpression.equals(expression.getExpressionText())) {
                            expression = CommandContextUtil.getProcessEngineConfiguration(commandContext).getExpressionManager().createExpression(overrideExpression);
                        }
                    }
                }

                value = expression.getValue(execution);
                if (resultVariable != null) {
                    if (useLocalScopeForResultVariable) {
                        execution.setVariableLocal(resultVariable, value);
                    } else {
                        execution.setVariable(resultVariable, value);
                    }
                }
            }

            leave(execution);
            
        } catch (Exception exc) {

            Throwable cause = exc;
            BpmnError error = null;
            while (cause != null) {
                if (cause instanceof BpmnError) {
                    error = (BpmnError) cause;
                    break;
                } else if (cause instanceof RuntimeException) {
                    if (ErrorPropagation.mapException((RuntimeException) cause, (ExecutionEntity) execution, mapExceptions)) {
                        return;
                    }
                }
                cause = cause.getCause();
            }

            if (error != null) {
                ErrorPropagation.propagateError(error, execution);
            } else {
                throw exc;
            }
        }
    }
}
