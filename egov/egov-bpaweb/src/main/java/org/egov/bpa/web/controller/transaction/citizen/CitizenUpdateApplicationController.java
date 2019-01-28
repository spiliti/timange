/*
 * eGov suite of products aim to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) <2015>  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */
package org.egov.bpa.web.controller.transaction.citizen;

import org.egov.bpa.master.entity.StakeHolder;
import org.egov.bpa.master.entity.enums.StakeHolderStatus;
import org.egov.bpa.transaction.entity.BpaApplication;
import org.egov.bpa.transaction.entity.BpaAppointmentSchedule;
import org.egov.bpa.transaction.entity.BuildingSubUsage;
import org.egov.bpa.transaction.entity.BuildingSubUsageDetails;
import org.egov.bpa.transaction.entity.LettertoParty;
import org.egov.bpa.transaction.entity.SlotApplication;
import org.egov.bpa.transaction.entity.enums.AppointmentSchedulePurpose;
import org.egov.bpa.transaction.service.BpaAppointmentScheduleService;
import org.egov.bpa.transaction.service.InspectionService;
import org.egov.bpa.transaction.service.LettertoPartyService;
import org.egov.bpa.transaction.service.collection.ApplicationBpaBillService;
import org.egov.bpa.transaction.service.collection.GenericBillGeneratorService;
import org.egov.bpa.web.controller.transaction.BpaGenericApplicationController;
import org.egov.eis.entity.Assignment;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.infra.utils.DateUtils;
import org.egov.infra.workflow.matrix.entity.WorkFlowMatrix;
import org.egov.pims.commons.Position;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.egov.bpa.utils.BpaConstants.APPLICATION_STATUS_APPROVED;
import static org.egov.bpa.utils.BpaConstants.APPLICATION_STATUS_CANCELLED;
import static org.egov.bpa.utils.BpaConstants.APPLICATION_STATUS_CREATED;
import static org.egov.bpa.utils.BpaConstants.APPLICATION_STATUS_DOC_VERIFIED;
import static org.egov.bpa.utils.BpaConstants.APPLICATION_STATUS_REGISTERED;
import static org.egov.bpa.utils.BpaConstants.APPLICATION_STATUS_RESCHEDULED;
import static org.egov.bpa.utils.BpaConstants.APPLICATION_STATUS_SCHEDULED;
import static org.egov.bpa.utils.BpaConstants.AUTH_TO_SUBMIT_PLAN;
import static org.egov.bpa.utils.BpaConstants.CHECKLIST_TYPE;
import static org.egov.bpa.utils.BpaConstants.CHECKLIST_TYPE_NOC;
import static org.egov.bpa.utils.BpaConstants.CREATE_ADDITIONAL_RULE_CREATE;
import static org.egov.bpa.utils.BpaConstants.CREATE_ADDITIONAL_RULE_CREATE_ONEDAYPERMIT;
import static org.egov.bpa.utils.BpaConstants.DISCLIMER_MESSAGE_ONEDAYPERMIT_ONSAVE;
import static org.egov.bpa.utils.BpaConstants.DISCLIMER_MESSAGE_ONSAVE;
import static org.egov.bpa.utils.BpaConstants.ENABLEONLINEPAYMENT;
import static org.egov.bpa.utils.BpaConstants.WF_CANCELAPPLICATION_BUTTON;
import static org.egov.bpa.utils.BpaConstants.WF_LBE_SUBMIT_BUTTON;
import static org.egov.bpa.utils.BpaConstants.WF_SAVE_BUTTON;
import static org.egov.bpa.utils.BpaConstants.WF_NEW_STATE;

@Controller
@RequestMapping(value = "/application")
public class CitizenUpdateApplicationController extends BpaGenericApplicationController {
	private static final String COLLECT_FEE_VALIDATE = "collectFeeValidate";
	private static final String IS_CITIZEN = "isCitizen";
	private static final String CITIZEN_VIEW = "citizen-view";
	private static final String BPAAPP_CITIZEN_FORM = "bpaapp-citizenForm";
	private static final String MESSAGE = "message";
	private static final String BPA_APPLICATION = "bpaApplication";
	private static final String APPLICATION_HISTORY = "applicationHistory";
	private static final String ADDITIONALRULE = "additionalRule";
	private static final String COMMON_ERROR = "common-error";

