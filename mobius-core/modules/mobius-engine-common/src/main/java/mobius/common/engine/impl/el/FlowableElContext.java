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
package mobius.common.engine.impl.el;

import java.util.List;

import mobius.common.engine.api.delegate.FlowableFunctionDelegate;
import mobius.common.engine.impl.javax.el.ELContext;
import mobius.common.engine.impl.javax.el.ELResolver;
import mobius.common.engine.impl.javax.el.FunctionMapper;
import mobius.common.engine.impl.javax.el.VariableMapper;

/**
 *
 *
 */
public class FlowableElContext extends ELContext {

    protected ELResolver elResolver;
    protected List<FlowableFunctionDelegate> functionDelegates;

    public FlowableElContext(ELResolver elResolver, List<FlowableFunctionDelegate> functionDelegates) {
        this.elResolver = elResolver;
        this.functionDelegates = functionDelegates;
    }

    @Override
    public ELResolver getELResolver() {
        return elResolver;
    }

    @Override
    public FunctionMapper getFunctionMapper() {
        return new FlowableFunctionMapper(functionDelegates);
    }

    @Override
    public VariableMapper getVariableMapper() {
        return null;
    }
}
