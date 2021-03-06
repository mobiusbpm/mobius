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
package mobius.cmmn.engine.impl.scripting;

import mobius.cmmn.engine.CmmnEngineConfiguration;
import mobius.common.engine.impl.AbstractEngineConfiguration;
import mobius.common.engine.impl.scripting.Resolver;
import mobius.common.engine.impl.scripting.ResolverFactory;
import mobius.variable.api.delegate.VariableScope;

/**
 *
 *
 */
public class CmmnVariableScopeResolverFactory implements ResolverFactory {

    @Override
    public Resolver createResolver(AbstractEngineConfiguration engineConfiguration, VariableScope variableScope) {
        if (variableScope != null) {
            return new CmmnVariableScopeResolver((CmmnEngineConfiguration) engineConfiguration, variableScope);
        }
        return null;
    }

}
