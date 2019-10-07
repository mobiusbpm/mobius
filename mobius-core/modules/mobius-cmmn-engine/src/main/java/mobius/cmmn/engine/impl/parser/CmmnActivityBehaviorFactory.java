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
import mobius.cmmn.model.CaseTask;
import mobius.cmmn.model.DecisionTask;
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

/**
 *
 */
public interface CmmnActivityBehaviorFactory {

    StageActivityBehavior createStageActivityBehavior(PlanItem planItem, Stage stage);

    MilestoneActivityBehavior createMilestoneActivityBehavior(PlanItem planItem, Milestone milestone);

    TaskActivityBehavior createTaskActivityBehavior(PlanItem planItem, Task task);

    HumanTaskActivityBehavior createHumanTaskActivityBehavior(PlanItem planItem, HumanTask humanTask);

    CaseTaskActivityBehavior createCaseTaskActivityBehavior(PlanItem planItem, CaseTask caseTask);

    ProcessTaskActivityBehavior createProcessTaskActivityBehavior(PlanItem planItem, ProcessTask processTask);

    CmmnClassDelegate createCmmnClassDelegate(PlanItem planItem, ServiceTask task);

    PlanItemExpressionActivityBehavior createPlanItemExpressionActivityBehavior(PlanItem planItem, ServiceTask task);

    PlanItemDelegateExpressionActivityBehavior createPlanItemDelegateExpressionActivityBehavior(PlanItem planItem, ServiceTask task);

    DecisionTaskActivityBehavior createDecisionTaskActivityBehavior(PlanItem planItem, DecisionTask decisionTask);

    CmmnActivityBehavior createHttpActivityBehavior(PlanItem planItem, ServiceTask task);

    TimerEventListenerActivityBehaviour createTimerEventListenerActivityBehavior(PlanItem planItem, TimerEventListener timerEventListener);
    
    ScriptTaskActivityBehavior createScriptTaskActivityBehavior(PlanItem planItem, ScriptServiceTask task);

    UserEventListenerActivityBehaviour createUserEventListenerActivityBehavior(PlanItem planItem, UserEventListener userEventListener);
    
    SignalEventListenerActivityBehaviour createSignalEventListenerActivityBehavior(PlanItem planItem, SignalEventListener signalEventListener);
    
    GenericEventListenerActivityBehaviour createGenericEventListenerActivityBehavior(PlanItem planItem, GenericEventListener genericEventListener);

}
