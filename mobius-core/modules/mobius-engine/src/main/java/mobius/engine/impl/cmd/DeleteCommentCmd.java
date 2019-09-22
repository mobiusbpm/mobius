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
import java.util.ArrayList;

import mobius.common.engine.api.FlowableObjectNotFoundException;
import mobius.common.engine.impl.interceptor.Command;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.engine.compatibility.Flowable5CompatibilityHandler;
import mobius.engine.impl.persistence.entity.CommentEntity;
import mobius.engine.impl.persistence.entity.CommentEntityManager;
import mobius.engine.impl.persistence.entity.ExecutionEntity;
import mobius.engine.impl.util.CommandContextUtil;
import mobius.engine.impl.util.Flowable5Util;
import mobius.engine.task.Comment;
import mobius.task.api.Task;

/**
 * @author Joram Barrez
 */
public class DeleteCommentCmd implements Command<Void>, Serializable {

    private static final long serialVersionUID = 1L;
    protected String taskId;
    protected String processInstanceId;
    protected String commentId;

    public DeleteCommentCmd(String taskId, String processInstanceId, String commentId) {
        this.taskId = taskId;
        this.processInstanceId = processInstanceId;
        this.commentId = commentId;
    }

    @Override
    public Void execute(CommandContext commandContext) {
        CommentEntityManager commentManager = CommandContextUtil.getCommentEntityManager(commandContext);

        if (commentId != null) {
            // Delete for an individual comment
            Comment comment = commentManager.findComment(commentId);
            if (comment == null) {
                throw new FlowableObjectNotFoundException("Comment with id '" + commentId + "' doesn't exists.", Comment.class);
            }

            if (comment.getProcessInstanceId() != null) {
                ExecutionEntity execution = (ExecutionEntity) CommandContextUtil.getExecutionEntityManager(commandContext).findById(comment.getProcessInstanceId());
                if (execution != null && Flowable5Util.isFlowable5ProcessDefinitionId(commandContext, execution.getProcessDefinitionId())) {
                    Flowable5CompatibilityHandler compatibilityHandler = Flowable5Util.getFlowable5CompatibilityHandler();
                    compatibilityHandler.deleteComment(commentId, taskId, processInstanceId);
                    return null;
                }

            } else if (comment.getTaskId() != null) {
                Task task = CommandContextUtil.getTaskService().getTask(comment.getTaskId());
                if (task != null && task.getProcessDefinitionId() != null && Flowable5Util.isFlowable5ProcessDefinitionId(commandContext, task.getProcessDefinitionId())) {
                    Flowable5CompatibilityHandler compatibilityHandler = Flowable5Util.getFlowable5CompatibilityHandler();
                    compatibilityHandler.deleteComment(commentId, taskId, processInstanceId);
                    return null;
                }
            }

            commentManager.delete((CommentEntity) comment);

        } else {
            // Delete all comments on a task of process
            ArrayList<Comment> comments = new ArrayList<>();
            if (processInstanceId != null) {

                ExecutionEntity execution = (ExecutionEntity) CommandContextUtil.getExecutionEntityManager(commandContext).findById(processInstanceId);
                if (execution != null && Flowable5Util.isFlowable5ProcessDefinitionId(commandContext, execution.getProcessDefinitionId())) {
                    Flowable5CompatibilityHandler compatibilityHandler = Flowable5Util.getFlowable5CompatibilityHandler();
                    compatibilityHandler.deleteComment(commentId, taskId, processInstanceId);
                    return null;
                }

                comments.addAll(commentManager.findCommentsByProcessInstanceId(processInstanceId));
            }
            if (taskId != null) {

                Task task = CommandContextUtil.getTaskService().getTask(taskId);
                if (task != null && task.getProcessDefinitionId() != null && Flowable5Util.isFlowable5ProcessDefinitionId(commandContext, task.getProcessDefinitionId())) {
                    Flowable5CompatibilityHandler compatibilityHandler = Flowable5Util.getFlowable5CompatibilityHandler();
                    compatibilityHandler.deleteComment(commentId, taskId, processInstanceId);
                    return null;
                }

                comments.addAll(commentManager.findCommentsByTaskId(taskId));
            }

            for (Comment comment : comments) {
                commentManager.delete((CommentEntity) comment);
            }
        }
        return null;
    }
}
