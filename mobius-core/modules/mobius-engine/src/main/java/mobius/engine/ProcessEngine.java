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
package mobius.engine;

import mobius.common.engine.impl.FlowableVersions;
import mobius.idm.api.Group;
import mobius.idm.api.User;
import mobius.task.api.Task;

/**
 * Provides access to all the services that expose the BPM and workflow operations.
 * 
 * <ul>
 * <li><b>{@link mobius.engine.RuntimeService}: </b> Allows the creation of {@link mobius.engine.repository.Deployment}s and the starting of and searching on
 * {@link mobius.engine.runtime.ProcessInstance}s.</li>
 * <li><b>{@link mobius.engine.TaskService}: </b> Exposes operations to manage human (standalone) {@link Task}s, such as claiming, completing and assigning tasks</li>
 * <li><b>{@link mobius.engine.IdentityService}: </b> Used for managing {@link User}s, {@link Group}s and the relations between them<</li>
 * <li><b>{@link mobius.engine.ManagementService}: </b> Exposes engine admin and maintenance operations</li>
 * <li><b>{@link mobius.engine.HistoryService}: </b> Service exposing information about ongoing and past process instances.</li>
 * </ul>
 * 
 * Typically, there will be only one central ProcessEngine instance needed in a end-user application. Building a ProcessEngine is done through a {@link ProcessEngineConfiguration} instance and is a
 * costly operation which should be avoided. For that purpose, it is advised to store it in a static field or JNDI location (or something similar). This is a thread-safe object, so no special
 * precautions need to be taken.
 * 
 *
 *
 */
public interface ProcessEngine {

    /** the version of the flowable library */
    public static String VERSION = FlowableVersions.CURRENT_VERSION;

    /**
     * The name as specified in 'process-engine-name' in the flowable.cfg.xml configuration file. The default name for a process engine is 'default
     */
    String getName();

    void close();

    RepositoryService getRepositoryService();

    RuntimeService getRuntimeService();

    FormService getFormService();

    TaskService getTaskService();

    HistoryService getHistoryService();

    IdentityService getIdentityService();

    ManagementService getManagementService();

    DynamicBpmnService getDynamicBpmnService();

    ProcessEngineConfiguration getProcessEngineConfiguration();
}
