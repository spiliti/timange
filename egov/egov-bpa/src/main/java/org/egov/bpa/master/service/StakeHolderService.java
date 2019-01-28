/*
 * eGov suite of products aim to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) <2017>  eGovernments Foundation
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
package org.egov.bpa.master.service;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.egov.bpa.autonumber.StakeHolderCodeGenerator;
import org.egov.bpa.master.entity.StakeHolder;
import org.egov.bpa.master.entity.enums.StakeHolderStatus;
import org.egov.bpa.master.repository.StakeHolderAddressRepository;
import org.egov.bpa.master.repository.StakeHolderRepository;
import org.egov.bpa.transaction.entity.StakeHolderDocument;
import org.egov.bpa.transaction.entity.dto.SearchStakeHolderForm;
import org.egov.bpa.transaction.entity.enums.StakeHolderType;
import org.egov.bpa.transaction.service.SearchBpaApplicationService;
import org.egov.bpa.utils.BpaConstants;
import org.egov.commons.entity.Source;
import org.egov.infra.admin.master.service.RoleService;
import org.egov.infra.config.core.EnvironmentSettings;
import org.egov.infra.exception.ApplicationRuntimeException;
import org.egov.infra.filestore.entity.FileStoreMapper;
import org.egov.infra.filestore.service.FileStoreService;
import org.egov.infra.persistence.entity.Address;
import org.egov.infra.persistence.entity.CorrespondenceAddress;
import org.egov.infra.persistence.entity.PermanentAddress;
import org.egov.infra.persistence.entity.enums.AddressType;
import org.egov.infra.security.utils.SecurityUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class StakeHolderService {

    private static final String STK_HLDR_TYPE = "stakeHolder.stakeHolderType";
    private static final String STK_HLDR = "stakeHolder";
    public static final String BLOCK = "Block";
    public static final String UNBLOCK = "Unblock";
    public static final String STAKE_HOLDER_DOT_CREATED_DATE = "stakeHolder.createdDate";
    @Autowired
    private SecurityUtils securityUtils;
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private StakeHolderRepository stakeHolderRepository;
    @Autowired
    private StakeHolderAddressRepository stakeHolderAddressRepository;
    @Autowired
    private FileStoreService fileStoreService;
    @Autowired
    private CheckListDetailService checkListDetailService;
    @Autowired
    private StakeHolderCodeGenerator stakeHolderCodeGenerator;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EnvironmentSettings environmentSettings;
    @Autowired
    private RoleService roleService;
    @Autowired
    private SearchBpaApplicationService searchBpaApplicationService;

    public Session getCurrentSession() {
        return entityManager.unwrap(Session.class);
    }

    public List<StakeHolder> findAll() {
        return stakeHolderRepository.findAll();
    }

    @Transactional
    public StakeHolder save(final StakeHolder stakeHolder) {
        if (null == stakeHolder.getCode())
            stakeHolder.setCode(stakeHolderCodeGenerator.generateStakeHolderCode(stakeHolder));
        final List<Address> addressList = new ArrayList<>();
        addressList.add(setCorrespondenceAddress(stakeHolder));
        addressList.add(setPermanentAddress(stakeHolder));
        stakeHolder.setAddress(addressList);
        stakeHolder.setUsername(stakeHolder.getEmailId());
        stakeHolder.updateNextPwdExpiryDate(environmentSettings.userPasswordExpiryInDays());
        stakeHolder.setPassword(passwordEncoder.encode("demo"));
        stakeHolder.addRole(roleService.getRoleByName(BpaConstants.ROLE_BUSINESS_USER));
        stakeHolder.setActive(stakeHolder.getIsActive());
        stakeHolder.setSource(Source.SYSTEM);
        stakeHolder.setCreatedUser(securityUtils.getCurrentUser());
        stakeHolder.setCreateDate(new Date());
        stakeHolder.setLastUpdatedDate(new Date());
        stakeHolder.setLastUpdatedUser(securityUtils.getCurrentUser());
        processAndStoreApplicationDocuments(stakeHolder);
        return stakeHolderRepository.save(stakeHolder);
    }

    @Transactional
    public StakeHolder updateOnResubmit(final StakeHolder modelObj, final StakeHolder existingStakeholder) {
        if (null == modelObj.getCode())
            modelObj.setCode(stakeHolderCodeGenerator.generateStakeHolderCode(modelObj));
        modelObj.setActive(modelObj.getIsActive());
        getStakeHolderWhenResubmit(modelObj, existingStakeholder);
        updateDocumentsOnResubmit(modelObj, existingStakeholder);
        return stakeHolderRepository.save(existingStakeholder);
    }

    private void updateDocumentsOnResubmit(final StakeHolder modelObj, final StakeHolder existingStakeHolder) {
        if (!modelObj.getStakeHolderDocument().isEmpty())
            for (final StakeHolderDocument modelSHDoc : modelObj.getStakeHolderDocument()) {
                for (final StakeHolderDocument stakeHolderDoc : existingStakeHolder.getStakeHolderDocument()) {
                    if (modelSHDoc.getCheckListDetail().getId().equals(stakeHolderDoc.getCheckListDetail().getId())) {
                        stakeHolderDoc.setCheckListDetail(
                                checkListDetailService.load(stakeHolderDoc.getCheckListDetail().getId()));
                        stakeHolderDoc.setStakeHolder(existingStakeHolder);
                        if (modelSHDoc.getFiles() != null) {
                            if (modelSHDoc.getFiles().length > 0 && modelSHDoc.getFiles() != null) {
                                stakeHolderDoc.setSupportDocs(addToFileStore(modelSHDoc.getFiles()));
                                stakeHolderDoc.setIsAttached(true);
                            } else {
                                stakeHolderDoc.setIsAttached(false);
                            }
                        }
                    }
                }
            }
    }

    protected void processAndStoreApplicationDocuments(final StakeHolder stakeHolder) {
        if (!stakeHolder.getStakeHolderDocument().isEmpty())
            for (final StakeHolderDocument applicationDocument : stakeHolder.getStakeHolderDocument()) {
                applicationDocument.setCheckListDetail(
                        checkListDetailService.load(applicationDocument.getCheckListDetail().getId()));
                applicationDocument.setStakeHolder(stakeHolder);
                if (applicationDocument.getFiles() != null) {
                    for (MultipartFile tempFile : applicationDocument.getFiles()) {
                        if (!tempFile.isEmpty()) {
                            applicationDocument.setSupportDocs(addToFileStore(applicationDocument.getFiles()));
                            applicationDocument.setIsAttached(true);
                        } else {
                            applicationDocument.setIsAttached(false);
                        }
                    }
                }
            }
    }

    protected Set<FileStoreMapper> addToFileStore(final MultipartFile[] files) {
        if (ArrayUtils.isNotEmpty(files))
            return Arrays.asList(files).stream().filter(file -> !file.isEmpty()).map(file -> {
                try {
                    return fileStoreService.store(file.getInputStream(), file.getOriginalFilename(),
                            file.getContentType(), BpaConstants.FILESTORE_MODULECODE);
                } catch (final Exception e) {
                    throw new ApplicationRuntimeException("Error occurred while getting inputstream", e);
                }
            }).collect(Collectors.toSet());
        else
            return Collections.emptySet();
    }

    @Transactional
    public StakeHolder update(final StakeHolder stakeHolder, final String workFlowAction) {
        stakeHolder.addAddress(updateCorrespondenceAddress(stakeHolder, stakeHolder.getAddress()));
        stakeHolder.addAddress(updatePermanentAddress(stakeHolder, stakeHolder.getAddress()));
        stakeHolder.setLastUpdatedDate(stakeHolder.getLastModifiedDate());
        stakeHolder.setLastUpdatedUser(securityUtils.getCurrentUser());
        processAndStoreApplicationDocuments(stakeHolder);
        if ("Update".equals(workFlowAction)) {
            stakeHolder.setActive(stakeHolder.getIsActive());
        } else if (BLOCK.equals(workFlowAction)) {
            stakeHolder.setStatus(StakeHolderStatus.BLOCKED);
            setActiveToStakeholder(stakeHolder);
            stakeHolder.setNoOfTimesBlocked(stakeHolder.getNoOfTimesBlocked() == null ? 1 : stakeHolder.getNoOfTimesBlocked() + 1);
        } else if (UNBLOCK.equals(workFlowAction)) {
            stakeHolder.setStatus(StakeHolderStatus.UNBLOCKED);
            setActiveToStakeholder(stakeHolder);
        }
        return stakeHolderRepository.save(stakeHolder);
    }

    private void setActiveToStakeholder(StakeHolder stakeHolder) {
        stakeHolder.setIsActive(true);
        stakeHolder.setActive(true);
    }

    public CorrespondenceAddress setCorrespondenceAddress(final StakeHolder stakeHolder) {
        return buildCorrespondenceAddress(stakeHolder, new CorrespondenceAddress());
    }

    public PermanentAddress setPermanentAddress(final StakeHolder stakeHolder) {
        return buildPermanentAddress(stakeHolder, new PermanentAddress());
    }

    private CorrespondenceAddress updateCorrespondenceAddress(final StakeHolder stakeHolder, final List<Address> addressList) {
        CorrespondenceAddress correspondenceAddress = null;
        for (final Address address : addressList)
            if (AddressType.CORRESPONDENCE.equals(address.getType()))
                correspondenceAddress = (CorrespondenceAddress) address;
        return buildCorrespondenceAddress(stakeHolder, correspondenceAddress == null ? new CorrespondenceAddress() : correspondenceAddress);
    }

    private CorrespondenceAddress buildCorrespondenceAddress(final StakeHolder stakeHolder, final CorrespondenceAddress correspondenceAddress) {
        correspondenceAddress.setHouseNoBldgApt(stakeHolder.getCorrespondenceAddress().getHouseNoBldgApt());
        correspondenceAddress.setStreetRoadLine(stakeHolder.getCorrespondenceAddress().getStreetRoadLine());
        correspondenceAddress.setAreaLocalitySector(stakeHolder.getCorrespondenceAddress().getAreaLocalitySector());
        correspondenceAddress.setCityTownVillage(stakeHolder.getCorrespondenceAddress().getCityTownVillage());
        correspondenceAddress.setDistrict(stakeHolder.getCorrespondenceAddress().getDistrict());
        correspondenceAddress.setState(stakeHolder.getCorrespondenceAddress().getState());
        correspondenceAddress.setPostOffice(stakeHolder.getCorrespondenceAddress().getPostOffice());
        correspondenceAddress.setPinCode(stakeHolder.getCorrespondenceAddress().getPinCode());
        correspondenceAddress.setUser(stakeHolder);
        return correspondenceAddress;
    }

    public PermanentAddress updatePermanentAddress(final StakeHolder stakeHolder, final List<Address> addressList) {
        PermanentAddress permanentAddress = null;
        for (final Address address : addressList)
            if (AddressType.PERMANENT.equals(address.getType()))
                permanentAddress = (PermanentAddress) address;
        return buildPermanentAddress(stakeHolder, permanentAddress == null ? new PermanentAddress() : permanentAddress);
    }

    private PermanentAddress buildPermanentAddress(final StakeHolder stakeHolder, final PermanentAddress permanentAddress) {
        permanentAddress.setHouseNoBldgApt(stakeHolder.getPermanentAddress().getHouseNoBldgApt());
        permanentAddress.setStreetRoadLine(stakeHolder.getPermanentAddress().getStreetRoadLine());
        permanentAddress.setAreaLocalitySector(stakeHolder.getPermanentAddress().getAreaLocalitySector());
        permanentAddress.setCityTownVillage(stakeHolder.getPermanentAddress().getCityTownVillage());
        permanentAddress.setDistrict(stakeHolder.getPermanentAddress().getDistrict());
        permanentAddress.setState(stakeHolder.getPermanentAddress().getState());
        permanentAddress.setPostOffice(stakeHolder.getCorrespondenceAddress().getPostOffice());
        permanentAddress.setPinCode(stakeHolder.getPermanentAddress().getPinCode());
        permanentAddress.setUser(stakeHolder);
        return permanentAddress;
    }

    @Transactional
    public void removeAddress(final List<Address> address) {
        stakeHolderAddressRepository.deleteInBatch(address);
    }

    public StakeHolder findById(final Long id) {
        return stakeHolderRepository.findOne(id);
    }

    public StakeHolder findByMobileNumberAndStatus(final String mobileNo, final StakeHolderStatus status) {
        return stakeHolderRepository.findByMobileNumberAndStatus(mobileNo, status);
    }

    public StakeHolder findByEmailIdAndStatus(final String email, final StakeHolderStatus status) {
        return stakeHolderRepository.findByEmailIdAndStatus(email, status);
    }

    public StakeHolder findByAadhaarNumberAndStatus(final String aadhaar, final StakeHolderStatus status) {
        return stakeHolderRepository.findByAadhaarNumberAndStatus(aadhaar, status);
    }

    public StakeHolder findByPanNumberAndStatus(final String pan, final StakeHolderStatus status) {
        return stakeHolderRepository.findByPanAndStatus(pan, status);
    }

    public StakeHolder findByLicenseNumber(final String licenseNumber) {
        return stakeHolderRepository.findByLicenceNumber(licenseNumber);
    }

    public StakeHolder findByLicenseNumberAndStatus(final String licenseNumber, final StakeHolderStatus status) {
        return stakeHolderRepository.findByLicenceNumberAndStatus(licenseNumber, status);
    }

    public StakeHolder getStakeHolderWhenResubmit(final StakeHolder modelObj, final StakeHolder existingStakeHolder) {
        existingStakeHolder.setIsOnbehalfOfOrganization(modelObj.getIsOnbehalfOfOrganization());
        if (modelObj.getIsOnbehalfOfOrganization()) {
            existingStakeHolder.setOrganizationName(modelObj.getOrganizationName());
            existingStakeHolder.setOrganizationAddress(modelObj.getOrganizationAddress());
            existingStakeHolder.setOrganizationMobNo(modelObj.getOrganizationMobNo());
            existingStakeHolder.setOrganizationUrl(modelObj.getOrganizationUrl());
        }
        existingStakeHolder.setCode(modelObj.getCode());
        existingStakeHolder.setName(modelObj.getName());
        existingStakeHolder.setStakeHolderType(modelObj.getStakeHolderType());
        existingStakeHolder.setGender(modelObj.getGender());
        existingStakeHolder.setDob(modelObj.getDob());
        existingStakeHolder.setMobileNumber(modelObj.getMobileNumber());
        existingStakeHolder.setEmailId(modelObj.getEmailId());
        existingStakeHolder.setLicenceNumber(modelObj.getLicenceNumber());
        existingStakeHolder.setAadhaarNumber(modelObj.getAadhaarNumber());
        existingStakeHolder.setPan(modelObj.getPan());
        existingStakeHolder.setSource(modelObj.getSource());
        existingStakeHolder.setBuildingLicenceIssueDate(modelObj.getBuildingLicenceIssueDate());
        existingStakeHolder.setBuildingLicenceExpiryDate(modelObj.getBuildingLicenceExpiryDate());
        existingStakeHolder.setIsActive(modelObj.getIsActive());
        existingStakeHolder.setStatus(StakeHolderStatus.RE_SUBMITTED);
        existingStakeHolder.setLastUpdatedUser(securityUtils.getCurrentUser());
        existingStakeHolder.setLastUpdatedDate(new Date());
        existingStakeHolder.setComments("");
        existingStakeHolder.addAddress(updateCorrespondenceAddress(modelObj, existingStakeHolder.getAddress()));
        existingStakeHolder.addAddress(updatePermanentAddress(modelObj, existingStakeHolder.getAddress()));
        return existingStakeHolder;
    }

    public StakeHolder validateStakeHolderIsRejected(final String mobileNo, final String email, final String aadhaarNo, final String panNo, final String licenseNo) {
        StakeHolder shWithMobNo = StringUtils.isBlank(mobileNo) ? null : findByMobileNumberAndStatus(mobileNo, StakeHolderStatus.REJECTED);
        StakeHolder shWithEmail = StringUtils.isBlank(email) ? null : findByEmailIdAndStatus(email, StakeHolderStatus.REJECTED);
        StakeHolder shWithAadhaar = StringUtils.isBlank(aadhaarNo) ? null : findByAadhaarNumberAndStatus(aadhaarNo, StakeHolderStatus.REJECTED);
        StakeHolder shWithPan = StringUtils.isBlank(panNo) ? null : findByPanNumberAndStatus(panNo, StakeHolderStatus.REJECTED);
        StakeHolder shWithLicenseNo = StringUtils.isBlank(licenseNo) ? null : findByLicenseNumberAndStatus(licenseNo, StakeHolderStatus.REJECTED);
        StakeHolder existingStakeholder = null;
        if (shWithMobNo != null) {
            existingStakeholder = shWithMobNo;
        } else if (shWithEmail != null) {
            existingStakeholder = shWithEmail;
        } else if (shWithAadhaar != null) {
            existingStakeholder = shWithAadhaar;
        } else if (shWithPan != null) {
            existingStakeholder = shWithPan;
        } else if (shWithLicenseNo != null) {
            existingStakeholder = shWithLicenseNo;
        }
        return existingStakeholder;
    }

    @SuppressWarnings("unchecked")
    public List<StakeHolder> search(final StakeHolder stakeHolder) {
        final Criteria criteria = buildSearchCriteria(stakeHolder);
        return criteria.list();
    }

    @SuppressWarnings("unchecked")
    public List<StakeHolder> getStakeHolderListByType(final StakeHolderType stkHldrType, final String name) {
        final Criteria criteria = getCurrentSession().createCriteria(StakeHolder.class, STK_HLDR);
        criteria.add(Restrictions.eq(STK_HLDR_TYPE, stkHldrType));
        criteria.add(Restrictions.ilike("stakeHolder.name", name, MatchMode.ANYWHERE));
        criteria.add(Restrictions.eq("stakeHolder.isActive", true));
        return criteria.list();
    }

    public Criteria buildSearchCriteria(final StakeHolder stkHldr) {
        final Criteria criteria = getCurrentSession().createCriteria(StakeHolder.class, STK_HLDR);

        if (stkHldr.getName() != null) {
            criteria.add(Restrictions.ilike("stakeHolder.name", stkHldr.getName(),
                    MatchMode.ANYWHERE));
        }
        if (stkHldr.getStakeHolderType() != null) {
            criteria.add(Restrictions.eq(STK_HLDR_TYPE, stkHldr.getStakeHolderType()));
        }
        if (stkHldr.getAadhaarNumber() != null) {
            criteria.add(Restrictions.ilike("stakeHolder.aadhaarNumber", stkHldr.getAadhaarNumber(),
                    MatchMode.ANYWHERE));
        }
        if (stkHldr.getPan() != null) {
            criteria.add(Restrictions.ilike("stakeHolder.pan", stkHldr.getPan(),
                    MatchMode.ANYWHERE));
        }
        if (stkHldr.getLicenceNumber() != null) {
            criteria.add(Restrictions.ilike("stakeHolder.licenceNumber", stkHldr.getLicenceNumber(),
                    MatchMode.ANYWHERE));
        }
        if (stkHldr.getStatus() != null) {
            criteria.add(Restrictions.eq("stakeHolder.status", stkHldr.getStatus()));
        }
        criteria.addOrder(Order.desc(STAKE_HOLDER_DOT_CREATED_DATE));
        criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
        return criteria;
    }

    public boolean checkIsEmailAlreadyExists(final StakeHolder stakeHolder) {
        return stakeHolderRepository.findByEmailId(stakeHolder.getEmailId()) == null ? false : true;
    }

    public boolean checkIsStakeholderCodeAlreadyExists(final StakeHolder stakeHolder) {
        return stakeHolderRepository.findByCode(stakeHolder.getCode()) == null ? false : true;
    }

    public List<SearchStakeHolderForm> searchForApproval(SearchStakeHolderForm srchStkHldrFrm) {
        return buildResponseAsPerForm(buildSearchCriteriaForApproval(srchStkHldrFrm));
    }

    private List<SearchStakeHolderForm> buildResponseAsPerForm(List<StakeHolder> stkHldrLst) {
        List<SearchStakeHolderForm> srchFrmLst = new ArrayList<>();
        for (StakeHolder stakeHolder : stkHldrLst) {
            SearchStakeHolderForm srchForm = new SearchStakeHolderForm();
            srchForm.setId(stakeHolder.getId());
            srchForm.setApplicantName(stakeHolder.getName());
            srchForm.setIssueDate(stakeHolder.getBuildingLicenceIssueDate());
            srchForm.setLicenceNumber(stakeHolder.getLicenceNumber());
            srchForm.setType(stakeHolder.getStakeHolderType().getStakeHolderTypeVal());
            srchForm.setActive(stakeHolder.getIsActive());
            srchForm.setStatus(stakeHolder.getStatus().getStakeHolderStatusVal());
            srchForm.setCreatedDate(stakeHolder.getCreatedDate());
            srchFrmLst.add(srchForm);
        }
        return srchFrmLst;
    }

    @SuppressWarnings("unchecked")
    private List<StakeHolder> buildSearchCriteriaForApproval(SearchStakeHolderForm srchStkHldrFrm) {
        final Criteria criteria = getCurrentSession().createCriteria(StakeHolder.class, STK_HLDR);
        if (srchStkHldrFrm.getFromDate() != null) {
            criteria.add(Restrictions.ge(STAKE_HOLDER_DOT_CREATED_DATE, searchBpaApplicationService.resetFromDateTimeStamp(srchStkHldrFrm.getFromDate())));
        }
        if (srchStkHldrFrm.getToDate() != null) {
            criteria.add(Restrictions.le(STAKE_HOLDER_DOT_CREATED_DATE, searchBpaApplicationService.resetToDateTimeStamp(srchStkHldrFrm.getToDate())));
        }
        if (srchStkHldrFrm.getStakeHolderType() != null) {
            criteria.add(Restrictions.eq(STK_HLDR_TYPE,
                    StakeHolderType.valueOf(srchStkHldrFrm.getStakeHolderType())));
        }
        criteria.add(Restrictions.in("stakeHolder.status", StakeHolderStatus.SUBMITTED, StakeHolderStatus.RE_SUBMITTED));
        criteria.addOrder(Order.desc(STAKE_HOLDER_DOT_CREATED_DATE));
        return criteria.list();
    }

    public StakeHolder updateForApproval(StakeHolder stakeHolder, String stkHldrStatus) {
        stakeHolder.setLastUpdatedUser(securityUtils.getCurrentUser());
        stakeHolder.setLastUpdatedDate(new Date());
        if ("Approve".equals(stkHldrStatus)) {
			stakeHolder.setUsername(stakeHolder.getEmailId());
			stakeHolder.updateNextPwdExpiryDate(environmentSettings.userPasswordExpiryInDays());
            setActiveToStakeholder(stakeHolder);
            stakeHolder.setStatus(StakeHolderStatus.APPROVED);
        } else if ("Reject".equals(stkHldrStatus)) {
            stakeHolder.setIsActive(false);
            stakeHolder.setActive(false);
            stakeHolder.setStatus(StakeHolderStatus.REJECTED);
            stakeHolder.setNoOfTimesRejected(stakeHolder.getNoOfTimesRejected() == null ? 1 : stakeHolder.getNoOfTimesRejected() + 1);
        } else if (BLOCK.equals(stkHldrStatus)) {
            stakeHolder.setStatus(StakeHolderStatus.BLOCKED);
            stakeHolder.setNoOfTimesBlocked(stakeHolder.getNoOfTimesBlocked() == null ? 1 : stakeHolder.getNoOfTimesBlocked() + 1);
        }
        return stakeHolderRepository.saveAndFlush(stakeHolder);
    }

	public List<StakeHolder> getStakeHoldersByType(StakeHolderType type) {
		return stakeHolderRepository.findActiveByType(type);
	}
}