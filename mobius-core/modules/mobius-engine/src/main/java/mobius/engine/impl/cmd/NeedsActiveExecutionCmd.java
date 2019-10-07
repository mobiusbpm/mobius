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
package mobius.engine.impl.cmd;

import java.io.Serializable;

import mobius.common.engine.api.FlowableException;
import mobius.common.engine.api.FlowableIllegalArgumentException;
import mobius.common.engine.api.FlowableObjectNotFoundException;
import mobius.common.engine.impl.interceptor.Command;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.engine.impl.persistence.entity.ExecutionEntity;
import mobius.engine.impl.util.CommandContextUtil;
import mobius.engine.runtime.Execution;

/**
 *
 */
public abstract class NeedsActiveExecutionCmd<T> implements Command<T>, Serializable {

    private static final long serialVersionUID = 1L;

    protected String executionId;

    public NeedsActiveExecutionCmd(String executionId) {
        this.executionId = executionId;
    }

    @Override
    public T execute(CommandContext commandContext) {
        if (executionId == null) {
            throw new FlowableIllegalArgumentException("executionId is null");
        }

        ExecutionEntity execution = CommandContextUtil.getExecutionEntityManager(commandContext).findById(executionId);

        if (execution == null) {
            throw new FlowableObjectNotFoundException("execution " + executionId + " doesn't exist", Execution.class);
        }

        if (execution.isSuspended()) {
            throw new FlowableException(getSuspendedExceptionMessage());
        }

        return execute(commandContext, execution);
    }

    /**
     * Subclasses should implement this method. The provided {@link ExecutionEntity} is guaranteed to be active (ie. not suspended).
     */
    protected abstract T execute(CommandContext commandContext, ExecutionEntity execution);

    /**
     * Subclasses can override this to provide a more detailed exception message that will be thrown when the execution is suspended.
     */
    protected String getSuspendedExceptionMessage() {
        return "Cannot execution operation because execution '" + executionId + "' is suspended";
    }

}
