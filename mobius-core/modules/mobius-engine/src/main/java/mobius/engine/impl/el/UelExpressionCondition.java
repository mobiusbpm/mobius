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

package mobius.engine.impl.el;

import mobius.common.engine.api.FlowableException;
import mobius.common.engine.api.delegate.Expression;
import mobius.engine.delegate.DelegateExecution;
import mobius.engine.impl.Condition;

/**
 * {@link Condition} that resolves an UEL expression at runtime.
 * 
 *
 *
 */
public class UelExpressionCondition implements Condition {

    protected Expression expression;

    public UelExpressionCondition(Expression expression) {
        this.expression = expression;
    }

    @Override
    public boolean evaluate(String sequenceFlowId, DelegateExecution execution) {
        Object result = expression.getValue(execution);

        if (result == null) {
            throw new FlowableException("condition expression returns null (sequenceFlowId: " + sequenceFlowId + ")" );
        }
        if (!(result instanceof Boolean)) {
            throw new FlowableException("condition expression returns non-Boolean (sequenceFlowId: " + sequenceFlowId + "): " + result + " (" + result.getClass().getName() + ")");
        }
        return (Boolean) result;
    }

}
