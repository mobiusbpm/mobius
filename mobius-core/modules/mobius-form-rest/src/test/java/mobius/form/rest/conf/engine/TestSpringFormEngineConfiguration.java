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
package mobius.form.rest.conf.engine;

import mobius.common.engine.impl.EngineConfigurator;
import mobius.form.engine.FormEngineConfiguration;
import mobius.form.spring.SpringFormEngineConfiguration;
import mobius.idm.engine.configurator.IdmEngineConfigurator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestSpringFormEngineConfiguration extends SpringFormEngineConfiguration {

    protected boolean disableIdmEngine;

    public boolean isDisableIdmEngine() {
        return disableIdmEngine;
    }

    public FormEngineConfiguration setDisableIdmEngine(boolean disableIdmEngine) {
        this.disableIdmEngine = disableIdmEngine;
        return this;
    }

    @Override
    protected List<EngineConfigurator> getEngineSpecificEngineConfigurators() {
        if (!disableIdmEngine) {
            List<EngineConfigurator> specificConfigurators = new ArrayList<>();
            if (idmEngineConfigurator != null) {
                specificConfigurators.add(idmEngineConfigurator);
            } else {
                specificConfigurators.add(new IdmEngineConfigurator());
            }
            return specificConfigurators;
        }
        return Collections.emptyList();
    }
}
