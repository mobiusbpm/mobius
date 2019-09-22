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

package mobius.engine.impl.migration;

import java.util.Map;

import mobius.common.engine.api.FlowableException;
import mobius.engine.RuntimeService;
import mobius.engine.migration.ActivityMigrationMapping;
import mobius.engine.migration.ProcessInstanceMigrationBuilder;
import mobius.engine.migration.ProcessInstanceMigrationDocument;

/**
 * @author Dennis Federico
 */
public class ProcessInstanceMigrationBuilderImpl implements ProcessInstanceMigrationBuilder {

    protected RuntimeService runtimeService;
    protected ProcessInstanceMigrationDocumentBuilderImpl migrationDocumentBuilder = new ProcessInstanceMigrationDocumentBuilderImpl();

    public ProcessInstanceMigrationBuilderImpl(RuntimeService runtimeService) {
        this.runtimeService = runtimeService;
    }

    @Override
    public ProcessInstanceMigrationBuilder fromProcessInstanceMigrationDocument(ProcessInstanceMigrationDocument document) {
        migrationDocumentBuilder.setProcessDefinitionToMigrateTo(document.getMigrateToProcessDefinitionId());
        migrationDocumentBuilder.setProcessDefinitionToMigrateTo(document.getMigrateToProcessDefinitionKey(), document.getMigrateToProcessDefinitionVersion());
        migrationDocumentBuilder.setTenantId(document.getMigrateToProcessDefinitionTenantId());
        migrationDocumentBuilder.addActivityMigrationMappings(document.getActivityMigrationMappings());
        return this;
    }

    @Override
    public ProcessInstanceMigrationBuilder migrateToProcessDefinition(String processDefinitionId) {
        this.migrationDocumentBuilder.setProcessDefinitionToMigrateTo(processDefinitionId);
        return this;
    }

    @Override
    public ProcessInstanceMigrationBuilder migrateToProcessDefinition(String processDefinitionKey, int processDefinitionVersion) {
        this.migrationDocumentBuilder.setProcessDefinitionToMigrateTo(processDefinitionKey, processDefinitionVersion);
        return this;
    }

    @Override
    public ProcessInstanceMigrationBuilder migrateToProcessDefinition(String processDefinitionKey, int processDefinitionVersion, String processDefinitionTenantId) {
        this.migrationDocumentBuilder.setProcessDefinitionToMigrateTo(processDefinitionKey, processDefinitionVersion);
        this.migrationDocumentBuilder.setTenantId(processDefinitionTenantId);
        return this;
    }

    @Override
    public ProcessInstanceMigrationBuilder withMigrateToProcessDefinitionTenantId(String processDefinitionTenantId) {
        this.migrationDocumentBuilder.setTenantId(processDefinitionTenantId);
        return this;
    }

    @Override
    public ProcessInstanceMigrationBuilder addActivityMigrationMapping(ActivityMigrationMapping mapping) {
        this.migrationDocumentBuilder.addActivityMigrationMapping(mapping);
        return this;
    }

    @Override
    public ProcessInstanceMigrationBuilder withProcessInstanceVariable(String variableName, Object variableValue) {
        this.migrationDocumentBuilder.processInstanceVariables.put(variableName, variableValue);
        return this;
    }

    @Override
    public ProcessInstanceMigrationBuilder withProcessInstanceVariables(Map<String, Object> variables) {
        this.migrationDocumentBuilder.processInstanceVariables.putAll(variables);
        return this;
    }

    @Override
    public ProcessInstanceMigrationDocument getProcessInstanceMigrationDocument() {
        return this.migrationDocumentBuilder.build();
    }

    @Override
    public void migrate(String processInstanceId) {
        ProcessInstanceMigrationDocument document = migrationDocumentBuilder.build();
        getRuntimeService().migrateProcessInstance(processInstanceId, document);
    }

    @Override
    public ProcessInstanceMigrationValidationResult validateMigration(String processInstanceId) {
        ProcessInstanceMigrationDocument document = migrationDocumentBuilder.build();
        return getRuntimeService().validateMigrationForProcessInstance(processInstanceId, document);
    }

    @Override
    public void migrateProcessInstances(String processDefinitionId) {
        ProcessInstanceMigrationDocument document = migrationDocumentBuilder.build();
        getRuntimeService().migrateProcessInstancesOfProcessDefinition(processDefinitionId, document);
    }

    @Override
    public ProcessInstanceMigrationValidationResult validateMigrationOfProcessInstances(String processDefinitionId) {
        ProcessInstanceMigrationDocument document = migrationDocumentBuilder.build();
        return getRuntimeService().validateMigrationForProcessInstancesOfProcessDefinition(processDefinitionId, document);
    }

    @Override
    public void migrateProcessInstances(String processDefinitionKey, int processDefinitionVersion, String processDefinitionTenantId) {
        ProcessInstanceMigrationDocument document = migrationDocumentBuilder.build();
        getRuntimeService().migrateProcessInstancesOfProcessDefinition(processDefinitionKey, processDefinitionVersion, processDefinitionTenantId, document);
    }

    @Override
    public ProcessInstanceMigrationValidationResult validateMigrationOfProcessInstances(String processDefinitionKey, int processDefinitionVersion, String processDefinitionTenantId) {
        ProcessInstanceMigrationDocument document = migrationDocumentBuilder.build();
        return getRuntimeService().validateMigrationForProcessInstancesOfProcessDefinition(processDefinitionKey, processDefinitionVersion, processDefinitionTenantId, document);
    }

    protected RuntimeService getRuntimeService() {
        if (runtimeService == null) {
            throw new FlowableException("RuntimeService cannot be null, Obtain your builder instance from the RuntimeService to access this feature");
        }
        return runtimeService;
    }
}
