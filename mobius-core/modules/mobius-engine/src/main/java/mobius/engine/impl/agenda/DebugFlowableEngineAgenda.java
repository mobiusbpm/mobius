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
package mobius.engine.impl.agenda;

import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.engine.impl.persistence.entity.ExecutionEntity;
import mobius.engine.runtime.ProcessDebugger;

/**
 * This {@link mobius.engine.FlowableEngineAgenda} schedules operations which allow debugging
 */
public class DebugFlowableEngineAgenda extends DefaultFlowableEngineAgenda {

    protected ProcessDebugger processDebugger;

    public DebugFlowableEngineAgenda(CommandContext commandContext, ProcessDebugger processDebugger) {
        super(commandContext);
        this.processDebugger = processDebugger;
    }

    @Override
    public void planContinueProcessOperation(ExecutionEntity execution) {
        planOperation(new DebugContinueProcessOperation(processDebugger, commandContext, execution), execution);
    }

    @Override
    public void planContinueProcessSynchronousOperation(ExecutionEntity execution) {
        planOperation(new DebugContinueProcessOperation(processDebugger, commandContext, execution, true, false), execution);
    }

    @Override
    public void planContinueProcessInCompensation(ExecutionEntity execution) {
        planOperation(new DebugContinueProcessOperation(processDebugger, commandContext, execution, false, true), execution);
    }

}
