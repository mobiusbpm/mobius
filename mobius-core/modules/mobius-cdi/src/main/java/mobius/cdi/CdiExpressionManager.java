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
package mobius.cdi;

import mobius.cdi.impl.el.CdiResolver;
import mobius.common.engine.impl.el.DefaultExpressionManager;
import mobius.common.engine.impl.javax.el.ArrayELResolver;
import mobius.common.engine.impl.javax.el.ELResolver;
import mobius.engine.impl.el.ProcessExpressionManager;

import java.util.List;

/**
 * {@link DefaultExpressionManager} for resolving Cdi-managed beans.
 * 
 * This {@link DefaultExpressionManager} implementation performs lazy lookup of the Cdi-BeanManager and can thus be configured using the spring-based configuration of the process engine:
 * 
 * <pre>
 * &lt;property name="expressionManager"&gt;
 *      &lt;bean class="mobius.cdi.CdiExpressionManager" /&gt;
 * &lt;/property&gt;
 * </pre>
 * 
 * @author Daniel Meyer
 */
public class CdiExpressionManager extends ProcessExpressionManager {

    @Override
    protected void configureResolvers(List<ELResolver> elResolvers) {
        int arrayElResolverIndex = -1;
        for (int i=0; i<elResolvers.size(); i++) {
            if (elResolvers.get(i) instanceof ArrayELResolver) {
                arrayElResolverIndex = i;
            }
        }
        
        if (arrayElResolverIndex > 0) {
            elResolvers.add(arrayElResolverIndex, new CdiResolver());
        }
    }

}
