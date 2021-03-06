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
package mobius.examples.bpmn.executionlistener;

import java.util.HashMap;
import java.util.Map;

import mobius.engine.delegate.CustomPropertiesResolver;
import mobius.engine.delegate.DelegateExecution;

/**
 * @author Yvo Swillens
 */
public class MyCustomPropertiesResolver implements CustomPropertiesResolver {

    @Override
    public Map<String, Object> getCustomPropertiesMap(DelegateExecution execution) {
        Map<String, Object> myMap = new HashMap<>();
        myMap.put("customProp1", execution.getCurrentActivityId());
        return myMap;
    }
}
