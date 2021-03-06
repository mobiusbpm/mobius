package mobius.rest.service.api.history;

import java.util.Date;

import mobius.common.rest.api.PaginateRequest;

/**
 * @author Luis Belloch
 */
public class HistoricTaskLogEntryQueryRequest extends PaginateRequest {

    protected String taskId;
    protected String type;
    protected String userId;
    protected String processInstanceId;
    protected String processDefinitionId;
    protected String scopeId;
    protected String scopeDefinitionId;
    protected String subScopeId;
    protected String scopeType;
    protected Date from;
    protected Date to;
    protected String tenantId;
    protected Long fromLogNumber;
    protected Long toLogNumber;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getProcessDefinitionId() {
        return processDefinitionId;
    }

    public void setProcessDefinitionId(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    public String getScopeId() {
        return scopeId;
    }

    public void setScopeId(String scopeId) {
        this.scopeId = scopeId;
    }

    public String getScopeDefinitionId() {
        return scopeDefinitionId;
    }

    public void setScopeDefinitionId(String scopeDefinitionId) {
        this.scopeDefinitionId = scopeDefinitionId;
    }

    public String getSubScopeId() {
        return subScopeId;
    }

    public void setSubScopeId(String subScopeId) {
        this.subScopeId = subScopeId;
    }

    public String getScopeType() {
        return scopeType;
    }

    public void setScopeType(String scopeType) {
        this.scopeType = scopeType;
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public Long getFromLogNumber() {
        return fromLogNumber;
    }

    public void setFromLogNumber(Long fromLogNumber) {
        this.fromLogNumber = fromLogNumber;
    }

    public Long getToLogNumber() {
        return toLogNumber;
    }

    public void setToLogNumber(Long toLogNumber) {
        this.toLogNumber = toLogNumber;
    }
}
