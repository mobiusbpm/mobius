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
package mobius.dmn.engine.impl;

import mobius.common.engine.impl.service.CommonEngineServiceImpl;
import mobius.dmn.api.DmnHistoricDecisionExecutionQuery;
import mobius.dmn.api.DmnHistoryService;
import mobius.dmn.api.NativeHistoricDecisionExecutionQuery;
import mobius.dmn.engine.DmnEngineConfiguration;

/**
 *
 */
public class DmnHistoryServiceImpl extends CommonEngineServiceImpl<DmnEngineConfiguration> implements DmnHistoryService {

    @Override
    public DmnHistoricDecisionExecutionQuery createHistoricDecisionExecutionQuery() {
        return new HistoricDecisionExecutionQueryImpl(commandExecutor);
    }

    @Override
    public NativeHistoricDecisionExecutionQuery createNativeHistoricDecisionExecutionQuery() {
        return new NativeHistoryDecisionExecutionQueryImpl(commandExecutor);
    }
}
