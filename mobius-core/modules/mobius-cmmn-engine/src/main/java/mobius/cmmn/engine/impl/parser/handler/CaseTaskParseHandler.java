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

import mobius.cmmn.engine.impl.parser.CmmnParseResult;
import mobius.cmmn.engine.impl.parser.CmmnParserImpl;
import mobius.cmmn.model.BaseElement;
import mobius.cmmn.model.CaseTask;
import mobius.cmmn.model.PlanItem;

/**
 * @author Joram Barrez
 */
public class CaseTaskParseHandler extends AbstractPlanItemParseHandler<CaseTask> {

    @Override
    public Collection<Class<? extends BaseElement>> getHandledTypes() {
        return Collections.singletonList(CaseTask.class);
    }

    @Override
    protected void executePlanItemParse(CmmnParserImpl cmmnParser, CmmnParseResult cmmnParseResult, PlanItem planItem, CaseTask caseTask) {
        planItem.setBehavior(cmmnParser.getActivityBehaviorFactory().createCaseTaskActivityBehavior(planItem, caseTask));
    }

}