	@Autowired
	LettertoPartyService lettertoPartyService;
	@Autowired
	private GenericBillGeneratorService genericBillGeneratorService;
	@Autowired
	private InspectionService inspectionService;
	@Autowired
	private ApplicationBpaBillService applicationBpaBillService;
	@Autowired
	private BpaAppointmentScheduleService bpaAppointmentScheduleService;

	@ModelAttribute
	public BpaApplication getBpaApplication(@PathVariable final String applicationNumber) {
		return applicationBpaService.findByApplicationNumber(applicationNumber);
	}

	@RequestMapping(value = "/citizen/update/{applicationNumber}", method = RequestMethod.GET)
	public String updateApplicationForm(final Model model, @PathVariable final String applicationNumber,
										final HttpServletRequest request) {
		final BpaApplication application = getBpaApplication(applicationNumber);
		User user = securityUtils.getCurrentUser();
		StakeHolder stkHldr = stakeHolderService.findById(user.getId());
		if(stkHldr != null && StakeHolderStatus.BLOCKED.equals(stkHldr.getStatus()) && APPLICATION_STATUS_CREATED.equals(application.getStatus().getCode())) {
			model.addAttribute(MESSAGE,
					messageSource.getMessage("msg.stakeholder.license.blocked", new String[] {ApplicationThreadLocals.getMunicipalityName()}, null));
			return COMMON_ERROR;
		}
		model.addAttribute(APPLICATION_HISTORY, workflowHistoryService.getHistory(application.getAppointmentSchedule(), application.getCurrentState(), application.getStateHistory()));
		prepareCommonModelAttribute(model, application.isCitizenAccepted());
		return loadViewdata(model, application);
	}

