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

package mobius.common.engine.impl.persistence;

import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.common.engine.impl.interceptor.SessionFactory;
import mobius.common.engine.api.FlowableException;
import mobius.common.engine.impl.interceptor.Session;

/**
 *
 *
 */
public class GenericManagerFactory implements SessionFactory {

    protected Class<? extends Session> typeClass;
    protected Class<? extends Session> implementationClass;

    public GenericManagerFactory(Class<? extends Session> typeClass, Class<? extends Session> implementationClass) {
        this.typeClass = typeClass;
        this.implementationClass = implementationClass;
    }

    public GenericManagerFactory(Class<? extends Session> implementationClass) {
        this(implementationClass, implementationClass);
    }

    @Override
    public Class<?> getSessionType() {
        return typeClass;
    }

    @Override
    public Session openSession(CommandContext commandContext) {
        try {
            return implementationClass.newInstance();
        } catch (Exception e) {
            throw new FlowableException("couldn't instantiate " + implementationClass.getName() + ": " + e.getMessage(), e);
        }
    }
}
