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

package mobius.camel.cdi.impl;

import mobius.camel.FlowableEndpoint;
import mobius.camel.cdi.CdiCamelBehavior;

/**
 * CDI equivalent of {@link CdiCamelBehaviorCamelBodyImpl}
 * 
 * @author Zach Visagie
 */
public class CdiCamelBehaviorCamelBodyImpl extends CdiCamelBehavior {

    private static final long serialVersionUID = 1L;

    @Override
    protected void setPropertTargetVariable(FlowableEndpoint endpoint) {
        toTargetType = TargetType.BODY;
    }
}