	private String loadViewdata(final Model model, final BpaApplication application) {
		model.addAttribute("mode", "newappointment");
		if (!application.getIsOneDayPermitApplication()
				&& (APPLICATION_STATUS_SCHEDULED.equals(application.getStatus().getCode())
						|| APPLICATION_STATUS_RESCHEDULED.equals(application.getStatus().getCode()))
				&& !application.getIsRescheduledByCitizen()) {
			model.addAttribute("mode", "showRescheduleToCitizen");
		}
		prepareFormData(model);
		buildReceiptDetails(application.getDemand().getEgDemandDetails(), application.getReceipts());
		application.setApplicationAmenityTemp(application.getApplicationAmenity());
		applicationBpaService.buildExistingAndProposedBuildingDetails(application);
		if(!application.getBuildingSubUsages().isEmpty())
			buildBuildingSubUsages(application);
		model.addAttribute("stateType", application.getClass().getSimpleName());
		model.addAttribute("isEDCRIntegrationRequire",
				bpaApplicationValidationService.isEdcrInetgrationRequired(application.getServiceType().getCode(), application.getOccupancy().getDescription()));
		model.addAttribute("loadingFloorDetailsFromEdcrRequire", true);
		if (application.getIsOneDayPermitApplication()) {
			model.addAttribute(ADDITIONALRULE, CREATE_ADDITIONAL_RULE_CREATE_ONEDAYPERMIT);
		} else
			model.addAttribute(ADDITIONALRULE, CREATE_ADDITIONAL_RULE_CREATE);
		model.addAttribute(BPA_APPLICATION, application);
		model.addAttribute("currentState",
				application.getCurrentState() == null ? "" : application.getCurrentState().getValue());
		model.addAttribute("nocCheckListDetails", checkListDetailService
				.findActiveCheckListByServiceType(application.getServiceType().getId(), CHECKLIST_TYPE_NOC));
		model.addAttribute("checkListDetailList", checkListDetailService
				.findActiveCheckListByServiceType(application.getServiceType().getId(), CHECKLIST_TYPE));
		model.addAttribute("applicationDocumentList", application.getApplicationDocument());
		model.addAttribute("isFeeCollected", bpaDemandService.checkAnyTaxIsPendingToCollect(application));
		model.addAttribute("isReconciliationInProgress", bpaUtils.checkIsReconciliationInProgress(application.getApplicationNumber()));
		List<LettertoParty> lettertoPartyList = lettertoPartyService.findByBpaApplicationOrderByIdDesc(application);
		model.addAttribute("lettertopartylist", lettertoPartyList);
		model.addAttribute("inspectionList", inspectionService.findByBpaApplicationOrderByIdAsc(application));
		model.addAttribute("admissionFee", applicationBpaService.setAdmissionFeeAmountForRegistrationWithAmenities(
				application.getServiceType().getId(), application.getApplicationAmenity()));
		if (!lettertoPartyList.isEmpty() && lettertoPartyList.get(0).getSentDate() != null)
			model.addAttribute("mode", "showLPDetails");
		buildAppointmentDetailsOfScutinyAndInspection(model, application);
		Boolean isCitizen = (Boolean) model.asMap().get(IS_CITIZEN);
		if (APPLICATION_STATUS_REGISTERED.equals(application.getStatus().getCode())
			|| APPLICATION_STATUS_SCHEDULED.equals(application.getStatus().getCode())
			|| APPLICATION_STATUS_RESCHEDULED.equals(application.getStatus().getCode())
			|| APPLICATION_STATUS_APPROVED.equals(application.getStatus().getCode())) {
			if (applicationBpaService.applicationinitiatedByNonEmployee(application)
				&& applicationBpaService.checkAnyTaxIsPendingToCollect(application)) {
				model.addAttribute(COLLECT_FEE_VALIDATE, messageSource.getMessage("msg.payfees.toprocess.appln", null, null));
				String enableOrDisablePayOnline = bpaUtils.getAppconfigValueByKeyName(ENABLEONLINEPAYMENT);
				model.addAttribute("onlinePaymentEnable", (enableOrDisablePayOnline.equalsIgnoreCase("YES") ? Boolean.TRUE : Boolean.FALSE));
			} else
				model.addAttribute(COLLECT_FEE_VALIDATE, "");
		}

		if (application.getStatus() != null
			&& application.getStatus().getCode().equals(APPLICATION_STATUS_CREATED) && !isCitizen ) {
			getDcrDocumentsUploadMode(model);
			return BPAAPP_CITIZEN_FORM;
		} else
			return CITIZEN_VIEW;
	}

	private void buildBuildingSubUsages(final BpaApplication application) {
		for(BuildingSubUsage subUsage : application.getBuildingSubUsages())
			for(BuildingSubUsageDetails subUsageDetails : subUsage.getSubUsageDetails())
				subUsageDetails.setSubUsagesTemp(occupancyService.findSubUsagesByOccupancy(subUsageDetails.getMainUsage().getDescription()));
	}

	private void buildAppointmentDetailsOfScutinyAndInspection(Model model, BpaApplication application) {
		if (APPLICATION_STATUS_SCHEDULED.equals(application.getStatus().getCode())
			|| APPLICATION_STATUS_RESCHEDULED.equals(application.getStatus().getCode())) {
			Optional<SlotApplication> activeSlotApplication = application.getSlotApplications().stream().reduce((slotAppln1, slotAppln2) -> slotAppln2);
			if (activeSlotApplication.isPresent()) {
				model.addAttribute("appointmentDateRes", DateUtils.toDefaultDateFormat(activeSlotApplication.get().getSlotDetail().getSlot().getAppointmentDate()));
				model.addAttribute("appointmentTimeRes", activeSlotApplication.get().getSlotDetail().getAppointmentTime());
				model.addAttribute("appointmentTitle", messageSource.getMessage("msg.appointment.details.for.docscrutiny", null, null));
			}
		} else if (APPLICATION_STATUS_DOC_VERIFIED.equals(application.getStatus().getCode()) && application.getInspections().isEmpty()) {
			List<BpaAppointmentSchedule> appointmentScheduledList = bpaAppointmentScheduleService.findByApplication(application,
					AppointmentSchedulePurpose.INSPECTION);
			if (!appointmentScheduledList.isEmpty()) {
				model.addAttribute("appointmentDateRes", DateUtils.toDefaultDateFormat(appointmentScheduledList.get(0).getAppointmentDate()));
				model.addAttribute("appointmentTimeRes", appointmentScheduledList.get(0).getAppointmentTime());
				model.addAttribute("appmntInspnRemarks", appointmentScheduledList.get(0).isPostponed() ? appointmentScheduledList.get(0).getPostponementReason() : appointmentScheduledList.get(0).getRemarks());
				model.addAttribute("appointmentTitle", messageSource.getMessage("msg.appointment.details.for.fieldinspec", null, null));
			}
		}
	}

