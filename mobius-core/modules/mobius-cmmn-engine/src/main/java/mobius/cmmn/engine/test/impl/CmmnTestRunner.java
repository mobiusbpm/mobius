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
package mobius.cmmn.engine.test.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import mobius.cmmn.api.CmmnRepositoryService;
import mobius.cmmn.api.repository.CmmnDeploymentBuilder;
import mobius.cmmn.engine.CmmnEngineConfiguration;
import mobius.cmmn.engine.impl.deployer.CmmnDeployer;
import mobius.cmmn.engine.test.CmmnDeployment;
import mobius.common.engine.api.FlowableException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.MultipleFailureException;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class CmmnTestRunner extends BlockJUnit4ClassRunner {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CmmnTestRunner.class);
    
    protected static CmmnEngineConfiguration cmmnEngineConfiguration;
    protected static String deploymentId;

    public CmmnTestRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }
    
    public static CmmnEngineConfiguration getCmmnEngineConfiguration() {
        return CmmnTestRunner.cmmnEngineConfiguration;
    }

    public static void setCmmnEngineConfiguration(CmmnEngineConfiguration cmmnEngineConfiguration) {
        CmmnTestRunner.cmmnEngineConfiguration = cmmnEngineConfiguration;
    }
    
    @Override
    protected void runChild(FrameworkMethod method, RunNotifier notifier) {
        super.runChild(method, notifier);
        
    }

    @Override
    protected Statement withBefores(FrameworkMethod method, Object target, Statement statement) {
        if (method.getAnnotation(Ignore.class) == null && method.getAnnotation(CmmnDeployment.class) != null) {

            List<FrameworkMethod> befores = getTestClass().getAnnotatedMethods(Before.class);

            return new Statement() {

                @Override
                public void evaluate() throws Throwable {
                    for (FrameworkMethod before : befores) {
                        before.invokeExplosively(target);
                    }
                    deploymentId = deployCmmnDefinition(method);
                    statement.evaluate();
                }

            };
        } else {
            return super.withBefores(method, target, statement);
        }

    }

    @Override
    protected Statement withAfters(FrameworkMethod method, Object target, Statement statement) {
        List<FrameworkMethod> afters = getTestClass().getAnnotatedMethods(After.class);

        return new Statement() {

            @Override
            public void evaluate() throws Throwable {
                List<Throwable> errors = new ArrayList<>();
                try {
                    statement.evaluate();
                } catch (Throwable e) {
                    errors.add(e);
                } finally {

                    if (deploymentId != null) {
                        deleteDeployment(deploymentId);
                        deploymentId = null;
                    }

                    for (FrameworkMethod each : afters) {
                        try {
                            each.invokeExplosively(target);
                        } catch (Throwable e) {
                            errors.add(e);
                        }
                    }

                    if (errors == null || errors.isEmpty()) {
                        assertDatabaseEmpty(method);
                    }

                }
                MultipleFailureException.assertEmpty(errors);
            }

        };
    }


    protected String deployCmmnDefinition(FrameworkMethod method) {
        try {
            LOGGER.debug("annotation @CmmnDeployment creates deployment for {}.{}", method.getMethod().getDeclaringClass().getSimpleName(), method.getName());
    
            if (cmmnEngineConfiguration == null) {
                throw new FlowableException("No cached CMMN engine found.");
            }
            CmmnRepositoryService repositoryService = cmmnEngineConfiguration.getCmmnRepositoryService();
            CmmnDeploymentBuilder deploymentBuilder = repositoryService
                    .createDeployment()
                    .name(method.getMethod().getDeclaringClass().getSimpleName() + "." + method.getName());
            
            CmmnDeployment deploymentAnnotation = method.getAnnotation(CmmnDeployment.class);
            String[] resources = deploymentAnnotation.resources();
            
            if (resources.length == 0) {
                resources = new String[] { getCmmnDefinitionResource(method) };
            }
    
            for (String resource : resources) {
                deploymentBuilder.addClasspathResource(resource);
            }
            
            if (StringUtils.isNotEmpty(deploymentAnnotation.tenantId())) {
                deploymentBuilder.tenantId(deploymentAnnotation.tenantId());
            }
            
            return deploymentBuilder.deploy().getId();
            
        } catch (Exception e) {
            throw new FlowableException("Error while deploying case definition", e);
        }
    }
    
    protected String getCmmnDefinitionResource(FrameworkMethod method) {
        String className = method.getMethod().getDeclaringClass().getName().replace('.', '/');
        String methodName = method.getName();
        for (String suffix : CmmnDeployer.CMMN_RESOURCE_SUFFIXES) {
            String resource = className + "." + methodName + suffix;
            if (CmmnTestRunner.class.getClassLoader().getResource(resource) != null) {
                return resource;
            }
        }
        return className + "." + method.getName() + ".cmmn";
    }
    
    protected void deleteDeployment(String deploymentId) {
        cmmnEngineConfiguration.getCmmnRepositoryService().deleteDeployment(deploymentId, true);
    }
    
    protected void assertDatabaseEmpty(FrameworkMethod method) {
        Map<String, Long> tableCounts = cmmnEngineConfiguration.getCmmnManagementService().getTableCounts();
        
        StringBuilder outputMessage = new StringBuilder();
        for (String table : tableCounts.keySet()) {
            long count = tableCounts.get(table);
            if (count != 0) {
                outputMessage.append("  ").append(table).append(": ").append(count).append(" record(s) ");
            }
        }
        
        if (outputMessage.length() > 0) {
            outputMessage.insert(0, "DB not clean for test " + getTestClass().getName() + "." + method.getName() + ": \n");
            LOGGER.error("\n");
            LOGGER.error(outputMessage.toString());
            Assert.fail(outputMessage.toString());

        } else {
            LOGGER.info("database was clean");
            
        }
    }

}
