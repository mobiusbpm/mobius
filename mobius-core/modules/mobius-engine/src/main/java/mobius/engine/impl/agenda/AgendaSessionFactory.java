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
package mobius.engine.impl.agenda;

import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.common.engine.impl.interceptor.Session;
import mobius.common.engine.impl.interceptor.SessionFactory;
import mobius.engine.FlowableEngineAgenda;
import mobius.engine.FlowableEngineAgendaFactory;

public class AgendaSessionFactory implements SessionFactory {
    
    protected FlowableEngineAgendaFactory flowableEngineAgendaFactory;
    
    public AgendaSessionFactory(FlowableEngineAgendaFactory flowableEngineAgendaFactory) {
        this.flowableEngineAgendaFactory = flowableEngineAgendaFactory;
    }

    @Override
    public Class<?> getSessionType() {
        return FlowableEngineAgenda.class;
    }

    @Override
    public Session openSession(CommandContext commandContext) {
        return flowableEngineAgendaFactory.createAgenda(commandContext);
    }

}
