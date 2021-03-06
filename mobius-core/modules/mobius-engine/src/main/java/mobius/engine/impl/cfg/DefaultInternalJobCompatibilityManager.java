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

package mobius.engine.impl.cfg;

import mobius.common.engine.impl.interceptor.Command;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.engine.compatibility.Flowable5CompatibilityHandler;
import mobius.engine.impl.util.CommandContextUtil;
import mobius.engine.impl.util.Flowable5Util;
import mobius.job.api.Job;
import mobius.job.service.InternalJobCompatibilityManager;
import mobius.job.service.impl.persistence.entity.AbstractRuntimeJobEntity;

/**
 *
 *
 */
public class DefaultInternalJobCompatibilityManager implements InternalJobCompatibilityManager {
    
    protected ProcessEngineConfigurationImpl processEngineConfiguration;

    public DefaultInternalJobCompatibilityManager(ProcessEngineConfigurationImpl processEngineConfiguration) {
        this.processEngineConfiguration = processEngineConfiguration;
    }
    
    @Override
    public boolean isFlowable5Job(Job job) {
        if (job.getProcessDefinitionId() != null) {
            return Flowable5Util.isFlowable5ProcessDefinitionId(processEngineConfiguration, job.getProcessDefinitionId());
        } 
        return false;
    }

    @Override
    public void executeV5Job(Job job) {
        Flowable5CompatibilityHandler compatibilityHandler = Flowable5Util.getFlowable5CompatibilityHandler();
        compatibilityHandler.executeJob(job);
    }

    @Override
    public void executeV5JobWithLockAndRetry(final Job job) {
        processEngineConfiguration.getCommandExecutor().execute(new Command<Void>() {
            @Override
            public Void execute(CommandContext commandContext) {
                CommandContextUtil.getProcessEngineConfiguration(commandContext).getFlowable5CompatibilityHandler().executeJobWithLockAndRetry(job);
                return null;
            }
        });
    }
    
    @Override
    public void deleteV5Job(String jobId) {
        Flowable5CompatibilityHandler compatibilityHandler = Flowable5Util.getFlowable5CompatibilityHandler();
        compatibilityHandler.deleteJob(jobId);
    }
    
    @Override
    public void handleFailedV5Job(AbstractRuntimeJobEntity job, Throwable exception) {
        Flowable5CompatibilityHandler compatibilityHandler = Flowable5Util.getFlowable5CompatibilityHandler();
        compatibilityHandler.handleFailedJob(job, exception);
    }
    
}
