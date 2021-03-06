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
package mobius.cmmn.test.delegate;

import mobius.cmmn.api.delegate.DelegatePlanItemInstance;
import mobius.cmmn.api.delegate.PlanItemJavaDelegate;
import mobius.common.engine.api.delegate.Expression;

/**
 *
 */
public class TestFieldsJavaDelegate implements PlanItemJavaDelegate {
    
    protected String testValue;
    protected Expression testExpression;

    @Override
    public void execute(DelegatePlanItemInstance planItemInstance) {
        planItemInstance.setVariable("testValue", testValue);
        planItemInstance.setVariable("testExpression", testExpression.getValue(planItemInstance));
    }
    
}
