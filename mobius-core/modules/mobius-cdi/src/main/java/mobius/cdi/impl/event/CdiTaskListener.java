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
package mobius.cdi.impl.event;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.Date;

import javax.enterprise.inject.spi.BeanManager;

import mobius.cdi.BusinessProcessEvent;
import mobius.cdi.BusinessProcessEventType;
import mobius.cdi.annotation.event.AssignTaskLiteral;
import mobius.cdi.annotation.event.BusinessProcessLiteral;
import mobius.cdi.annotation.event.CompleteTaskLiteral;
import mobius.cdi.annotation.event.CreateTaskLiteral;
import mobius.cdi.annotation.event.DeleteTaskLiteral;
import mobius.cdi.impl.util.BeanManagerLookup;
import mobius.cdi.impl.util.ProgrammaticBeanLookup;
import mobius.common.engine.api.FlowableException;
import mobius.engine.ProcessEngine;
import mobius.engine.delegate.TaskListener;
import mobius.engine.impl.cfg.ProcessEngineConfigurationImpl;
import mobius.engine.repository.ProcessDefinition;
import mobius.task.service.delegate.DelegateTask;

/**
 * Generic {@link TaskListener} publishing events using the cdi event infrastructure.
 * 
 * @author Dimitris Mandalidis
 */
public class CdiTaskListener implements TaskListener, Serializable {

    private static final long serialVersionUID = 1L;

    protected final BusinessProcessEventType type;
    protected final String transitionName;
    protected final String activityId;

    public CdiTaskListener(String transitionName) {
        this.type = BusinessProcessEventType.TAKE;
        this.transitionName = transitionName;
        this.activityId = null;
    }

    public CdiTaskListener(String activityId, BusinessProcessEventType type) {
        this.type = type;
        this.transitionName = null;
        this.activityId = activityId;
    }

    @Override
    public void notify(DelegateTask task) {
        // test whether cdi is setup correctly. (if not, just do not deliver the
        // event)
        try {
            ProgrammaticBeanLookup.lookup(ProcessEngine.class);
        } catch (Exception e) {
            return;
        }

        BusinessProcessEvent event = createEvent(task);
        Annotation[] qualifiers = getQualifiers(event);
        getBeanManager().fireEvent(event, qualifiers);
    }

    protected BusinessProcessEvent createEvent(DelegateTask task) {
        ProcessEngineConfigurationImpl engineConfiguration = mobius.engine.impl.context.Context.getProcessEngineConfiguration();
        ProcessDefinition processDefinition = engineConfiguration.getProcessDefinitionCache().get(task.getProcessDefinitionId()).getProcessDefinition();
        Date now = engineConfiguration.getClock().getCurrentTime();
        return new CdiBusinessProcessEvent(activityId, transitionName, processDefinition, task, type, task.getProcessInstanceId(), task.getExecutionId(), now);
    }

    protected BeanManager getBeanManager() {
        BeanManager bm = BeanManagerLookup.getBeanManager();
        if (bm == null) {
            throw new FlowableException("No cdi bean manager available, cannot publish event.");
        }
        return bm;
    }

    protected Annotation[] getQualifiers(BusinessProcessEvent event) {
        Annotation businessProcessQualifier = new BusinessProcessLiteral(event.getProcessDefinition().getKey());
        if (type == BusinessProcessEventType.CREATE_TASK) {
            return new Annotation[] { businessProcessQualifier, new CreateTaskLiteral(activityId) };
        }
        if (type == BusinessProcessEventType.ASSIGN_TASK) {
            return new Annotation[] { businessProcessQualifier, new AssignTaskLiteral(activityId) };
        }
        if (type == BusinessProcessEventType.COMPLETE_TASK) {
            return new Annotation[] { businessProcessQualifier, new CompleteTaskLiteral(activityId) };
        }
        if (type == BusinessProcessEventType.DELETE_TASK) {
            return new Annotation[] { businessProcessQualifier, new DeleteTaskLiteral(activityId) };
        }
        return new Annotation[] {};
    }
}
