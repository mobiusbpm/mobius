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
package mobius.cmmn.api.runtime;

import mobius.cmmn.model.CaseTask;
import mobius.cmmn.model.DecisionTask;
import mobius.cmmn.model.GenericEventListener;
import mobius.cmmn.model.HttpServiceTask;
import mobius.cmmn.model.HumanTask;
import mobius.cmmn.model.Milestone;
import mobius.cmmn.model.PlanFragment;
import mobius.cmmn.model.ProcessTask;
import mobius.cmmn.model.ServiceTask;
import mobius.cmmn.model.SignalEventListener;
import mobius.cmmn.model.Stage;
import mobius.cmmn.model.TimerEventListener;
import mobius.cmmn.model.UserEventListener;

/**
 *
 */
public interface PlanItemDefinitionType {

    String STAGE = Stage.class.getSimpleName().toLowerCase();
    
    String PLAN_FRAGMENT = PlanFragment.class.getSimpleName().toLowerCase();
    
    String MILESTONE = Milestone.class.getSimpleName().toLowerCase();

    String TIMER_EVENT_LISTENER = TimerEventListener.class.getSimpleName().toLowerCase();

    String USER_EVENT_LISTENER = UserEventListener.class.getSimpleName().toLowerCase();
    
    String SIGNAL_EVENT_LISTENER = SignalEventListener.class.getSimpleName().toLowerCase();
    
    String GENERIC_EVENT_LISTENER = GenericEventListener.class.getSimpleName().toLowerCase();
    
    String HUMAN_TASK = HumanTask.class.getSimpleName().toLowerCase();
    
    String CASE_TASK = CaseTask.class.getSimpleName().toLowerCase();
    
    String PROCESS_TASK = ProcessTask.class.getSimpleName().toLowerCase();
    
    String DECISION_TASK = DecisionTask.class.getSimpleName().toLowerCase();
    
    String SERVICE_TASK = ServiceTask.class.getSimpleName().toLowerCase();
    
    String HTTP_SERVICE_TASK = HttpServiceTask.class.getSimpleName().toLowerCase();

}
