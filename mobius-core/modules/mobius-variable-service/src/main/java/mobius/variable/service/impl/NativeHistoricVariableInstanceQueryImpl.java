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
package mobius.variable.service.impl;

import java.util.List;
import java.util.Map;

import mobius.common.engine.impl.query.AbstractNativeQuery;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.common.engine.impl.interceptor.CommandExecutor;
import mobius.variable.api.history.HistoricVariableInstance;
import mobius.variable.api.history.NativeHistoricVariableInstanceQuery;
import mobius.variable.service.impl.util.CommandContextUtil;

public class NativeHistoricVariableInstanceQueryImpl extends AbstractNativeQuery<NativeHistoricVariableInstanceQuery, HistoricVariableInstance> implements NativeHistoricVariableInstanceQuery {

    private static final long serialVersionUID = 1L;

    public NativeHistoricVariableInstanceQueryImpl(CommandContext commandContext) {
        super(commandContext);
    }

    public NativeHistoricVariableInstanceQueryImpl(CommandExecutor commandExecutor) {
        super(commandExecutor);
    }

    // results ////////////////////////////////////////////////////////////////

    @Override
    public List<HistoricVariableInstance> executeList(CommandContext commandContext, Map<String, Object> parameterMap) {
        return CommandContextUtil.getHistoricVariableInstanceEntityManager(commandContext).findHistoricVariableInstancesByNativeQuery(parameterMap);
    }

    @Override
    public long executeCount(CommandContext commandContext, Map<String, Object> parameterMap) {
        return CommandContextUtil.getHistoricVariableInstanceEntityManager(commandContext).findHistoricVariableInstanceCountByNativeQuery(parameterMap);
    }

}
