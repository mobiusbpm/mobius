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

package mobius.engine.test.mock;

import mobius.common.engine.api.variable.VariableContainer;
import mobius.common.engine.impl.javax.el.ArrayELResolver;
import mobius.common.engine.impl.javax.el.BeanELResolver;
import mobius.common.engine.impl.javax.el.CompositeELResolver;
import mobius.common.engine.impl.javax.el.CouldNotResolvePropertyELResolver;
import mobius.common.engine.impl.javax.el.ELResolver;
import mobius.common.engine.impl.javax.el.ListELResolver;
import mobius.common.engine.impl.javax.el.MapELResolver;
import mobius.engine.impl.el.ProcessExpressionManager;
import mobius.engine.impl.el.ProcessVariableScopeELResolver;

public class MockExpressionManager extends ProcessExpressionManager {

    @Override
    protected ELResolver createElResolver(VariableContainer variableContainer) {
        CompositeELResolver compositeElResolver = new CompositeELResolver();
        compositeElResolver.add(new ProcessVariableScopeELResolver(variableContainer));
        compositeElResolver.add(new MockElResolver());
        compositeElResolver.add(new ArrayELResolver());
        compositeElResolver.add(new ListELResolver());
        compositeElResolver.add(new MapELResolver());
        compositeElResolver.add(new BeanELResolver());
        compositeElResolver.add(new CouldNotResolvePropertyELResolver());
        return compositeElResolver;
    }

}
