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
package mobius.form.engine.impl;

import java.util.List;
import java.util.Map;

import mobius.common.engine.impl.query.AbstractNativeQuery;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.common.engine.impl.interceptor.CommandExecutor;
import mobius.form.api.FormDefinition;
import mobius.form.api.NativeFormDefinitionQuery;
import mobius.form.engine.impl.util.CommandContextUtil;

public class NativeFormDefinitionQueryImpl extends AbstractNativeQuery<NativeFormDefinitionQuery, FormDefinition> implements NativeFormDefinitionQuery {

    private static final long serialVersionUID = 1L;

    public NativeFormDefinitionQueryImpl(CommandContext commandContext) {
        super(commandContext);
    }

    public NativeFormDefinitionQueryImpl(CommandExecutor commandExecutor) {
        super(commandExecutor);
    }

    // results ////////////////////////////////////////////////////////////////

    @Override
    public List<FormDefinition> executeList(CommandContext commandContext, Map<String, Object> parameterMap) {
        return CommandContextUtil.getFormDefinitionEntityManager(commandContext).findFormDefinitionsByNativeQuery(parameterMap);
    }

    @Override
    public long executeCount(CommandContext commandContext, Map<String, Object> parameterMap) {
        return CommandContextUtil.getFormDefinitionEntityManager(commandContext).findFormDefinitionCountByNativeQuery(parameterMap);
    }

}
