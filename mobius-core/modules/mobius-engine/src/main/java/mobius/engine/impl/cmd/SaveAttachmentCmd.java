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

import mobius.common.engine.api.delegate.event.FlowableEngineEventType;
import mobius.common.engine.api.delegate.event.FlowableEventDispatcher;
import mobius.common.engine.impl.interceptor.Command;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.engine.compatibility.Flowable5CompatibilityHandler;
import mobius.engine.delegate.event.impl.FlowableEventBuilder;
import mobius.engine.impl.persistence.entity.AttachmentEntity;
import mobius.engine.impl.persistence.entity.ExecutionEntity;
import mobius.engine.impl.util.CommandContextUtil;
import mobius.engine.impl.util.Flowable5Util;
import mobius.engine.task.Attachment;

/**
 *
 */
public class SaveAttachmentCmd implements Command<Object>, Serializable {

    private static final long serialVersionUID = 1L;
    protected Attachment attachment;

    public SaveAttachmentCmd(Attachment attachment) {
        this.attachment = attachment;
    }

    @Override
    public Object execute(CommandContext commandContext) {
        AttachmentEntity updateAttachment = CommandContextUtil.getAttachmentEntityManager().findById(attachment.getId());

        String processInstanceId = updateAttachment.getProcessInstanceId();
        String processDefinitionId = null;
        if (updateAttachment.getProcessInstanceId() != null) {
            ExecutionEntity process = CommandContextUtil.getExecutionEntityManager(commandContext).findById(processInstanceId);
            if (process != null) {
                processDefinitionId = process.getProcessDefinitionId();
                if (Flowable5Util.isFlowable5ProcessDefinitionId(commandContext, process.getProcessDefinitionId())) {
                    Flowable5CompatibilityHandler compatibilityHandler = Flowable5Util.getFlowable5CompatibilityHandler();
                    compatibilityHandler.saveAttachment(attachment);
                    return null;
                }
            }
        }

        updateAttachment.setName(attachment.getName());
        updateAttachment.setDescription(attachment.getDescription());

        FlowableEventDispatcher eventDispatcher = CommandContextUtil.getProcessEngineConfiguration(commandContext).getEventDispatcher();
        if (eventDispatcher != null && eventDispatcher.isEnabled()) {
            eventDispatcher
                    .dispatchEvent(FlowableEventBuilder.createEntityEvent(FlowableEngineEventType.ENTITY_UPDATED, attachment, processInstanceId, processInstanceId, processDefinitionId));
        }

        return null;
    }
}
