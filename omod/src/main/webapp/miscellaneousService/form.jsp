<%--
* This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
--%>

<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:require privilege="Add/Edit MiscellaneousService"
	otherwise="/login.htm" redirect="/module/ehrbilling/main.form" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<h2>
	<spring:message code="ehrbilling.miscellaneousService.manage" />
</h2>

<c:forEach items="${errors.allErrors}" var="error">
	<span class="error"><spring:message
			code="${error.defaultMessage}" text="${error.defaultMessage}" />
	</span>
</c:forEach>
<spring:bind path="miscellaneousService">
	<c:if test="${not empty  status.errorMessages}">
		<div class="error">
			<ul>
				<c:forEach items="${status.errorMessages}" var="error">
					<li>${error}</li>
				</c:forEach>
			</ul>
		</div>
	</c:if>
</spring:bind>
<form method="post" class="box">
	<table>
		<tr>
			<td><spring:message code="general.name" />
			</td>
			<td><spring:bind path="miscellaneousService.name">
					<input type="text" name="${status.expression}"
						value="${status.value}" size="35" />
					<c:if test="${status.errorMessage != ''}">
						<span class="error">${status.errorMessage}</span>
					</c:if>
				</spring:bind></td>
		</tr>
		<tr>
			<td valign="top"><spring:message code="ehrbilling.description" />
			</td>
			<td><spring:bind path="miscellaneousService.description">
					<input type="text" name="${status.expression}"
						value="${status.value}" size="35" />
					<c:if test="${status.errorMessage != ''}">
						<span class="error">${status.errorMessage}</span>
					</c:if>
				</spring:bind></td>
		</tr>
		<tr>
			<td valign="top"><spring:message code="ehrbilling.price" />
			</td>
			<td><spring:bind path="miscellaneousService.price">
					<input type="text" name="${status.expression}"
						value="${status.value}" size="35" />
					<c:if test="${status.errorMessage != ''}">
						<span class="error">${status.errorMessage}</span>
					</c:if>
				</spring:bind></td>
		</tr>
		<tr>
			<td><spring:message code="general.retired" />
			</td>
			<td><spring:bind path="miscellaneousService.retired">
					<openmrs:fieldGen type="java.lang.Boolean"
						formFieldName="${status.expression}" val="${status.editor.value}"
						parameters="isNullable=false" />
					<c:if test="${status.errorMessage != ''}">
						<span class="error">${status.errorMessage}</span>
					</c:if>
				</spring:bind></td>
		</tr>
	</table>
	<br /> <input type="submit"
		value="<spring:message code="general.save"/>"> <input
		type="button" value="<spring:message code="general.cancel"/>"
		onclick="javascript:window.location.href='miscellaneousService.list'">
</form>
<%@ include file="/WEB-INF/template/footer.jsp"%>