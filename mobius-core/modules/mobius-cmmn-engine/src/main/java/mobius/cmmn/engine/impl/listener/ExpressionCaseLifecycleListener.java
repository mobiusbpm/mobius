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

import mobius.cmmn.api.listener.CaseInstanceLifecycleListener;
import mobius.cmmn.api.runtime.CaseInstance;
import mobius.cmmn.engine.impl.persistence.entity.CaseInstanceEntity;
import mobius.common.engine.api.delegate.Expression;

/**
 * @author martin.grofcik
 */
public class ExpressionCaseLifecycleListener implements CaseInstanceLifecycleListener {

    protected String sourceState;
    protected String targetState;
    protected Expression expression;

    public ExpressionCaseLifecycleListener(String sourceState, String targetState, Expression expression) {
        this.sourceState = sourceState;
        this.targetState = targetState;
        this.expression = expression;
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
        expression.getValue((CaseInstanceEntity) caseInstance);
    }

    /**
     * returns the expression text for this case instance lifecycle listener.
     */
    public String getExpressionText() {
        return expression.getExpressionText();
    }

}
