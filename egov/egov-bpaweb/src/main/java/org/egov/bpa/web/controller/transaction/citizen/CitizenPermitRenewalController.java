/*
 * eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 * accountability and the service delivery of the government  organizations.
 *
 *  Copyright (C) <2019>  eGovernments Foundation
 *
 *  The updated version of eGov suite of products as by eGovernments Foundation
 *  is available at http://www.egovernments.org
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see http://www.gnu.org/licenses/ or
 *  http://www.gnu.org/licenses/gpl.html .
 *
 *  In addition to the terms of the GPL license to be adhered to in using this
 *  program, the following additional terms are to be complied with:
 *
 *      1) All versions of this program, verbatim or modified must carry this
 *         Legal Notice.
 *      Further, all user interfaces, including but not limited to citizen facing interfaces,
 *         Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
 *         derived works should carry eGovernments Foundation logo on the top right corner.
 *
 *      For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
 *      For any further queries on attribution, including queries on brand guidelines,
 *         please contact contact@egovernments.org
 *
 *      2) Any misrepresentation of the origin of the material is prohibited. It
 *         is required that all modified versions of this material be marked in
 *         reasonable ways as different from the original version.
 *
 *      3) This license does not grant any rights to any user of the program
 *         with regards to rights under trademark law for use of the trade names
 *         or trademarks of eGovernments Foundation.
 *
 *  In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egov.bpa.web.controller.transaction.citizen;

import static org.egov.bpa.utils.BpaConstants.WF_NEW_STATE;

import java.util.Arrays;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.egov.bpa.master.entity.PermitRenewal;
import org.egov.bpa.master.service.ConstructionStagesService;
import org.egov.bpa.transaction.entity.WorkflowBean;
import org.egov.bpa.transaction.service.PermitRenewalService;
import org.egov.bpa.utils.BpaUtils;
import org.egov.bpa.utils.PushBpaApplicationsToPortalUtility;
import org.egov.commons.entity.Source;
import org.egov.infra.workflow.matrix.entity.WorkFlowMatrix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @author vinoth
 *
 */
@Controller
@RequestMapping(value = "/citizen/application")
public class CitizenPermitRenewalController {

    private static final String APPLICATION_SUCCESS = "application-success";
    private static final String PERMIT_RENEWAL = "permitRenewal";
    private static final String MESSAGE = "message";
    public static final String COMMON_ERROR = "common-error";
    private static final String WORK_FLOW_ACTION = "workFlowAction";

    @Autowired
    private PermitRenewalService permitRenewalService;
    @Autowired
    protected ResourceBundleMessageSource messageSource;
    @Autowired
    private PushBpaApplicationsToPortalUtility pushBpaApplicationsToPortalUtility;
    @Autowired
    private BpaUtils bpaUtils;
    @Autowired
    private ConstructionStagesService constructionStagesService;

    @GetMapping("/permit/renewal/apply")
    public String showPermitRevocationInitiateForm(final Model model) {
        PermitRenewal permitRenewal = new PermitRenewal();
        permitRenewal.setApplicationDate(new Date());
        permitRenewal.setSource(Source.CITIZENPORTAL);
        model.addAttribute(PERMIT_RENEWAL, permitRenewal);
        model.addAttribute("constStages", constructionStagesService.findByRequiredForPermitRenewal());
        return "permit-renewal-citizen-new";
    }

    @PostMapping("/permit/renewal/create")
    public String submitPermitRevocationInitiation(@ModelAttribute PermitRenewal permitRenewal, final HttpServletRequest request,
            final Model model, final BindingResult errors,
            final RedirectAttributes redirectAttributes) {
        if (permitRenewal.getSource() == null)
            permitRenewal.setSource(Source.CITIZENPORTAL);
        Long approvalPosition = null;
        WorkflowBean wfBean = new WorkflowBean();
        wfBean.setWorkFlowAction(request.getParameter(WORK_FLOW_ACTION));
        final WorkFlowMatrix wfMatrix = bpaUtils.getWfMatrixByCurrentState(
                false, permitRenewal.getStateType(), WF_NEW_STATE,
                permitRenewal.getParent().getApplicationType().getName());
        if (wfMatrix != null)
            approvalPosition = bpaUtils.getUserPositionIdByZone(wfMatrix.getNextDesignation(),
                    bpaUtils.getBoundaryForWorkflow(permitRenewal.getParent().getSiteDetail().get(0)).getId());
        wfBean.setApproverPositionId(approvalPosition);
        permitRenewalService.save(permitRenewal, wfBean);
        pushBpaApplicationsToPortalUtility.createPortalUserinbox(permitRenewal,
                Arrays.asList(permitRenewal.getParent().getOwner().getUser(),
                        permitRenewal.getParent().getStakeHolder().get(0).getStakeHolder()),
                wfBean.getWorkFlowAction());
        model.addAttribute(MESSAGE, messageSource.getMessage("msg.permit.renewal.submit",
                new String[] { permitRenewal.getApplicationNumber() }, LocaleContextHolder.getLocale()));
        return APPLICATION_SUCCESS;
    }

}
