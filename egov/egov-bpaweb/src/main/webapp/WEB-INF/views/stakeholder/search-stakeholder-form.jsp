<%--
  ~    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
  ~    accountability and the service delivery of the government  organizations.
  ~
  ~     Copyright (C) 2017  eGovernments Foundation
  ~
  ~     The updated version of eGov suite of products as by eGovernments Foundation
  ~     is available at http://www.egovernments.org
  ~
  ~     This program is free software: you can redistribute it and/or modify
  ~     it under the terms of the GNU General Public License as published by
  ~     the Free Software Foundation, either version 3 of the License, or
  ~     any later version.
  ~
  ~     This program is distributed in the hope that it will be useful,
  ~     but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~     GNU General Public License for more details.
  ~
  ~     You should have received a copy of the GNU General Public License
  ~     along with this program. If not, see http://www.gnu.org/licenses/ or
  ~     http://www.gnu.org/licenses/gpl.html .
  ~
  ~     In addition to the terms of the GPL license to be adhered to in using this
  ~     program, the following additional terms are to be complied with:
  ~
  ~         1) All versions of this program, verbatim or modified must carry this
  ~            Legal Notice.
  ~            Further, all user interfaces, including but not limited to citizen facing interfaces,
  ~            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
  ~            derived works should carry eGovernments Foundation logo on the top right corner.
  ~
  ~            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
  ~            For any further queries on attribution, including queries on brand guidelines,
  ~            please contact contact@egovernments.org
  ~
  ~         2) Any misrepresentation of the origin of the material is prohibited. It
  ~            is required that all modified versions of this material be marked in
  ~            reasonable ways as different from the original version.
  ~
  ~         3) This license does not grant any rights to any user of the program
  ~            with regards to rights under trademark law for use of the trade names
  ~            or trademarks of eGovernments Foundation.
  ~
  ~   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
  ~
  --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<form:hidden id="mode" path="" value="${mode}"/>
<div class="row">
	<div class="col-md-12">
		<div class="panel panel-primary" data-collapsed="0">
			<div class="panel-heading">
				<div class="panel-title"><spring:message code="lbl.search.building.licensee"/></div>
			</div>
			<div class="panel-body">
				<div class="form-group">
					<label class="col-sm-3 control-label text-right"><spring:message code="lbl.stakeholder.type" /></label>
					<div class="col-sm-3 add-margin">
						<select name="stakeHolderType" id="stakeHolderType"
								class="form-control">
							<option value=""><spring:message code="lbl.select" /></option>
							<c:forEach items="${stakeHolderTypes}" var="stkhldrtype">
								<option value="${stkhldrtype.id}">${stkhldrtype.name}</option>
							</c:forEach>
						</select>
						<form:errors path="stakeHolderType" cssClass="error-msg" />
					</div>
					<c:if test="${mode =='view'}">
						<label class="col-sm-2 control-label"><spring:message
								code="lbl.status" /></label>
						<div class="col-sm-3 add-margin">
							<select name="status" id="status"
									class="form-control">
								<option value=""><spring:message code="lbl.select" /></option>
								<c:forEach items="${stakeHolderStatusList}" var="stkhldrStatus">
									<option value="${stkhldrStatus}">${stkhldrStatus.stakeHolderStatusVal}</option>
								</c:forEach>
							</select>
						</div>
					</c:if>
				</div>
<!-- edit by Shuller 
				<div class="form-group">
					<label class="col-sm-3 control-label text-right"><spring:message
							code="lbl.aadhar" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" cssClass="form-control"
							path="aadhaarNumber" id="aadhaarNumber" />
						<form:errors path="aadhaarNumber" maxlength="12"
							cssClass="error-msg" />
					</div>
					<label class="col-sm-2 control-label text-right"><spring:message
							code="lbl.pan" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" cssClass="form-control" path="pan"
							id="panNumber" maxlength="10" />
						<form:errors path="pan" cssClass="error-msg" />
					</div>
				</div> -->
				
				<div class="form-group">
					<label class="col-sm-3 control-label text-right"><spring:message
							code="lbl.applicant.name" /></label>
					<div class="col-sm-3 add-margin">
						<form:input path="name"
									class="form-control text-left patternvalidation"
									data-pattern="alphanumeric" maxlength="100" />
						<form:errors path="name" cssClass="error-msg" />
					</div>
					<label class="col-sm-2 control-label text-right"><spring:message
							code="lbl.lic.no" /></label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" cssClass="form-control"
							path="licenceNumber" id="licenceNumber" />
						<form:errors path="licenceNumber" cssClass="error-msg" />
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
