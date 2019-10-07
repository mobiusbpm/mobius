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
package mobius.cmmn.engine.impl.parser.handler;

import java.util.Collection;
import java.util.Collections;

import org.apache.commons.lang3.StringUtils;
import mobius.cmmn.engine.impl.parser.CmmnActivityBehaviorFactory;
import mobius.cmmn.engine.impl.parser.CmmnParseResult;
import mobius.cmmn.engine.impl.parser.CmmnParserImpl;
import mobius.cmmn.model.BaseElement;
import mobius.cmmn.model.HttpServiceTask;
import mobius.cmmn.model.ImplementationType;
import mobius.cmmn.model.PlanItem;
import mobius.cmmn.model.ServiceTask;

/**
 *
 */
public class ServiceTaskParseHandler extends AbstractPlanItemParseHandler<ServiceTask> {

    @Override
    public Collection<Class<? extends BaseElement>> getHandledTypes() {
        return Collections.singletonList(ServiceTask.class);
    }

    @Override
    protected void executePlanItemParse(CmmnParserImpl cmmnParser, CmmnParseResult cmmnParseResult, PlanItem planItem, ServiceTask serviceTask) {
        CmmnActivityBehaviorFactory activityBehaviorFactory = cmmnParser.getActivityBehaviorFactory();
        switch (serviceTask.getType()) {
            case HttpServiceTask.HTTP_TASK:
                planItem.setBehavior(activityBehaviorFactory.createHttpActivityBehavior(planItem, serviceTask));
                break;
            default:
                // java task type was not set in the version <= 6.2.0 that's why we have to assume that default service task type is java
                if (StringUtils.isNotEmpty(serviceTask.getImplementation())) {
                    if (ImplementationType.IMPLEMENTATION_TYPE_CLASS.equals(serviceTask.getImplementationType())) {
                        planItem.setBehavior(activityBehaviorFactory.createCmmnClassDelegate(planItem, serviceTask));

                    } else if (ImplementationType.IMPLEMENTATION_TYPE_EXPRESSION.equals(serviceTask.getImplementationType())) {
                        planItem.setBehavior(activityBehaviorFactory.createPlanItemExpressionActivityBehavior(planItem, serviceTask));

                    } else if (ImplementationType.IMPLEMENTATION_TYPE_DELEGATEEXPRESSION.equals(serviceTask.getImplementationType())) {
                        planItem.setBehavior(activityBehaviorFactory.createPlanItemDelegateExpressionActivityBehavior(planItem, serviceTask));
                    }
                }
                break;
        }
    }

}
