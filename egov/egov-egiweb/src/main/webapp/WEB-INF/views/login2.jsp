
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

<%@page import="org.apache.commons.lang3.StringUtils"%>
<%
    String ipAddress = request.getRemoteAddr();
			String proxiedIPAddress = request.getHeader("X-Forwarded-For");
			if (StringUtils.isNotBlank(proxiedIPAddress)) {
				String[] ipAddresses = proxiedIPAddress.split(",");
				ipAddress = ipAddresses[ipAddresses.length - 1].trim();
			}
			String userAgentInfo = request.getHeader("User-Agent");
%>
<!DOCTYPE html>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri="/WEB-INF/taglib/cdn.tld" prefix="cdn"%>
<html lang="en">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport"
	content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
<meta name="description" content="eGov Urban Portal" />
<meta name="author" content="eGovernments Foundation" />
<title>eGov Urban Portal Login</title>
<link rel="icon"
	href="<cdn:url value='/resources/global/images/favicon.png'/>"
	sizes="32x32">
<link rel="stylesheet"
	href="<cdn:url value='/resources/global/css/bootstrap/bootstrap.css'/>">
<link rel="stylesheet"
	href="<cdn:url value='/resources/global/css/font-icons/font-awesome/css/font-awesome.min.css'/>">
<link rel="stylesheet"
	href="<cdn:url value='/resources/global/css/egov/customlogin.css?rnd=${app_release_no}'/>">
<script src="<cdn:url value='/resources/global/js/jquery/jquery.js'/>"
	type="text/javascript"></script>

<!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!--[if lt IE 9]>
			<script src="<cdn:url value='/resources/global/js/ie8/html5shiv.min.js'/>"></script>
			<script src="<cdn:url value='/resources/global/js/ie8/respond.min.js'/>"></script>
		<![endif]-->
