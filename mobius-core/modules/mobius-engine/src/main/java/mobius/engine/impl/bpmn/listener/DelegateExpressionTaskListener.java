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
package mobius.engine.impl.bpmn.listener;

import java.util.List;

import mobius.common.engine.api.FlowableException;
import mobius.common.engine.api.FlowableIllegalArgumentException;
import mobius.common.engine.api.delegate.Expression;
import mobius.engine.delegate.TaskListener;
import mobius.engine.impl.bpmn.helper.DelegateExpressionUtil;
import mobius.engine.impl.bpmn.parser.FieldDeclaration;
import mobius.engine.impl.delegate.invocation.TaskListenerInvocation;
import mobius.engine.impl.util.CommandContextUtil;
import mobius.task.service.delegate.DelegateTask;

/**
 *
 */
public class DelegateExpressionTaskListener implements TaskListener {

    protected Expression expression;
    private final List<FieldDeclaration> fieldDeclarations;

    public DelegateExpressionTaskListener(Expression expression, List<FieldDeclaration> fieldDeclarations) {
        this.expression = expression;
        this.fieldDeclarations = fieldDeclarations;
    }

    @Override
    public void notify(DelegateTask delegateTask) {
        Object delegate = DelegateExpressionUtil.resolveDelegateExpression(expression, delegateTask, fieldDeclarations);
        if (delegate instanceof TaskListener) {
            try {
                CommandContextUtil.getProcessEngineConfiguration().getDelegateInterceptor().handleInvocation(new TaskListenerInvocation((TaskListener) delegate, delegateTask));
            } catch (Exception e) {
                throw new FlowableException("Exception while invoking TaskListener: " + e.getMessage(), e);
            }
        } else {
            throw new FlowableIllegalArgumentException("Delegate expression " + expression + " did not resolve to an implementation of " + TaskListener.class);
        }
    }

    /**
     * returns the expression text for this task listener. Comes in handy if you want to check which listeners you already have.
     */
    public String getExpressionText() {
        return expression.getExpressionText();
    }

}
