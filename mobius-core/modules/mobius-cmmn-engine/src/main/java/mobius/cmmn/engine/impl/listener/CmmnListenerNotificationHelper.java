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
package mobius.cmmn.engine.impl.listener;

import mobius.cmmn.api.listener.CaseInstanceLifecycleListener;
import mobius.cmmn.api.listener.PlanItemInstanceLifecycleListener;
import mobius.cmmn.engine.impl.repository.CaseDefinitionUtil;
import mobius.cmmn.engine.impl.util.CommandContextUtil;
import mobius.cmmn.model.CaseElement;
import mobius.cmmn.model.CmmnModel;
import mobius.cmmn.model.FlowableListener;
import mobius.cmmn.model.HumanTask;
import mobius.cmmn.model.ImplementationType;
import mobius.common.engine.api.FlowableException;
import mobius.task.service.delegate.TaskListener;
import mobius.task.service.impl.persistence.entity.TaskEntity;

/**
 * @author Joram Barrez
 */
public class CmmnListenerNotificationHelper {

    public void executeTaskListeners(TaskEntity taskEntity, String eventType) {
        if (taskEntity.getScopeDefinitionId() != null) {
            CmmnModel cmmnModel = CaseDefinitionUtil.getCmmnModel(taskEntity.getScopeDefinitionId());
            CaseElement caseElement = cmmnModel.getPrimaryCase().getAllCaseElements().get(taskEntity.getTaskDefinitionKey());
            if (caseElement instanceof HumanTask) {
                HumanTask humanTask = (HumanTask) caseElement;
                executeTaskListeners(humanTask, taskEntity, eventType);
            }
        }
    }

    public void executeTaskListeners(HumanTask humanTask, TaskEntity taskEntity, String eventType) {
        for (FlowableListener listener : humanTask.getTaskListeners()) {
            String event = listener.getEvent();
            if (event.equals(eventType) || event.equals(TaskListener.EVENTNAME_ALL_EVENTS)) {
                TaskListener taskListener = createTaskListener(listener);

                taskEntity.setEventName(eventType);
                taskEntity.setEventHandlerId(listener.getId());

                try {
                    taskListener.notify(taskEntity);
                } catch (Exception e) {
                    throw new FlowableException("Exception while invoking TaskListener: " + e.getMessage(), e);
                } finally {
                    taskEntity.setEventName(null);
                }
            }
        }
    }

    protected TaskListener createTaskListener(FlowableListener listener) {
        TaskListener taskListener = null;

        CmmnListenerFactory listenerFactory = CommandContextUtil.getCmmnEngineConfiguration().getListenerFactory();
        if (ImplementationType.IMPLEMENTATION_TYPE_CLASS.equalsIgnoreCase(listener.getImplementationType())) {
            taskListener = listenerFactory.createClassDelegateTaskListener(listener);
        } else if (ImplementationType.IMPLEMENTATION_TYPE_EXPRESSION.equalsIgnoreCase(listener.getImplementationType())) {
            taskListener = listenerFactory.createExpressionTaskListener(listener);
        } else if (ImplementationType.IMPLEMENTATION_TYPE_DELEGATEEXPRESSION.equalsIgnoreCase(listener.getImplementationType())) {
            taskListener = listenerFactory.createDelegateExpressionTaskListener(listener);
        }

        return taskListener;
    }

    protected PlanItemInstanceLifecycleListener createLifecycleListener(FlowableListener listener) {
        PlanItemInstanceLifecycleListener lifecycleListener = null;

        CmmnListenerFactory listenerFactory = CommandContextUtil.getCmmnEngineConfiguration().getListenerFactory();
        if (ImplementationType.IMPLEMENTATION_TYPE_CLASS.equalsIgnoreCase(listener.getImplementationType())) {
            lifecycleListener = listenerFactory.createClassDelegateLifeCycleListener(listener);
        } else if (ImplementationType.IMPLEMENTATION_TYPE_EXPRESSION.equalsIgnoreCase(listener.getImplementationType())) {
            lifecycleListener = listenerFactory.createExpressionLifeCycleListener(listener);
        } else if (ImplementationType.IMPLEMENTATION_TYPE_DELEGATEEXPRESSION.equalsIgnoreCase(listener.getImplementationType())) {
            lifecycleListener = listenerFactory.createDelegateExpressionLifeCycleListener(listener);
        }

        return lifecycleListener;
    }

    protected CaseInstanceLifecycleListener createCaseLifecycleListener(FlowableListener listener) {
        CaseInstanceLifecycleListener lifecycleListener = null;

        CmmnListenerFactory listenerFactory = CommandContextUtil.getCmmnEngineConfiguration().getListenerFactory();
        if (ImplementationType.IMPLEMENTATION_TYPE_CLASS.equalsIgnoreCase(listener.getImplementationType())) {
            lifecycleListener = listenerFactory.createClassDelegateCaseLifeCycleListener(listener);
        } else if (ImplementationType.IMPLEMENTATION_TYPE_EXPRESSION.equalsIgnoreCase(listener.getImplementationType())) {
            lifecycleListener = listenerFactory.createExpressionCaseLifeCycleListener(listener);
        } else if (ImplementationType.IMPLEMENTATION_TYPE_DELEGATEEXPRESSION.equalsIgnoreCase(listener.getImplementationType())) {
            lifecycleListener = listenerFactory.createDelegateExpressionCaseLifeCycleListener(listener);
        }

        return lifecycleListener;
    }

}
