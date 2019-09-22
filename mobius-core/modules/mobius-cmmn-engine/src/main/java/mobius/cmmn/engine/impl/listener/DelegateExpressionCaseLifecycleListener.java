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
package mobius.cmmn.engine.impl.listener;

import java.util.List;

import mobius.cmmn.api.listener.CaseInstanceLifecycleListener;
import mobius.cmmn.api.runtime.CaseInstance;
import mobius.cmmn.engine.impl.persistence.entity.CaseInstanceEntity;
import mobius.cmmn.engine.impl.util.DelegateExpressionUtil;
import mobius.cmmn.model.FieldExtension;
import mobius.common.engine.api.FlowableException;
import mobius.common.engine.api.FlowableIllegalArgumentException;
import mobius.common.engine.api.delegate.Expression;

/**
 * @author martin.grofcik
 */
public class DelegateExpressionCaseLifecycleListener implements CaseInstanceLifecycleListener {

    protected String sourceState;
    protected String targetState;
    protected Expression expression;
    protected List<FieldExtension> fieldExtensions;

    public DelegateExpressionCaseLifecycleListener(String sourceState, String targetState, Expression expression,
        List<FieldExtension> fieldExtensions) {
        this.sourceState = sourceState;
        this.targetState = targetState;
        this.expression = expression;
        this.fieldExtensions = fieldExtensions;
    }

    @Override
    public String getSourceState() {
        return sourceState;
    }

    @Override
    public String getTargetState() {
        return targetState;
    }

    @Override
    public void stateChanged(CaseInstance caseInstance, String oldState, String newState) {
        try {
            CaseInstanceEntity caseInstanceEntity = (CaseInstanceEntity) caseInstance;
            Object delegate = DelegateExpressionUtil.resolveDelegateExpression(expression, caseInstanceEntity, fieldExtensions);

            if (delegate instanceof CaseInstanceLifecycleListener) {
                try {
                    CaseInstanceLifecycleListener listener = (CaseInstanceLifecycleListener) delegate;
                    listener.stateChanged(caseInstanceEntity, oldState, newState);
                } catch (Exception e) {
                    throw new FlowableException("Exception while invoking CaseInstanceLifeCycleListener: " + e.getMessage(), e);
                }
            } else {
                throw new FlowableIllegalArgumentException("Delegate expression " + expression + " did not resolve to an implementation of " + CaseInstanceLifecycleListener.class);
            }

        } catch (Exception e) {
            throw new FlowableException(e.getMessage(), e);
        }
    }

    /**
     * returns the expression text for this CaseInstance lifecycle listener.
     */
    public String getExpressionText() {
        return expression.getExpressionText();
    }

}
