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
package mobius.app.engine;

import com.fasterxml.jackson.databind.ObjectMapper;
import mobius.app.api.AppEngineConfigurationApi;
import mobius.app.api.AppManagementService;
import mobius.app.api.AppRepositoryService;
import mobius.app.api.repository.AppResourceConverter;
import mobius.app.engine.impl.AppEngineImpl;
import mobius.app.engine.impl.AppManagementServiceImpl;
import mobius.app.engine.impl.AppRepositoryServiceImpl;
import mobius.app.engine.impl.cfg.StandaloneInMemAppEngineConfiguration;
import mobius.app.engine.impl.cmd.SchemaOperationsAppEngineBuild;
import mobius.app.engine.impl.db.AppDbSchemaManager;
import mobius.app.engine.impl.db.EntityDependencyOrder;
import mobius.app.engine.impl.deployer.AppDeployer;
import mobius.app.engine.impl.deployer.AppDeploymentManager;
import mobius.app.engine.impl.deployer.AppResourceConverterImpl;
import mobius.app.engine.impl.el.AppExpressionManager;
import mobius.app.engine.impl.interceptor.AppCommandInvoker;
import mobius.app.engine.impl.persistence.entity.*;
import mobius.app.engine.impl.persistence.entity.data.AppDefinitionDataManager;
import mobius.app.engine.impl.persistence.entity.data.AppDeploymentDataManager;
import mobius.app.engine.impl.persistence.entity.data.AppResourceDataManager;
import mobius.app.engine.impl.persistence.entity.data.TableDataManager;
import mobius.app.engine.impl.persistence.entity.data.impl.MybatisAppDefinitionDataManager;
import mobius.app.engine.impl.persistence.entity.data.impl.MybatisAppDeploymentDataManager;
import mobius.app.engine.impl.persistence.entity.data.impl.MybatisResourceDataManager;
import mobius.app.engine.impl.persistence.entity.data.impl.TableDataManagerImpl;
import mobius.app.engine.impl.persistence.entity.deploy.AppDefinitionCacheEntry;
import mobius.common.engine.api.scope.ScopeTypes;
import mobius.common.engine.impl.*;
import mobius.common.engine.impl.calendar.*;
import mobius.common.engine.impl.cfg.BeansConfigurationHelper;
import mobius.common.engine.impl.db.SchemaManager;
import mobius.common.engine.impl.el.ExpressionManager;
import mobius.common.engine.impl.interceptor.CommandInterceptor;
import mobius.common.engine.impl.interceptor.EngineConfigurationConstants;
import mobius.common.engine.impl.persistence.deploy.DefaultDeploymentCache;
import mobius.common.engine.impl.persistence.deploy.DeploymentCache;
import mobius.identitylink.service.IdentityLinkServiceConfiguration;
import mobius.identitylink.service.impl.db.IdentityLinkDbSchemaManager;
import mobius.idm.api.IdmEngineConfigurationApi;
import mobius.idm.api.IdmIdentityService;
import mobius.idm.engine.configurator.IdmEngineConfigurator;
import mobius.variable.api.types.VariableType;
import mobius.variable.api.types.VariableTypes;
import mobius.variable.service.VariableServiceConfiguration;
import mobius.variable.service.impl.db.IbatisVariableTypeHandler;
import mobius.variable.service.impl.db.VariableDbSchemaManager;
import mobius.variable.service.impl.types.*;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class AppEngineConfiguration extends AbstractEngineConfiguration implements
        AppEngineConfigurationApi, HasExpressionManagerEngineConfiguration, HasVariableTypes {

    public static final String DEFAULT_MYBATIS_MAPPING_FILE = "mobius/db/mapping/mappings.xml";
    public static final String LIQUIBASE_CHANGELOG_PREFIX = "ACT_APP_";

    protected String cmmnEngineName = AppEngines.NAME_DEFAULT;

    protected AppManagementService appManagementService = new AppManagementServiceImpl(this);
    protected AppRepositoryService appRepositoryService = new AppRepositoryServiceImpl(this);
    
    protected TableDataManager tableDataManager;
    protected AppDeploymentDataManager deploymentDataManager;
    protected AppResourceDataManager resourceDataManager;
    protected AppDefinitionDataManager appDefinitionDataManager;

    protected AppDeploymentEntityManager appDeploymentEntityManager;
    protected AppResourceEntityManager appResourceEntityManager;
    protected AppDefinitionEntityManager appDefinitionEntityManager;

    protected boolean disableIdmEngine;

    protected boolean executeServiceSchemaManagers = true;
    
    protected AppDeployer appDeployer;
    protected AppDeploymentManager deploymentManager;
    protected AppResourceConverter appResourceConverter;
    
    protected int appDefinitionCacheLimit = -1;
    protected DeploymentCache<AppDefinitionCacheEntry> appDefinitionCache;

    protected ExpressionManager expressionManager;
    protected SchemaManager identityLinkSchemaManager;
    protected SchemaManager variableSchemaManager;

    // Identitylink support
    protected IdentityLinkServiceConfiguration identityLinkServiceConfiguration;

    // Variable support
    protected VariableTypes variableTypes;
    protected List<VariableType> customPreVariableTypes;
    protected List<VariableType> customPostVariableTypes;
    protected VariableServiceConfiguration variableServiceConfiguration;
    protected boolean serializableVariableTypeTrackDeserializedObjects = true;
    protected ObjectMapper objectMapper = new ObjectMapper();

    protected BusinessCalendarManager businessCalendarManager;

    public static AppEngineConfiguration createAppEngineConfigurationFromResourceDefault() {
        return createAppEngineConfigurationFromResource("flowable.app.cfg.xml", "appEngineConfiguration");
    }

    public static AppEngineConfiguration createAppEngineConfigurationFromResource(String resource) {
        return createAppEngineConfigurationFromResource(resource, "appEngineConfiguration");
    }

    public static AppEngineConfiguration createAppEngineConfigurationFromResource(String resource, String beanName) {
        return (AppEngineConfiguration) BeansConfigurationHelper.parseEngineConfigurationFromResource(resource, beanName);
    }

    public static AppEngineConfiguration createAppEngineConfigurationFromInputStream(InputStream inputStream) {
        return createAppEngineConfigurationFromInputStream(inputStream, "appEngineConfiguration");
    }

    public static AppEngineConfiguration createAppEngineConfigurationFromInputStream(InputStream inputStream, String beanName) {
        return (AppEngineConfiguration) BeansConfigurationHelper.parseEngineConfigurationFromInputStream(inputStream, beanName);
    }

    public static AppEngineConfiguration createStandaloneAppEngineConfiguration() {
        return new AppEngineConfiguration();
    }

    public static AppEngineConfiguration createStandaloneInMemAppEngineConfiguration() {
        return new StandaloneInMemAppEngineConfiguration();
    }

    public AppEngine buildAppEngine() {
        init();
        return new AppEngineImpl(this);
    }

    protected void init() {
        initEngineConfigurations();
        initConfigurators();
        configuratorsBeforeInit();
        initCommandContextFactory();
        initTransactionContextFactory();
        initCommandExecutors();
        initIdGenerator();
        initExpressionManager();
        
        if (usingRelationalDatabase) {
            initDataSource();
        }
        
        if (usingRelationalDatabase || usingSchemaMgmt) {
            initSchemaManager();
            initSchemaManagementCommand();
        }

        initVariableTypes();
        initBeans();
        initTransactionFactory();

        if (usingRelationalDatabase) {
            initSqlSessionFactory();
        }

        initSessionFactories();
        initServices();
        initDataManagers();
        initEntityManagers();
        initDeployers();
        initAppDefinitionCache();
        initAppResourceConverter();
        initDeploymentManager();
        initClock();
        initIdentityLinkServiceConfiguration();
        initVariableServiceConfiguration();
        configuratorsAfterInit();
        initBusinessCalendarManager();
    }

    @Override
    public void initSchemaManager() {
        super.initSchemaManager();
        initAppSchemaManager();

        if (executeServiceSchemaManagers) {
            initIdentityLinkSchemaManager();
            initVariableSchemaManager();
        }
    }
    
    public void initSchemaManagementCommand() {
        if (schemaManagementCmd == null) {
            if (usingRelationalDatabase && databaseSchemaUpdate != null) {
                this.schemaManagementCmd = new SchemaOperationsAppEngineBuild();
            }
        }
    }

    protected void initAppSchemaManager() {
        if (this.schemaManager == null) {
            this.schemaManager = new AppDbSchemaManager();
        }
    }

    protected void initVariableSchemaManager() {
        if (this.variableSchemaManager == null) {
            this.variableSchemaManager = new VariableDbSchemaManager();
        }
    }

    protected void initIdentityLinkSchemaManager() {
        if (this.identityLinkSchemaManager == null) {
            this.identityLinkSchemaManager = new IdentityLinkDbSchemaManager();
        }
    }

    @Override
    public void initMybatisTypeHandlers(Configuration configuration) {
        configuration.getTypeHandlerRegistry().register(VariableType.class, JdbcType.VARCHAR, new IbatisVariableTypeHandler(variableTypes));
    }

    public void initExpressionManager() {
        if (expressionManager == null) {
            expressionManager = new AppExpressionManager(beans);
        }
    }

    @Override
    public void initCommandInvoker() {
        if (commandInvoker == null) {
            commandInvoker = new AppCommandInvoker();
        }
    }

    protected void initServices() {
        initService(appManagementService);
        initService(appRepositoryService);
    }

    public void initDataManagers() {
        if (tableDataManager == null) {
            tableDataManager = new TableDataManagerImpl();
        }
        if (deploymentDataManager == null) {
            deploymentDataManager = new MybatisAppDeploymentDataManager(this);
        }
        if (resourceDataManager == null) {
            resourceDataManager = new MybatisResourceDataManager(this);
        }
        if (appDefinitionDataManager == null) {
            appDefinitionDataManager = new MybatisAppDefinitionDataManager(this);
        }
    }

    public void initEntityManagers() {
        if (appDeploymentEntityManager == null) {
            appDeploymentEntityManager = new AppDeploymentEntityManagerImpl(this, deploymentDataManager);
        }
        if (appResourceEntityManager == null) {
            appResourceEntityManager = new AppResourceEntityManagerImpl(this, resourceDataManager);
        }
        if (appDefinitionEntityManager == null) {
            appDefinitionEntityManager = new AppDefinitionEntityManagerImpl(this, appDefinitionDataManager);
        }
    }

    protected void initDeployers() {
        if (this.appDeployer == null) {
            this.deployers = new ArrayList<>();
            if (customPreDeployers != null) {
                this.deployers.addAll(customPreDeployers);
            }
            this.deployers.addAll(getDefaultDeployers());
            if (customPostDeployers != null) {
                this.deployers.addAll(customPostDeployers);
            }
        }
    }

    public Collection<? extends EngineDeployer> getDefaultDeployers() {
        List<EngineDeployer> defaultDeployers = new ArrayList<>();

        if (appDeployer == null) {
            appDeployer = new AppDeployer();
        }

        defaultDeployers.add(appDeployer);
        return defaultDeployers;
    }

    protected void initAppDefinitionCache() {
        if (appDefinitionCache == null) {
            if (appDefinitionCacheLimit <= 0) {
                appDefinitionCache = new DefaultDeploymentCache<>();
            } else {
                appDefinitionCache = new DefaultDeploymentCache<>(appDefinitionCacheLimit);
            }
        }
    }
    
    protected void initAppResourceConverter() {
        if (appResourceConverter == null) {
            appResourceConverter = new AppResourceConverterImpl(objectMapper);
        }
    }

    protected void initDeploymentManager() {
        if (deploymentManager == null) {
            deploymentManager = new AppDeploymentManager();
            deploymentManager.setAppEngineConfiguration(this);
            deploymentManager.setAppDefinitionCache(appDefinitionCache);
            deploymentManager.setDeployers(deployers);
            deploymentManager.setAppDefinitionEntityManager(appDefinitionEntityManager);
            deploymentManager.setDeploymentEntityManager(appDeploymentEntityManager);
        }
    }

    @Override
    public String getEngineCfgKey() {
        return EngineConfigurationConstants.KEY_APP_ENGINE_CONFIG;
    }

    @Override
    public CommandInterceptor createTransactionInterceptor() {
        return null;
    }

    @Override
    public InputStream getMyBatisXmlConfigurationStream() {
        return getResourceAsStream(DEFAULT_MYBATIS_MAPPING_FILE);
    }

    @Override
    protected void initDbSqlSessionFactoryEntitySettings() {
        defaultInitDbSqlSessionFactoryEntitySettings(EntityDependencyOrder.INSERT_ORDER, EntityDependencyOrder.DELETE_ORDER);
    }

    public void initVariableTypes() {
        if (variableTypes == null) {
            variableTypes = new DefaultVariableTypes();
            if (customPreVariableTypes != null) {
                for (VariableType customVariableType : customPreVariableTypes) {
                    variableTypes.addType(customVariableType);
                }
            }
            variableTypes.addType(new NullType());
            variableTypes.addType(new StringType(getMaxLengthString()));
            variableTypes.addType(new LongStringType(getMaxLengthString() + 1));
            variableTypes.addType(new BooleanType());
            variableTypes.addType(new ShortType());
            variableTypes.addType(new IntegerType());
            variableTypes.addType(new LongType());
            variableTypes.addType(new DateType());
            variableTypes.addType(new JodaDateType());
            variableTypes.addType(new JodaDateTimeType());
            variableTypes.addType(new DoubleType());
            variableTypes.addType(new UUIDType());
            variableTypes.addType(new JsonType(getMaxLengthString(), objectMapper));
            variableTypes.addType(new LongJsonType(getMaxLengthString() + 1, objectMapper));
            variableTypes.addType(new ByteArrayType());
            variableTypes.addType(new SerializableType(serializableVariableTypeTrackDeserializedObjects));
            if (customPostVariableTypes != null) {
                for (VariableType customVariableType : customPostVariableTypes) {
                    variableTypes.addType(customVariableType);
                }
            }
        }
    }

    public void initVariableServiceConfiguration() {
        this.variableServiceConfiguration = new VariableServiceConfiguration(ScopeTypes.APP);

        this.variableServiceConfiguration.setClock(this.clock);
        this.variableServiceConfiguration.setObjectMapper(this.objectMapper);
        this.variableServiceConfiguration.setEventDispatcher(this.eventDispatcher);

        this.variableServiceConfiguration.setVariableTypes(this.variableTypes);

        this.variableServiceConfiguration.setMaxLengthString(this.getMaxLengthString());
        this.variableServiceConfiguration.setSerializableVariableTypeTrackDeserializedObjects(this.isSerializableVariableTypeTrackDeserializedObjects());

        this.variableServiceConfiguration.init();

        addServiceConfiguration(EngineConfigurationConstants.KEY_VARIABLE_SERVICE_CONFIG, this.variableServiceConfiguration);
    }

    public void initIdentityLinkServiceConfiguration() {
        this.identityLinkServiceConfiguration = new IdentityLinkServiceConfiguration(ScopeTypes.APP);
        this.identityLinkServiceConfiguration.setClock(this.clock);
        this.identityLinkServiceConfiguration.setObjectMapper(this.objectMapper);
        this.identityLinkServiceConfiguration.setEventDispatcher(this.eventDispatcher);

        this.identityLinkServiceConfiguration.init();

        addServiceConfiguration(EngineConfigurationConstants.KEY_IDENTITY_LINK_SERVICE_CONFIG, this.identityLinkServiceConfiguration);
    }

    public void initBusinessCalendarManager() {
        if (businessCalendarManager == null) {
            MapBusinessCalendarManager mapBusinessCalendarManager = new MapBusinessCalendarManager();
            mapBusinessCalendarManager.addBusinessCalendar(DurationBusinessCalendar.NAME, new DurationBusinessCalendar(this.clock));
            mapBusinessCalendarManager.addBusinessCalendar(DueDateBusinessCalendar.NAME, new DueDateBusinessCalendar(this.clock));
            mapBusinessCalendarManager.addBusinessCalendar(CycleBusinessCalendar.NAME, new CycleBusinessCalendar(this.clock));

            businessCalendarManager = mapBusinessCalendarManager;
        }
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

    @Override
    public String getEngineName() {
        return cmmnEngineName;
    }

    public String getCmmnEngineName() {
        return cmmnEngineName;
    }

    public AppEngineConfiguration setCmmnEngineName(String cmmnEngineName) {
        this.cmmnEngineName = cmmnEngineName;
        return this;
    }

    @Override
    public AppManagementService getAppManagementService() {
        return appManagementService;
    }

    public AppEngineConfiguration setAppManagementService(AppManagementService appManagementService) {
        this.appManagementService = appManagementService;
        return this;
    }

    @Override
    public AppRepositoryService getAppRepositoryService() {
        return appRepositoryService;
    }

    public AppEngineConfiguration setAppRepositoryService(AppRepositoryService appRepositoryService) {
        this.appRepositoryService = appRepositoryService;
        return this;
    }

    public IdmIdentityService getIdmIdentityService() {
        return ((IdmEngineConfigurationApi) engineConfigurations.get(EngineConfigurationConstants.KEY_IDM_ENGINE_CONFIG)).getIdmIdentityService();
    }

    public TableDataManager getTableDataManager() {
        return tableDataManager;
    }

    public AppEngineConfiguration setTableDataManager(TableDataManager tableDataManager) {
        this.tableDataManager = tableDataManager;
        return this;
    }

    public AppDeploymentDataManager getDeploymentDataManager() {
        return deploymentDataManager;
    }

    public AppEngineConfiguration setDeploymentDataManager(AppDeploymentDataManager deploymentDataManager) {
        this.deploymentDataManager = deploymentDataManager;
        return this;
    }

    public AppResourceDataManager getResourceDataManager() {
        return resourceDataManager;
    }

    public AppEngineConfiguration setResourceDataManager(AppResourceDataManager resourceDataManager) {
        this.resourceDataManager = resourceDataManager;
        return this;
    }

    public AppDefinitionDataManager getAppDefinitionDataManager() {
        return appDefinitionDataManager;
    }

    public AppEngineConfiguration setAppDefinitionDataManager(AppDefinitionDataManager appDefinitionDataManager) {
        this.appDefinitionDataManager = appDefinitionDataManager;
        return this;
    }

    public AppDeploymentEntityManager getAppDeploymentEntityManager() {
        return appDeploymentEntityManager;
    }

    public AppEngineConfiguration setAppDeploymentEntityManager(AppDeploymentEntityManager appDeploymentEntityManager) {
        this.appDeploymentEntityManager = appDeploymentEntityManager;
        return this;
    }

    public AppResourceEntityManager getAppResourceEntityManager() {
        return appResourceEntityManager;
    }

    public AppEngineConfiguration setAppResourceEntityManager(AppResourceEntityManager appResourceEntityManager) {
        this.appResourceEntityManager = appResourceEntityManager;
        return this;
    }

    public AppDefinitionEntityManager getAppDefinitionEntityManager() {
        return appDefinitionEntityManager;
    }

    public AppEngineConfiguration setAppDefinitionEntityManager(AppDefinitionEntityManager appDefinitionEntityManager) {
        this.appDefinitionEntityManager = appDefinitionEntityManager;
        return this;
    }

    public AppDeployer getAppDeployer() {
        return appDeployer;
    }

    public AppEngineConfiguration setAppDeployer(AppDeployer appDeployer) {
        this.appDeployer = appDeployer;
        return this;
    }

    public AppResourceConverter getAppResourceConverter() {
        return appResourceConverter;
    }

    public AppEngineConfiguration setAppResourceConverter(AppResourceConverter appResourceConverter) {
        this.appResourceConverter = appResourceConverter;
        return this;
    }

    public AppDeploymentManager getDeploymentManager() {
        return deploymentManager;
    }

    public AppEngineConfiguration setDeploymentManager(AppDeploymentManager deploymentManager) {
        this.deploymentManager = deploymentManager;
        return this;
    }

    public int getAppDefinitionCacheLimit() {
        return appDefinitionCacheLimit;
    }

    public AppEngineConfiguration setAppDefinitionCacheLimit(int appDefinitionCacheLimit) {
        this.appDefinitionCacheLimit = appDefinitionCacheLimit;
        return this;
    }

    public DeploymentCache<AppDefinitionCacheEntry> getAppDefinitionCache() {
        return appDefinitionCache;
    }

    public AppEngineConfiguration setAppDefinitionCache(DeploymentCache<AppDefinitionCacheEntry> appDefinitionCache) {
        this.appDefinitionCache = appDefinitionCache;
        return this;
    }

    @Override
    public AppEngineConfiguration setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        return this;
    }

    public boolean isExecuteServiceSchemaManagers() {
        return executeServiceSchemaManagers;
    }

    public void setExecuteServiceSchemaManagers(boolean executeServiceSchemaManagers) {
        this.executeServiceSchemaManagers = executeServiceSchemaManagers;
    }

    @Override
    public ExpressionManager getExpressionManager() {
        return expressionManager;
    }

    @Override
    public AppEngineConfiguration setExpressionManager(ExpressionManager expressionManager) {
        this.expressionManager = expressionManager;
        return this;
    }

    public SchemaManager getIdentityLinkSchemaManager() {
        return identityLinkSchemaManager;
    }

    public AppEngineConfiguration setIdentityLinkSchemaManager(SchemaManager identityLinkSchemaManager) {
        this.identityLinkSchemaManager = identityLinkSchemaManager;
        return this;
    }

    public SchemaManager getVariableSchemaManager() {
        return variableSchemaManager;
    }

    public AppEngineConfiguration setVariableSchemaManager(SchemaManager variableSchemaManager) {
        this.variableSchemaManager = variableSchemaManager;
        return this;
    }

    @Override
    public VariableTypes getVariableTypes() {
        return variableTypes;
    }

    @Override
    public AppEngineConfiguration setVariableTypes(VariableTypes variableTypes) {
        this.variableTypes = variableTypes;
        return this;
    }

    public List<VariableType> getCustomPreVariableTypes() {
        return customPreVariableTypes;
    }

    public AppEngineConfiguration setCustomPreVariableTypes(List<VariableType> customPreVariableTypes) {
        this.customPreVariableTypes = customPreVariableTypes;
        return this;
    }

    public List<VariableType> getCustomPostVariableTypes() {
        return customPostVariableTypes;
    }

    public AppEngineConfiguration setCustomPostVariableTypes(List<VariableType> customPostVariableTypes) {
        this.customPostVariableTypes = customPostVariableTypes;
        return this;
    }

    public IdentityLinkServiceConfiguration getIdentityLinkServiceConfiguration() {
        return identityLinkServiceConfiguration;
    }

    public AppEngineConfiguration setIdentityLinkServiceConfiguration(IdentityLinkServiceConfiguration identityLinkServiceConfiguration) {
        this.identityLinkServiceConfiguration = identityLinkServiceConfiguration;
        return this;
    }

    public VariableServiceConfiguration getVariableServiceConfiguration() {
        return variableServiceConfiguration;
    }

    public AppEngineConfiguration setVariableServiceConfiguration(VariableServiceConfiguration variableServiceConfiguration) {
        this.variableServiceConfiguration = variableServiceConfiguration;
        return this;
    }

    public boolean isSerializableVariableTypeTrackDeserializedObjects() {
        return serializableVariableTypeTrackDeserializedObjects;
    }

    public AppEngineConfiguration setSerializableVariableTypeTrackDeserializedObjects(boolean serializableVariableTypeTrackDeserializedObjects) {
        this.serializableVariableTypeTrackDeserializedObjects = serializableVariableTypeTrackDeserializedObjects;
        return this;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public AppEngineConfiguration setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        return this;
    }

    public boolean isDisableIdmEngine() {
        return disableIdmEngine;
    }

    public AppEngineConfiguration setDisableIdmEngine(boolean disableIdmEngine) {
        this.disableIdmEngine = disableIdmEngine;
        return this;
    }

    public BusinessCalendarManager getBusinessCalendarManager() {
        return businessCalendarManager;
    }

    public AppEngineConfiguration setBusinessCalendarManager(BusinessCalendarManager businessCalendarManager) {
        this.businessCalendarManager = businessCalendarManager;
        return this;
    }
}
