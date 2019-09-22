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
package mobius.cmmn.engine.impl.cmd;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import mobius.cmmn.api.repository.CaseDefinition;
import mobius.cmmn.api.repository.CmmnDeployment;
import mobius.cmmn.engine.CmmnEngineConfiguration;
import mobius.cmmn.engine.impl.repository.CaseDefinitionUtil;
import mobius.cmmn.engine.impl.util.CommandContextUtil;
import mobius.cmmn.model.Case;
import mobius.cmmn.model.CmmnModel;
import mobius.cmmn.model.Stage;
import mobius.common.engine.api.FlowableIllegalArgumentException;
import mobius.common.engine.api.FlowableObjectNotFoundException;
import mobius.common.engine.api.scope.ScopeTypes;
import mobius.common.engine.impl.interceptor.Command;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.form.api.FormFieldHandler;
import mobius.form.api.FormInfo;
import mobius.form.api.FormService;

/**
 * @author Yvo Swillens
 */
public class GetStartFormModelCmd implements Command<FormInfo>, Serializable {

    private static final long serialVersionUID = 1L;

    protected String caseDefinitionId;
    protected String caseInstanceId;

    public GetStartFormModelCmd(String caseDefinitionId, String caseInstanceId) {
        this.caseDefinitionId = caseDefinitionId;
        this.caseInstanceId = caseInstanceId;
    }

    @Override
    public FormInfo execute(CommandContext commandContext) {
        CmmnEngineConfiguration cmmnEngineConfiguration = CommandContextUtil.getCmmnEngineConfiguration(commandContext);
        FormService formService = CommandContextUtil.getFormService(commandContext);
        if (formService == null) {
            throw new FlowableIllegalArgumentException("Form engine is not initialized");
        }

        FormInfo formInfo = null;
        CaseDefinition caseDefinition = CaseDefinitionUtil.getCaseDefinition(caseDefinitionId);
        CmmnModel cmmnModel = CaseDefinitionUtil.getCmmnModel(caseDefinitionId);
        Case caseModel = cmmnModel.getCaseById(caseDefinition.getKey());
        Stage planModel = caseModel.getPlanModel();

        if (StringUtils.isNotEmpty(planModel.getFormKey())) {
            CmmnDeployment deployment = CommandContextUtil.getCmmnDeploymentEntityManager(commandContext).findById(caseDefinition.getDeploymentId());
            formInfo = formService.getFormInstanceModelByKeyAndParentDeploymentIdAndScopeId(planModel.getFormKey(), deployment.getParentDeploymentId(), 
                            caseInstanceId, ScopeTypes.CMMN, null, caseDefinition.getTenantId(), cmmnEngineConfiguration.isFallbackToDefaultTenant());
        }

        // If form does not exists, we don't want to leak out this info to just anyone
        if (formInfo == null) {
            throw new FlowableObjectNotFoundException("Form model for case definition " + caseDefinitionId + " cannot be found");
        }

        FormFieldHandler formFieldHandler = cmmnEngineConfiguration.getFormFieldHandler();
        formFieldHandler.enrichFormFields(formInfo);

        return formInfo;
    }
}
