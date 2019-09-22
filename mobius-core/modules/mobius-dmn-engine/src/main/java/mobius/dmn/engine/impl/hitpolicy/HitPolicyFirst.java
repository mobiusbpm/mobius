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
package mobius.dmn.engine.impl.hitpolicy;

import mobius.dmn.model.HitPolicy;

/**
 * @author Yvo Swillens
 */
public class HitPolicyFirst extends AbstractHitPolicy implements ContinueEvaluatingBehavior {

    @Override
    public String getHitPolicyName() {
        return HitPolicy.FIRST.getValue();
    }

    @Override
    public boolean shouldContinueEvaluating(boolean ruleResult) {
        boolean shouldContinue = true;

        if (ruleResult) {
            shouldContinue = false;
        }

        return shouldContinue;
    }
}