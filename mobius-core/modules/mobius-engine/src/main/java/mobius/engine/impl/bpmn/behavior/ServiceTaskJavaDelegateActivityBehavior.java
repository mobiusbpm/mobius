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

import mobius.common.engine.api.delegate.Expression;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.engine.delegate.DelegateExecution;
import mobius.engine.delegate.ExecutionListener;
import mobius.engine.delegate.JavaDelegate;
import mobius.engine.impl.bpmn.helper.SkipExpressionUtil;
import mobius.engine.impl.delegate.ActivityBehavior;
import mobius.engine.impl.delegate.TriggerableActivityBehavior;
import mobius.engine.impl.delegate.invocation.JavaDelegateInvocation;
import mobius.engine.impl.util.CommandContextUtil;

/**
 * @author Tom Baeyens
 */
public class ServiceTaskJavaDelegateActivityBehavior extends TaskActivityBehavior implements ActivityBehavior, ExecutionListener {

    private static final long serialVersionUID = 1L;

    protected JavaDelegate javaDelegate;
    protected Expression skipExpression;
    protected boolean triggerable;

    protected ServiceTaskJavaDelegateActivityBehavior() {
    }

    public ServiceTaskJavaDelegateActivityBehavior(JavaDelegate javaDelegate, boolean triggerable, Expression skipExpression) {
        this.javaDelegate = javaDelegate;
        this.triggerable = triggerable;
        this.skipExpression = skipExpression;
    }

    @Override
    public void trigger(DelegateExecution execution, String signalName, Object signalData) {
        if (triggerable && javaDelegate instanceof TriggerableActivityBehavior) {
            ((TriggerableActivityBehavior) javaDelegate).trigger(execution, signalName, signalData);
            leave(execution);
        }
    }

    @Override
    public void execute(DelegateExecution execution) {
        CommandContext commandContext = CommandContextUtil.getCommandContext();
        String skipExpressionText = null;
        if (skipExpression != null) {
            skipExpressionText = skipExpression.getExpressionText();
        }
        boolean isSkipExpressionEnabled = SkipExpressionUtil.isSkipExpressionEnabled(skipExpressionText, 
                        execution.getCurrentActivityId(), execution, commandContext);
        
        if (!isSkipExpressionEnabled || !SkipExpressionUtil.shouldSkipFlowElement(skipExpressionText, 
                        execution.getCurrentActivityId(), execution, commandContext)) {

            CommandContextUtil.getProcessEngineConfiguration(commandContext).getDelegateInterceptor()
                .handleInvocation(new JavaDelegateInvocation(javaDelegate, execution));
        }

        if (!triggerable) {
            leave(execution);
        }
    }

    @Override
    public void notify(DelegateExecution execution) {
        execute(execution);
    }
}