</head>
<body class="page-body index"
	style="height: 580px; background: #F8F9F9;">
	<div class="page-container">
		<div class="main-content">
			<nav class="navbar transparent">
				<div class="container-fluid">
					<div class="navbar-header">
						<%-- <a class="navbar-brand" href="javascript:void(0);">
					<img src="https://www.egovernments.org/wp-content/uploads/2018/11/Logo-2.png" style="background-color:#3b73af" height="90">
						</a> --%>
						<div>
							<span class="title2"
								style="color: black; font-family: OpenSans; font-size: 30px"><b>${sessionScope.citymunicipalityname}</b></span>
						</div>
					</div>
					<div class="navbar-right">
						<a href="http://www.egovernments.org" data-strwindname="egovsite"
							class="open-popup"> <img
							src="<cdn:url value='/resources/global/images/egov_logo_tr_h.png'/>"
							title="Customised by ITM Pamodzi"
 height="64" alt="">
						</a>
					</div>
				</div>
			</nav>

			<div class="row top-space">
				<div class="text-center error-msg">
					<noscript>You don't have javascript enabled. Make sure
						Javascript is enabled.</noscript>
				</div>
				<div class="col-md-6 side-space">
					<div class="col-md-12 community-card">
						<a href="#"
							onclick="window.open('/portal/citizen/signup','CTZ',config='height=800, width=1100, toolbar=no, menubar=no, scrollbars=yes, resizable=no,location=no, directories=no, status=no')">
							<div class="rounded-circle">
								<i class="fa fa-user a"></i>
							</div>&nbsp;
							<div class="label-font">
								<spring:message code="lbl.create.ac.citizen" />
								<br> <span class="label-subfont"><spring:message
										code="lbl.create.ac.desc" /></span>
							</div>
						</a>
					</div>

					<div class="col-md-12 community-card"<%--style="opacity: 0.75"--%>>
						<a href="#"
							onclick="window.open('/bpa/stakeholder/createbycitizen','BL',config='height=800, width=1100, toolbar=no, menubar=no, scrollbars=yes, resizable=no,location=no, directories=no, status=no')">
							<div class="rounded-circle">
								<i class="fa fa-user a"></i>
							</div>&nbsp;
							<div class="label-font">
								<spring:message code="lbl.bldng.create.ac" />
								<br> <span class="label-subfont"><spring:message
										code="lbl.create.ac.desc" /></span>
							</div>
						</a>
					</div>
					<div class="col-md-12 community-card">
					<div class="rounded-circle">
							<i class="fa fa-search c"></i>
						</div>
						<div class="label-font" style="vertical-align: sub">
							<spring:message code="lbl.check.application.status" />
							&nbsp;<br> <input type="text"
								placeholder="Enter application number" id="appsearchtxt"
								style="padding: 2px 5px; height: 30px; font-size: 14px; border: 0; padding-left: 0; border-bottom: 1px solid #D0D2D7; outline: none; box-shadow: none; width: initial;">
							<button class="btn btn-custom btn-small" id="appsearch" style="font-size: 12px;">
								<spring:message code="btn.lbl.search" />
							</button>
						</div>
						<div class="error-msg search-error-msg display-hide"
							style="padding-left: 65px;">Application number is mandatory</div>
					</div>
					<div class="col-md-12 community-card transparent text-center">
					<img src="<cdn:url value='/resources/guide/assets/digit_dcr_glow_logo.png'/>" height=128>
					</div>
					<div class="col-md-12  community-card" style="position: relative; top: -60px;">
						
								<h6 class="text-center view-content">Building
									Planning Approval</h6>
								<p class="text-justify view-content">
									Citizens, ULB officials, and other stakeholders can
										submit and track applications in real time and obtain
										approvals without having to physically visit an office.
								</p>
					</div>
				</div>

				<div class="col-md-6 side-space login-form-licensee">
					<!-- style="position: relative; left: 63px; top: 60px" -->

					<div class="signin-formcontent signin-section">
						<form method="post" role="form" id="signform"
							action="${pageContext.request.contextPath}/j_security_check"
							autocomplete="off"<%--style="opacity: 0.75"--%>>
							<div class="form-group">
								<div class="signin-title">
									<spring:message code="lbl.login" />
								</div>
							</div>
							<input style="display: none" type="text"> <input
								style="display: none" type="password" />
							<div class="form-group">
								<div class="input-group">
									<div class="input-group-addon style-label">
										<i class="fa fa-user theme-color style-color"></i>
									</div>
									<input type="text" class="form-control style-form"
										name="j_username" id="j_username"
										placeholder="Mobile Number / Login ID" autocomplete="off"
										required="required" autofocus="autofocus" /> <label
										id="j_username-error" class="error pull-right"
										for="j_username"></label>
								</div>
							</div>
							<div class="form-group overflow">
								<div class="input-group">
									<div class="input-group-addon style-label">
										<i class="fa fa-key theme-color style-color"></i>
									</div>
									<input type="password" class="form-control style-form"
										name="j_password" id="j_password" placeholder="Password"
										autocomplete="new-password" required="required" />
									<div class="input-group-addon font-12" data-toggle="modal"
										data-target="#fpassword" data-backdrop="static">Forgot?
									</div>
								</div>
								<label id="j_password-error" class="error align-top pull-right"
									for="j_password"></label>
							</div>
							<div class="form-group display-hide" id="counter-section">
								<div class="input-group">
									<div class="input-group-addon style-label">
										<i class="fa fa-map-marker theme-color style-color"></i>
									</div>
									<select class="form-control style-form" name="locationId"
										id="locationId"></select> <label id="locationId-error"
										class="error pull-right" for="locationId"></label>
								</div>
							</div>
							<c:if test="${param.error}">
								<div class="text-center error-msg add-margin">
									<c:set var="security_message"
										value="${sessionScope.SPRING_SECURITY_LAST_EXCEPTION.message}" />
									<c:choose>
										<c:when
											test="${security_message.contains('Maximum sessions')}">
											<spring:message code="msg.multiple.login" />
										</c:when>
										<c:when test="${security_message.contains('expired')}">
											<spring:message code="msg.cred.exprd1" />
											<a href="javascript:void(0);" data-toggle="modal"
												data-target="#fpassword" data-backdrop="static"> <spring:message
													code="msg.cred.exprd2" />
											</a>
											<spring:message code="msg.cred.exprd3" />
										</c:when>
										<c:when
											test="${fn:contains(security_message, 'User account is locked')}">
											<spring:message code="msg.acc.locked" />
											<spring:eval
												expression="@environment.getProperty('captcha.strength')"
												var="strength" />
											<c:import url="/WEB-INF/views/common/captcha-${strength}.jsp"
												context="/egi" />
											<c:if
												test="${fn:contains(security_message, 'Recaptcha Invalid')}">
												<spring:message code="err.recaptcha.invalid" />
											</c:if>
										</c:when>
										<c:when
											test="${fn:contains(security_message, 'Too many attempts')}">
											<c:set var="attempts"
												value="${fn:substringAfter(security_message, 'Too many attempts')}" />
											<spring:message code="msg.acc.toomany.attempt"
												arguments="${attempts}" />
										</c:when>
										<c:otherwise>
											<div class="form-group">
												<div>
													<spring:message code="msg.cred.invalid" />
												</div>
											</div>
										</c:otherwise>
									</c:choose>
								</div>
							</c:if>
							<c:if test="${not empty param.reset}">
								<div class="form-group">
									<c:choose>
										<c:when test="${param.reset}">
											<div class="text-center success-msg font-12">
												<spring:message code="msg.success.pwd.reset" />
											</div>
										</c:when>
										<c:otherwise>
											<div class="text-center  error-msg font-12">
												<spring:message code="msg.fail.pwd.reset" />
											</div>
										</c:otherwise>
									</c:choose>
								</div>
							</c:if>
							<div class="form-group signin-leftpadding">
								<button type="submit"
									class="btn btn-custom btn-block btn-login signin-submit"
									id="signin-action">
									<i class="fa fa-sign-in"></i>
									<spring:message code="lbl.login" />
								</button>
							</div>
							<input type="hidden" id="ipAddress" name="ipAddress"
								value="<%=ipAddress%>" /> <input type="hidden" id="loginType"
								name="loginType" /> <input type="hidden" name="userAgentInfo"
								value="<%=userAgentInfo%>" />
						</form>
						<div style="color: white; height: 30px"></div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div>
		<%@include file="../../resources/guide/landingPage.jsp"%>
	</div>


	<%-- <footer class="main">
				<div class="col-md-12 feature-top text-center"></div>
				<div class="col-md-4 top-community-space login-footer-icon" style=" text-align: center ;>
					<a href="${sessionScope.corpGisLink}" target="_blank"><span><i
							class="fa fa-map-marker fa-3x"></i></span></a>
					<p style="margin: 0px">
						${sessionScope.corpAddress}
					</p>
					<a class="block-left-text" href="${sessionScope.corpGisLink}"
					   target="_blank">Find us on google maps</a>
				</div>
					<div class="col-md-4 top-community-space login-footer-icon" style=" text-align: center ;>
					<a href="tel:${sessionScope.corpContactNo}"><span><i
							class="fa fa-phone fa-3x"></i></span></a>
					<p style="margin: 0px">${sessionScope.corpContactNo}</p>
					<a href="mailto:${sessionScope.corpContactEmail}"><p>${sessionScope.corpContactEmail}</p></a>
				</div>
				<div class="col-md-4 top-community-space login-footer-icon" style=" text-align: center;">
					<a href="javascript:void(0)"><span><i
							class="fa fa-thumbs-o-up fa-2x"></i></span></a>
					<p>Follow us on</p>
					<a href="${sessionScope.corpTwitterLink}" target="_blank"><i
							class="fa fa-twitter fa-2x"></i></a> <a
						href="${sessionScope.corpFBLink}" target="_blank"><i
						class="fa fa-facebook fa-2x"></i></a>
				</div>
		</div>
		</div>
		</div>
		</footer> --%>
	<div class="modal fade" id="fpassword" tabindex="-1" role="dialog"
		aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title" id="myModalLabel">
						<spring:message code="lbl.recover.pwd" />
					</h4>
				</div>
				<form method="post" role="form" id="forgotPasswordForm">
					<div class="modal-body">
						<div class="form-group">
							<div class="input-group" style="margin: 0;">
								<div class="input-group-addon style-label">
									<i class="fa fa-user style-color"></i>
								</div>
								<input type="text" class="form-control style-form"
									name="identity" id="emailOrMobileNum" required="required"
									placeholder="Mobile Number / Login ID" autocomplete="off" /> <input
									type="hidden" name="originURL" id="originURL"> <input
									type="hidden" name="byOTP" id="byOtp">
							</div>
							<div id="emailOrMobileNoReq"
								class="text-right error-msg display-hide">
								<spring:message code="lbl.pwd.recover.un.req" />
							</div>
							<div class="text-right" style="font-size: 12px; color: #6b4f2c;">
								<spring:message code="lbl.pwd.reset.link" />
							</div>
						</div>
					</div>
					<div class="modal-footer">
						<div class="form-group text-right">
							<button type="button" class="btn btn-primary recovrbtn">
								<spring:message code="btn.lbl.recover.link" />
							</button>
							<button type="button" id="recoveryotpbtn"
								class="btn btn-primary recovrbtn">
								<spring:message code="btn.lbl.recover.otp" />
							</button>
							<button type="button" class="btn btn-default"
								data-dismiss="modal">
								<spring:message code="lbl.close" />
							</button>
						</div>
					</div>
				</form>
			</div>
		</div>
	</div>
	<div class="modal fade" id="cookieornoscript" data-backdrop="static">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<h4 class="modal-title">Enable Cookies</h4>
				</div>
				<div class="modal-body">Your browser seems to have cookies
					disabled. Make sure cookies are enabled or try opening a new
					browser window.</div>
			</div>
		</div>
	</div>
	<c:if test="${not empty param.recovered}">
		<div class="modal fade" data-backdrop="static" id="resetpwd">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<h4 class="modal-title">
							<spring:message code="lbl.recover.pwd" />
						</h4>
					</div>
					<form method="post" role="form">
						<c:choose>
							<c:when test="${param.recovered}">
								<c:if test="${param.byOTP}">
									<div class="modal-body">
										<div class="form-group">
											<div class="input-group" style="margin: 0;">
												<div class="input-group-addon style-label">
													<i class="fa fa-key theme-color style-color"></i>
												</div>
												<input style="display: none" type="password"> <input
													type="password" class="form-control style-form"
													name="token" id="token" placeholder="Enter your OTP"
													autocomplete="new-password" required="required" /> <span
													class="mandatory set-mandatory"></span>
											</div>
											<div class="text-right font-12">OTP sent to your
												registered mobile / email</div>
										</div>
									</div>
									<div class="modal-footer">
										<button type="button"
											class="btn btn-custom recovrbtn text-right"
											id="otprecoverybtn">
											<spring:message code="title.reset.password" />
										</button>
									</div>
								</c:if>
								<c:if test="${not param.byOTP}">
									<div class="modal-body">
										<div class="text-center font-12">
											<spring:message
												code="msg.success.pwd.recov.otp.${param.byOTP}" />
										</div>
									</div>
									<div class="modal-footer">
										<button type="button" class="btn btn-default text-right"
											data-dismiss="modal">Close</button>
									</div>
								</c:if>
							</c:when>
							<c:otherwise>
								<div class="modal-body">
									<div class="text-center error-msg">
										<spring:message code="msg.fail.pwd.recov" />
									</div>
								</div>
								<div class="modal-footer">
									<button type="button" class="btn btn-default text-right"
										data-dismiss="modal">Close</button>
								</div>
							</c:otherwise>
						</c:choose>
					</form>
				</div>
			</div>
		</div>
		<script>
			$(document).ready(function() {
				$('#resetpwd').modal('show', {
					backdrop : 'static'
				});
			});
		</script>
	</c:if>

	<script
		src="<cdn:url value='/resources/global/js/bootstrap/bootstrap.js'/>"
		type="text/javascript"></script>
	<script
		src="<cdn:url value='/resources/global/js/egov/custom.js?rnd=${app_release_no}'/>"
		type="text/javascript"></script>
	<script
		src="<cdn:url value='/resources/global/js/jquery/plugins/jquery.validate.min.js'/>"></script>
	<script
		src="<cdn:url value='/resources/js/app/login.js?rnd=${app_release_no}'/>"
		type="text/javascript"></script>
</body>
</html>