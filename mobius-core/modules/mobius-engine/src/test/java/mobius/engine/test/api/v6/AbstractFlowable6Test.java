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
package mobius.engine.test.api.v6;

import java.sql.SQLException;
import java.util.List;

import mobius.common.engine.impl.EngineConfigurator;
import mobius.engine.FormService;
import mobius.engine.HistoryService;
import mobius.engine.ManagementService;
import mobius.engine.ProcessEngine;
import mobius.engine.ProcessEngineConfiguration;
import mobius.engine.ProcessEngineLifecycleListener;
import mobius.engine.RepositoryService;
import mobius.engine.RuntimeService;
import mobius.engine.TaskService;
import mobius.engine.impl.ProcessEngineImpl;
import mobius.engine.impl.cfg.ProcessEngineConfigurationImpl;
import mobius.engine.test.FlowableRule;
import mobius.engine.test.impl.logger.ProcessExecutionLoggerConfigurator;
import org.h2.tools.Server;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parent class for internal Flowable tests.
 * 
 * Boots up a process engine and caches it.
 * 
 * When using H2 and the default schema name, it will also boot the H2 webapp (reachable with browser on http://localhost:8082/)
 * 
 * @author Joram Barrez
 */
public class AbstractFlowable6Test {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractFlowable6Test.class);

    public static final String H2_TEST_JDBC_URL = "jdbc:h2:mem:activiti;DB_CLOSE_DELAY=1000";

    @Rule
    public FlowableRule activitiRule = new FlowableRule();

    protected static ProcessEngine cachedProcessEngine;
    protected ProcessEngineConfigurationImpl processEngineConfiguration;
    protected RepositoryService repositoryService;
    protected RuntimeService runtimeService;
    protected TaskService taskService;
    protected FormService formService;
    protected HistoryService historyService;
    protected ManagementService managementService;

    @Before
    public void initProcessEngine() {
        if (cachedProcessEngine == null) {
            cachedProcessEngine = activitiRule.getProcessEngine();

            // Boot up H2 webapp
            if (cachedProcessEngine instanceof ProcessEngineImpl) {
                if (((ProcessEngineImpl) cachedProcessEngine).getProcessEngineConfiguration().getJdbcUrl().equals(H2_TEST_JDBC_URL)) {
                    initializeH2WebApp(cachedProcessEngine);
                }
            }
        }

        this.processEngineConfiguration = (ProcessEngineConfigurationImpl) cachedProcessEngine.getProcessEngineConfiguration();
        this.repositoryService = cachedProcessEngine.getRepositoryService();
        this.runtimeService = cachedProcessEngine.getRuntimeService();
        this.taskService = cachedProcessEngine.getTaskService();
        this.formService = cachedProcessEngine.getFormService();
        this.historyService = cachedProcessEngine.getHistoryService();
        this.managementService = cachedProcessEngine.getManagementService();
    }

    @After
    public void resetClock() {
        activitiRule.getProcessEngine().getProcessEngineConfiguration().getClock().reset();
    }

    @After
    public void logCommandInvokerDebugInfo() {

        ProcessExecutionLoggerConfigurator loggerConfigurator = null;
        List<EngineConfigurator> configurators = ((ProcessEngineImpl) cachedProcessEngine).getProcessEngineConfiguration().getConfigurators();
        if (configurators != null && configurators.size() > 0) {
            for (EngineConfigurator configurator : configurators) {
                if (configurator instanceof ProcessExecutionLoggerConfigurator) {
                    loggerConfigurator = (ProcessExecutionLoggerConfigurator) configurator;
                    break;
                }
            }

            if (loggerConfigurator != null) {
                loggerConfigurator.getProcessExecutionLogger().logDebugInfo(true);
            }
        }
    }

    protected void initializeH2WebApp(ProcessEngine processEngine) {
        try {
            final Server server = Server.createWebServer("-web");

            // Shutdown hook
            final ProcessEngineConfiguration processEngineConfiguration = ((ProcessEngineImpl) processEngine).getProcessEngineConfiguration();
            final ProcessEngineLifecycleListener originalLifecycleListener = processEngineConfiguration.getProcessEngineLifecycleListener();
            processEngineConfiguration.setProcessEngineLifecycleListener(new ProcessEngineLifecycleListener() {

                @Override
                public void onProcessEngineClosed(ProcessEngine processEngine) {
                    server.stop();
                    originalLifecycleListener.onProcessEngineClosed(processEngine);
                }

                @Override
                public void onProcessEngineBuilt(ProcessEngine processEngine) {
                    originalLifecycleListener.onProcessEngineBuilt(processEngine);
                }

            });

            // Actually start the web server
            server.start();

        } catch (SQLException e) {
            LOGGER.warn("Could not start H2 webapp", e);
        }
    }

}
