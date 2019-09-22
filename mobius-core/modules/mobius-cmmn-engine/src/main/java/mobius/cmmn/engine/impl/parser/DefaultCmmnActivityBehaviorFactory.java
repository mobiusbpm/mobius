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
package mobius.cmmn.engine.impl.parser;

import org.apache.commons.lang3.StringUtils;
import mobius.cmmn.engine.impl.behavior.CmmnActivityBehavior;
import mobius.cmmn.engine.impl.behavior.impl.CaseTaskActivityBehavior;
import mobius.cmmn.engine.impl.behavior.impl.DecisionTaskActivityBehavior;
import mobius.cmmn.engine.impl.behavior.impl.GenericEventListenerActivityBehaviour;
import mobius.cmmn.engine.impl.behavior.impl.HumanTaskActivityBehavior;
import mobius.cmmn.engine.impl.behavior.impl.MilestoneActivityBehavior;
import mobius.cmmn.engine.impl.behavior.impl.PlanItemDelegateExpressionActivityBehavior;
import mobius.cmmn.engine.impl.behavior.impl.PlanItemExpressionActivityBehavior;
import mobius.cmmn.engine.impl.behavior.impl.ProcessTaskActivityBehavior;
import mobius.cmmn.engine.impl.behavior.impl.ScriptTaskActivityBehavior;
import mobius.cmmn.engine.impl.behavior.impl.SignalEventListenerActivityBehaviour;
import mobius.cmmn.engine.impl.behavior.impl.StageActivityBehavior;
import mobius.cmmn.engine.impl.behavior.impl.TaskActivityBehavior;
import mobius.cmmn.engine.impl.behavior.impl.TimerEventListenerActivityBehaviour;
import mobius.cmmn.engine.impl.behavior.impl.UserEventListenerActivityBehaviour;
import mobius.cmmn.engine.impl.delegate.CmmnClassDelegate;
import mobius.cmmn.engine.impl.delegate.CmmnClassDelegateFactory;
import mobius.cmmn.model.CaseTask;
import mobius.cmmn.model.DecisionTask;
import mobius.cmmn.model.FieldExtension;
import mobius.cmmn.model.GenericEventListener;
import mobius.cmmn.model.HumanTask;
import mobius.cmmn.model.Milestone;
import mobius.cmmn.model.PlanItem;
import mobius.cmmn.model.ProcessTask;
import mobius.cmmn.model.ScriptServiceTask;
import mobius.cmmn.model.ServiceTask;
import mobius.cmmn.model.SignalEventListener;
import mobius.cmmn.model.Stage;
import mobius.cmmn.model.Task;
import mobius.cmmn.model.TimerEventListener;
import mobius.cmmn.model.UserEventListener;
import mobius.common.engine.api.FlowableException;
import mobius.common.engine.api.delegate.Expression;
import mobius.common.engine.impl.el.ExpressionManager;

/**
 * @author Joram Barrez
 */
public class DefaultCmmnActivityBehaviorFactory implements CmmnActivityBehaviorFactory {

    protected CmmnClassDelegateFactory classDelegateFactory;
    protected ExpressionManager expressionManager;

    @Override
    public StageActivityBehavior createStageActivityBehavior(PlanItem planItem, Stage stage) {
        return new StageActivityBehavior(stage);
    }

    @Override
    public MilestoneActivityBehavior createMilestoneActivityBehavior(PlanItem planItem, Milestone milestone) {
        String name = null;
        if (!StringUtils.isEmpty(planItem.getName())) {
            name = planItem.getName();
        } else if (StringUtils.isNotEmpty(milestone.getName())) {
            name = milestone.getName();
        }
        return new MilestoneActivityBehavior(expressionManager.createExpression(name));
    }

    @Override
    public TaskActivityBehavior createTaskActivityBehavior(PlanItem planItem, Task task) {
        return new TaskActivityBehavior(task.isBlocking(), task.getBlockingExpression());
    }

    @Override
    public HumanTaskActivityBehavior createHumanTaskActivityBehavior(PlanItem planItem, HumanTask humanTask) {
        return new HumanTaskActivityBehavior(humanTask);
    }

    @Override
    public CaseTaskActivityBehavior createCaseTaskActivityBehavior(PlanItem planItem, CaseTask caseTask) {
        return new CaseTaskActivityBehavior(expressionManager.createExpression(caseTask.getCaseRef()), caseTask);
    }

