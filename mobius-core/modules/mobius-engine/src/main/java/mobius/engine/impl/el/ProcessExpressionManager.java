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

import java.util.List;
import java.util.Map;

import mobius.common.engine.api.delegate.Expression;
import mobius.common.engine.api.variable.VariableContainer;
import mobius.common.engine.impl.el.DynamicBeanPropertyELResolver;
import mobius.common.engine.impl.javax.el.BeanELResolver;
import mobius.common.engine.impl.javax.el.ELResolver;
import mobius.common.engine.impl.javax.el.ValueExpression;
import mobius.engine.impl.bpmn.data.ItemInstance;
import mobius.engine.impl.delegate.invocation.DefaultDelegateInterceptor;
import mobius.engine.impl.interceptor.DelegateInterceptor;
import mobius.variable.service.impl.el.VariableScopeExpressionManager;

/**
 * @author Joram Barrez
 */
public class ProcessExpressionManager extends VariableScopeExpressionManager {
    
    protected DelegateInterceptor delegateInterceptor;
    
    public ProcessExpressionManager() {
        this(null);
    }

    public ProcessExpressionManager(Map<Object, Object> beans) {
       this(new DefaultDelegateInterceptor(), beans);
    }
    
    public ProcessExpressionManager(DelegateInterceptor delegateInterceptor, Map<Object, Object> beans) {
        super(beans);
        this.delegateInterceptor = delegateInterceptor;
    }
    
    @Override
    protected Expression createJuelExpression(String expression, ValueExpression valueExpression) {
        return new JuelExpression(this, this.delegateInterceptor, valueExpression, expression);
    }
    
    @Override
    protected ELResolver createVariableElResolver(VariableContainer variableContainer) {
        return new ProcessVariableScopeELResolver(variableContainer);
    }

    @Override
    protected void configureResolvers(List<ELResolver> elResolvers) {
        int beanElResolverIndex = -1;
        for (int i=0; i<elResolvers.size(); i++) {
            if (elResolvers.get(i) instanceof BeanELResolver) {
                beanElResolverIndex = i;
            }
        }
        
        if (beanElResolverIndex > 0) {
            elResolvers.add(beanElResolverIndex, new DynamicBeanPropertyELResolver(ItemInstance.class, "getFieldValue", "setFieldValue"));
        }
    }
    
}
