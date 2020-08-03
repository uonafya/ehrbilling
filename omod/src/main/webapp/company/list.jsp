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

<openmrs:require privilege="View Companies" otherwise="/login.htm"
	redirect="/module/ehrbilling/main.form" />

<spring:message var="pageTitle" code="ehrbilling.company.manage"
	scope="page" />

<%@ include file="/WEB-INF/template/header.jsp"%>

<link type="text/css" rel="stylesheet"
	href="${pageContext.request.contextPath}/moduleResources/ehrbilling/styles/paging.css" />
<script type="text/javascript"
	src="${pageContext.request.contextPath}/moduleResources/ehrbilling/scripts/paging.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/moduleResources/ehrbilling/scripts/jquery/jquery-1.4.2.min.js"></script>
<h2>
	<spring:message code="ehrbilling.company.manage" />
</h2>
<p>
	<b><a href="searchCompany.form"><spring:message
				code="ehrbilling.tender" />
	</a>
	</b>
</p>
<br />
<c:forEach items="${errors.allErrors}" var="error">
	<span class="error"><spring:message
			code="${error.defaultMessage}" text="${error.defaultMessage}" />
	</span><
</c:forEach>
<input type="button"
	value="<spring:message code='ehrbilling.company.add'/>"
	onclick="javascript:window.location.href='company.form'" />

<br />
<br />
<c:choose>
	<c:when test="${not empty companies}">
		<form method="post" onsubmit="return false" id="form">
			<input type="button" onclick="checkValue()"
				value="<spring:message code='ehrbilling.company.deleteselected'/>" /> <span
				class="boxHeader"><spring:message code="ehrbilling.company.list" />
			</span>
			<div class="box">
				<table cellpadding="5" cellspacing="0">
					<tr>
						<th>#</th>
						<th><spring:message code="general.name" />
						</th>
						<th><spring:message code="ehrbilling.phone" />
						</th>
						<th><spring:message code="ehrbilling.address" />
						</th>
						<th><spring:message code="ehrbilling.createddate" />
						</th>
						<th></th>
					</tr>
					<c:forEach items="${companies}" var="company" varStatus="varStatus">
						<tr class='${varStatus.index % 2 == 0 ? "oddRow" : "evenRow" } '>
							<td><c:out
									value="${(( pagingUtil.currentPage - 1  ) * pagingUtil.pageSize ) + varStatus.count }" />
							</td>
							<td><a
								href="javascript:window.location.href='company.form?companyId=${company.companyId}'">${company.name
									}</a></td>
							<td>${company.phone }</td>
							<td>${company.address}</td>
							<td><openmrs:formatDate date="${company.createdDate}"
									type="textbox" />
							</td>
							<td><input type="checkbox" name="ids"
								value="${company.companyId}" />
							</td>
						</tr>
					</c:forEach>
					</form>
					<tr class="paging-container">
						<td colspan="7"><%@ include file="../paging.jsp"%></td>
					</tr>
				</table>
			</div>
			<script>
function checkValue()
{
	var form = jQuery("#form");
	if( jQuery("input[type='checkbox']:checked",form).length > 0 ) 
		form.submit();
	else{
		alert("Please choose items for deleting");
		return false;
	}
}</script>
	</c:when>
	<c:otherwise>
No company found.
</c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/template/footer.jsp"%>
