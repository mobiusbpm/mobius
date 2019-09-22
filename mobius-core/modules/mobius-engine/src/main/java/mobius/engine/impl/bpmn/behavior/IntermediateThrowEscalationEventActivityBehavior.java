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

package mobius.engine.impl.bpmn.behavior;

import mobius.bpmn.model.Escalation;
import mobius.bpmn.model.EscalationEventDefinition;
import mobius.bpmn.model.ThrowEvent;
import mobius.common.engine.impl.context.Context;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.engine.delegate.DelegateExecution;
import mobius.engine.impl.bpmn.helper.EscalationPropagation;
import mobius.engine.impl.persistence.entity.ExecutionEntity;
import mobius.engine.impl.util.CommandContextUtil;

/**
 * @author Tijs Rademakers
 */
public class IntermediateThrowEscalationEventActivityBehavior extends AbstractBpmnActivityBehavior {

    private static final long serialVersionUID = 1L;

    protected final EscalationEventDefinition escalationEventDefinition;
    protected String escalationCode;
    protected String escalationName;

    public IntermediateThrowEscalationEventActivityBehavior(ThrowEvent throwEvent, EscalationEventDefinition escalationEventDefinition, 
                    Escalation escalation) {
        
        if (escalation != null) {
            escalationCode = escalation.getEscalationCode();
            escalationName = escalation.getName();
            
        } else {
            escalationCode = escalationEventDefinition.getEscalationCode();
            escalationName = escalationCode;
        }

        this.escalationEventDefinition = escalationEventDefinition;
    }

    @Override
    public void execute(DelegateExecution execution) {
        CommandContext commandContext = Context.getCommandContext();
        EscalationPropagation.propagateEscalation(escalationCode, escalationName, execution);
        CommandContextUtil.getAgenda(commandContext).planTakeOutgoingSequenceFlowsOperation((ExecutionEntity) execution, true);
    }

}