	@RequestMapping(value = "/citizen/update-submit/{applicationNumber}", method = RequestMethod.POST)
	public String updateApplication(@Valid @ModelAttribute("") BpaApplication bpaApplication,
									@PathVariable final String applicationNumber, final BindingResult resultBinder,
									final HttpServletRequest request, final Model model,
									final RedirectAttributes redirectAttributes, @RequestParam("files") final MultipartFile... files) {

		if (resultBinder.hasErrors()) {
			prepareCommonModelAttribute(model, bpaApplication.isCitizenAccepted());
			return loadViewdata(model, bpaApplication);
		}
		
		String workFlowAction = request.getParameter("workFlowAction");
		Long approvalPosition = 0l;
		final WorkFlowMatrix wfMatrix = bpaUtils.getWfMatrixByCurrentState(bpaApplication.getIsOneDayPermitApplication(), bpaApplication.getStateType(), WF_NEW_STATE);
		if (wfMatrix != null)
			approvalPosition = bpaUtils.getUserPositionIdByZone(wfMatrix.getNextDesignation(),
					bpaApplication.getSiteDetail().get(0) != null
					&& bpaApplication.getSiteDetail().get(0).getElectionBoundary() != null
					? bpaApplication.getSiteDetail().get(0).getElectionBoundary().getId() : null);
		if (workFlowAction != null
			&& workFlowAction.equals(WF_LBE_SUBMIT_BUTTON)
			&& (approvalPosition == 0 || approvalPosition == null)) {
			model.addAttribute("noJAORSAMessage", messageSource.getMessage("msg.official.not.exist", new String[]{
					ApplicationThreadLocals.getMunicipalityName()}, LocaleContextHolder.getLocale()));
			prepareCommonModelAttribute(model, bpaApplication.isCitizenAccepted());
			return loadViewdata(model, bpaApplication);
		}
		if (bpaApplicationValidationService.validateBuildingDetails(bpaApplication, model)) {
			prepareCommonModelAttribute(model, bpaApplication.isCitizenAccepted());
			return loadViewdata(model, bpaApplication);
		}
		applicationBpaService.buildExistingAndProposedBuildingDetails(bpaApplication);
		if (!bpaApplicationValidationService.checkStakeholderIsValid(bpaApplication)) {
			String message = bpaApplicationValidationService.getValidationMessageForBusinessResgistration(bpaApplication);
			model.addAttribute("invalidStakeholder", message);
			prepareCommonModelAttribute(model, bpaApplication.isCitizenAccepted());
			return loadViewdata(model, bpaApplication);
		}

		if (!bpaApplication.getApplicationDocument().isEmpty())
			applicationBpaService.persistOrUpdateApplicationDocument(bpaApplication);

		bpaApplication.getApplicationAmenity().clear();
		bpaApplication.setApplicationAmenity(bpaApplication.getApplicationAmenityTemp());
		bpaApplication.setDemand(applicationBpaBillService.createDemand(bpaApplication));
		String enableOrDisablePayOnline = bpaUtils.getAppconfigValueByKeyName(ENABLEONLINEPAYMENT);

		if (workFlowAction != null && WF_CANCELAPPLICATION_BUTTON.equalsIgnoreCase(workFlowAction))
			bpaApplication.setStatus(applicationBpaService.getStatusByCodeAndModuleType(APPLICATION_STATUS_CANCELLED));

		if (bpaApplication.getOwner().getUser() != null && bpaApplication.getOwner().getUser().getId() == null)
			applicationBpaService.buildOwnerDetails(bpaApplication);
		// To allot slot for one day permit applications
		applicationBpaService.saveAndFlushApplication(bpaApplication, workFlowAction);
		bpaUtils.updatePortalUserinbox(bpaApplication, null);

		// Will redirect to collection, then after collection success will forward to official
		if (workFlowAction != null && workFlowAction.equals(WF_LBE_SUBMIT_BUTTON)
			&& enableOrDisablePayOnline.equalsIgnoreCase("YES") && bpaUtils.checkAnyTaxIsPendingToCollect(bpaApplication.getDemand())) {
			return genericBillGeneratorService
					.generateBillAndRedirectToCollection(bpaApplication, model);
		} // When if fee collection not require then will forward to official
		else if (workFlowAction != null && workFlowAction.equals(WF_LBE_SUBMIT_BUTTON)
				   && !bpaUtils.checkAnyTaxIsPendingToCollect(bpaApplication.getDemand()) && !bpaUtils.logedInuserIsCitizen()) {
			String remarks = null;
			if (bpaApplication.getAuthorizedToSubmitPlan())
				remarks = AUTH_TO_SUBMIT_PLAN;
			bpaUtils.redirectToBpaWorkFlow(approvalPosition, bpaApplication, WF_NEW_STATE,
					remarks == null ? bpaApplication.getApprovalComent() : remarks, null, null);
			bpaUtils.sendSmsEmailOnCitizenSubmit(bpaApplication);
			List<Assignment> assignments;
			if (null == approvalPosition)
				assignments = bpaWorkFlowService.getAssignmentsByPositionAndDate(bpaApplication.getCurrentState().getOwnerPosition().getId(), new Date());
			else
				assignments = bpaWorkFlowService.getAssignmentsByPositionAndDate(approvalPosition, new Date());
			Position pos = assignments.get(0).getPosition();
			User wfUser = assignments.get(0).getEmployee();
			String message = messageSource.getMessage("msg.portal.forward.registration", new String[]{
					wfUser == null ? "" : wfUser.getUsername().concat("~")
												.concat(getDesinationNameByPosition(pos)),
					bpaApplication.getApplicationNumber()}, LocaleContextHolder.getLocale());
			if (bpaApplication.getIsOneDayPermitApplication()) {
				message = message.concat(DISCLIMER_MESSAGE_ONEDAYPERMIT_ONSAVE);
				getAppointmentMsgForOnedayPermit(bpaApplication, model);
			} else
				message = message.concat(DISCLIMER_MESSAGE_ONSAVE);

			redirectAttributes.addFlashAttribute(MESSAGE, message);
		} else if (workFlowAction != null && workFlowAction.equals(WF_CANCELAPPLICATION_BUTTON))
			redirectAttributes.addFlashAttribute(MESSAGE, messageSource.getMessage("msg.cancel.applnby.applicantitself.success", new String[]{bpaApplication.getApplicationNumber()}, null));
		else if ( workFlowAction != null && workFlowAction.equals(WF_SAVE_BUTTON) && bpaUtils.isCitizenAcceptanceRequired() && bpaApplication.isCitizenAccepted() && bpaUtils.logedInuserIsCitizen())
		{
			String successMessage = messageSource.getMessage("msg.appln.accepted.succes", new String[]{bpaApplication.getApplicationNumber()}, null);
			redirectAttributes.addFlashAttribute(MESSAGE, successMessage.concat(DISCLIMER_MESSAGE_ONSAVE));
		}
		else
			redirectAttributes.addFlashAttribute(MESSAGE, 
					messageSource.getMessage("msg.appln.saved.succes", new String[]{bpaApplication.getApplicationNumber()}, null));

		return "redirect:/application/citizen/success/" + bpaApplication.getApplicationNumber();
	}

}