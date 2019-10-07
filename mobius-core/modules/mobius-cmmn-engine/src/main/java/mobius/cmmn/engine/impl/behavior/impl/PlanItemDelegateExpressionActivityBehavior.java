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
package mobius.cmmn.engine.impl.behavior.impl;

import java.util.List;

import mobius.cmmn.api.delegate.PlanItemJavaDelegate;
import mobius.cmmn.engine.impl.behavior.CmmnActivityBehavior;
import mobius.cmmn.engine.impl.behavior.CoreCmmnActivityBehavior;
import mobius.cmmn.engine.impl.behavior.PlanItemActivityBehavior;
import mobius.cmmn.engine.impl.persistence.entity.PlanItemInstanceEntity;
import mobius.cmmn.engine.impl.util.CommandContextUtil;
import mobius.cmmn.engine.impl.util.DelegateExpressionUtil;
import mobius.cmmn.model.FieldExtension;
import mobius.common.engine.api.FlowableException;
import mobius.common.engine.api.FlowableIllegalArgumentException;
import mobius.common.engine.api.delegate.Expression;
import mobius.common.engine.impl.interceptor.CommandContext;

/**
 * {@link CmmnActivityBehavior} used when 'delegateExpression' is used for a serviceTask.
 * 
 *
 * @author Josh Long
 *
 */
public class PlanItemDelegateExpressionActivityBehavior extends CoreCmmnActivityBehavior {

    protected String expression;
    protected List<FieldExtension> fieldExtensions;

    public PlanItemDelegateExpressionActivityBehavior(String expression, List<FieldExtension> fieldExtensions) {
        this.expression = expression;
        this.fieldExtensions = fieldExtensions;
    }
    
    @Override
    public void execute(CommandContext commandContext, PlanItemInstanceEntity planItemInstanceEntity) {
        try {
            Expression expressionObject = CommandContextUtil.getCmmnEngineConfiguration(commandContext).getExpressionManager().createExpression(expression);
            Object delegate = DelegateExpressionUtil.resolveDelegateExpression(expressionObject, planItemInstanceEntity, fieldExtensions);
            if (delegate instanceof PlanItemActivityBehavior) {
                ((PlanItemActivityBehavior) delegate).execute(planItemInstanceEntity);

            } else if (delegate instanceof PlanItemJavaDelegate) {
                PlanItemJavaDelegateActivityBehavior behavior = new PlanItemJavaDelegateActivityBehavior((PlanItemJavaDelegate) delegate);
                behavior.execute(planItemInstanceEntity);

            } else {
                throw new FlowableIllegalArgumentException("Delegate expression " + expression + " did neither resolve to an implementation of " + 
                                PlanItemActivityBehavior.class + " nor " + PlanItemJavaDelegate.class);
            }    
           
        } catch (Exception exc) {
            throw new FlowableException(exc.getMessage(), exc);
        }
    }

}