    @Override
    public ProcessTaskActivityBehavior createProcessTaskActivityBehavior(PlanItem planItem, ProcessTask processTask) {
        Expression processRefExpression = createExpression(processTask.getProcessRefExpression());
        return new ProcessTaskActivityBehavior(processTask.getProcess(), processRefExpression, processTask);
    }

    @Override
    public CmmnClassDelegate createCmmnClassDelegate(PlanItem planItem, ServiceTask task) {
        return classDelegateFactory.create(task.getImplementation(), task.getFieldExtensions());
    }

    @Override
    public PlanItemExpressionActivityBehavior createPlanItemExpressionActivityBehavior(PlanItem planItem, ServiceTask task) {
        return new PlanItemExpressionActivityBehavior(task.getImplementation(), task.getResultVariableName());
    }

    @Override
    public PlanItemDelegateExpressionActivityBehavior createPlanItemDelegateExpressionActivityBehavior(PlanItem planItem, ServiceTask task) {
        return new PlanItemDelegateExpressionActivityBehavior(task.getImplementation(), task.getFieldExtensions());
    }

    @Override
    public TimerEventListenerActivityBehaviour createTimerEventListenerActivityBehavior(PlanItem planItem, TimerEventListener timerEventListener) {
        return new TimerEventListenerActivityBehaviour(timerEventListener);
    }

    @Override
    public UserEventListenerActivityBehaviour createUserEventListenerActivityBehavior(PlanItem planItem, UserEventListener userEventListener) {
        return new UserEventListenerActivityBehaviour(userEventListener);
    }
    
    @Override
    public SignalEventListenerActivityBehaviour createSignalEventListenerActivityBehavior(PlanItem planItem, SignalEventListener signalEventListener) {
        return new SignalEventListenerActivityBehaviour(signalEventListener);
    }
    
    @Override
    public GenericEventListenerActivityBehaviour createGenericEventListenerActivityBehavior(PlanItem planItem, GenericEventListener genericEventListener) {
        return new GenericEventListenerActivityBehaviour();
    }

    @Override
    public DecisionTaskActivityBehavior createDecisionTaskActivityBehavior(PlanItem planItem, DecisionTask decisionTask) {
        return new DecisionTaskActivityBehavior(createExpression(decisionTask.getDecisionRefExpression()), decisionTask);
    }

    @Override
    public CmmnActivityBehavior createHttpActivityBehavior(PlanItem planItem, ServiceTask task) {
        try {
            Class<?> theClass = null;
            FieldExtension behaviorExtension = null;
            for (FieldExtension fieldExtension : task.getFieldExtensions()) {
                if ("httpActivityBehaviorClass".equals(fieldExtension.getFieldName()) && StringUtils.isNotEmpty(fieldExtension.getStringValue())) {
                    theClass = Class.forName(fieldExtension.getStringValue());
                    behaviorExtension = fieldExtension;
                    break;
                }
            }

            if (behaviorExtension != null) {
                task.getFieldExtensions().remove(behaviorExtension);
            }

            // Default Http behavior class
            if (theClass == null) {
                theClass = Class.forName("mobius.http.cmmn.impl.CmmnHttpActivityBehaviorImpl");
            }

            return (CmmnActivityBehavior) classDelegateFactory.defaultInstantiateDelegate(theClass, task);

        } catch (ClassNotFoundException e) {
            throw new FlowableException("Could not find mobius.http.HttpActivityBehavior: ", e);
        }
    }

    @Override
    public ScriptTaskActivityBehavior createScriptTaskActivityBehavior(PlanItem planItem, ScriptServiceTask task) {
        return new ScriptTaskActivityBehavior(task);
    }

    public void setClassDelegateFactory(CmmnClassDelegateFactory classDelegateFactory) {
        this.classDelegateFactory = classDelegateFactory;
    }

    public ExpressionManager getExpressionManager() {
        return expressionManager;
    }

    public void setExpressionManager(ExpressionManager expressionManager) {
        this.expressionManager = expressionManager;
    }

    protected Expression createExpression(String refExpressionString) {
        Expression processRefExpression = null;
        if (StringUtils.isNotEmpty(refExpressionString)) {
            processRefExpression = expressionManager.createExpression(refExpressionString);
        }
        return processRefExpression;
    }

}
