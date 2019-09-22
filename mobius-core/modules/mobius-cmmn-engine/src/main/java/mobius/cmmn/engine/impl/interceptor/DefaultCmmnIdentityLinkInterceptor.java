package mobius.cmmn.engine.impl.interceptor;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import mobius.cmmn.engine.impl.persistence.entity.CaseInstanceEntity;
import mobius.cmmn.engine.impl.util.CommandContextUtil;
import mobius.cmmn.engine.impl.util.IdentityLinkUtil;
import mobius.cmmn.engine.interceptor.CmmnIdentityLinkInterceptor;
import mobius.common.engine.api.scope.ScopeTypes;
import mobius.common.engine.impl.identity.Authentication;
import mobius.identitylink.api.IdentityLinkType;
import mobius.identitylink.service.impl.persistence.entity.IdentityLinkEntity;
import mobius.task.api.Task;
import mobius.task.service.impl.persistence.entity.TaskEntity;

public class DefaultCmmnIdentityLinkInterceptor implements CmmnIdentityLinkInterceptor {

    @Override
    public void handleCompleteTask(TaskEntity task) {
        String userId = Authentication.getAuthenticatedUserId();
        if (StringUtils.isNotEmpty(userId)) {
            addUserIdentityLinkToParent(task, userId);
        }
    }
    
    @Override
    public void handleAddIdentityLinkToTask(TaskEntity taskEntity, IdentityLinkEntity identityLinkEntity) {
        addUserIdentityLinkToParent(taskEntity, identityLinkEntity.getUserId());
    }
    
    @Override
    public void handleAddAssigneeIdentityLinkToTask(TaskEntity taskEntity, String assignee) {
        addUserIdentityLinkToParent(taskEntity, assignee);
    }
    
    @Override
    public void handleAddOwnerIdentityLinkToTask(TaskEntity taskEntity, String owner) {
        addUserIdentityLinkToParent(taskEntity, owner);
    }

    @Override
    public void handleCreateCaseInstance(CaseInstanceEntity caseInstance) {
        String authenticatedUserId = Authentication.getAuthenticatedUserId();
        if (authenticatedUserId != null) {
            IdentityLinkUtil.createCaseInstanceIdentityLink(caseInstance, authenticatedUserId, null, IdentityLinkType.STARTER);
        }
    }
    
    protected void addUserIdentityLinkToParent(Task task, String userId) {
        if (userId != null && ScopeTypes.CMMN.equals(task.getScopeType()) && StringUtils.isNotEmpty(task.getScopeId())) {
            CaseInstanceEntity caseInstanceEntity = CommandContextUtil.getCaseInstanceEntityManager().findById(task.getScopeId());
            if (caseInstanceEntity != null) {
                List<IdentityLinkEntity> identityLinks = CommandContextUtil.getIdentityLinkService()
                    .findIdentityLinksByScopeIdAndType(caseInstanceEntity.getId(), ScopeTypes.CMMN);
                for (IdentityLinkEntity identityLink : identityLinks) {
                    if (identityLink.isUser() && identityLink.getUserId().equals(userId) && IdentityLinkType.PARTICIPANT.equals(identityLink.getType())) {
                        return;
                    }
                }

                IdentityLinkUtil.createCaseInstanceIdentityLink(caseInstanceEntity, userId, null, IdentityLinkType.PARTICIPANT);
            }
        }
    }
}