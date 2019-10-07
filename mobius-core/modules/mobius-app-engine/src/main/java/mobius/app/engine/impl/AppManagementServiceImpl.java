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
package mobius.app.engine.impl;

import java.util.Collection;
import java.util.Map;

import mobius.app.engine.impl.cmd.GetTableCountsCmd;
import mobius.app.engine.impl.cmd.GetTableNamesCmd;
import mobius.app.api.AppManagementService;
import mobius.app.engine.AppEngineConfiguration;
import mobius.common.engine.impl.service.CommonEngineServiceImpl;

/**
 *
 */
public class AppManagementServiceImpl extends CommonEngineServiceImpl<AppEngineConfiguration> implements AppManagementService {
    
    public AppManagementServiceImpl(AppEngineConfiguration engineConfiguration) {
        super(engineConfiguration);
    }

    @Override
    public Map<String, Long> getTableCounts() {
        return commandExecutor.execute(new GetTableCountsCmd());
    }

    @Override
    public Collection<String> getTableNames() {
        return commandExecutor.execute(new GetTableNamesCmd());
    }
    
}
