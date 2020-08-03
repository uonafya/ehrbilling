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
<%@ include file="/WEB-INF/template/header.jsp"%>
<openmrs:require privilege="View Bills" otherwise="/login.htm"
	redirect="/module/ehrbilling/main.form" />
<style type="text/css">
.hidden {
	display: none;
}
</style>
<link type="text/css" rel="stylesheet"
	href="${pageContext.request.contextPath}/moduleResources/ehrbilling/styles/paging.css" />
<script type="text/javascript"
	src="${pageContext.request.contextPath}/moduleResources/ehrbilling/scripts/paging.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/moduleResources/ehrbilling/scripts/jquery/jquery-1.4.2.min.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/moduleResources/ehrbilling/scripts/common.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/moduleResources/ehrbilling/scripts/jquery/jquery-ui-1.8.2.custom.min.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/moduleResources/ehrbilling/scripts/jquery/jquery.PrintArea.js"></script>
<c:forEach items="${errors}" var="error">
	<span class="error"><spring:message
			code="${error.defaultMessage}" text="${error.defaultMessage}" /> </span>
</c:forEach>
<div>
	<b><a href="addTenderBill.form?companyId=${companyId}">Add new
			Tender bill</a> </b>
</div>

<c:if test="${not empty tenderBill}">
	<div id="billContainer" style="margin: 10px auto; width: 981px;">
		<table>
			<tr>
				<td>Company name:</td>
				<td>${company.name }</td>
			</tr>
			<tr>
				<td>Address:</td>
				<td>${company.address}</td>
			</tr>
			<tr>
				<td>Date:</td>
				<td><openmrs:formatDate date="${tenderBill.createdDate}"
						type="textbox" /></td>
			</tr>
			<tr>
				<td>Bill ID:</td>
				<td>${tenderBill.receipt.id}</td>
			</tr>
		</table>
		<table width="100%" border="1">
			<tr> <!--Adjust allignment in table-->
				<th align="center">Service Name</th>
				<th align="center">Price (Rs)</th>
				<th align="center">Quantity</th>
				<th align="center">Amount</th>
			</tr>
			<c:forEach items="${tenderBill.billItems}" var="bill">
				<tr>
					<td>${bill.name}</td>
					<td align="right"><c:choose>
							<c:when test="${bill.unitPrice==0}"></c:when>
							<c:otherwise>${bill.unitPrice}</c:otherwise>
						</c:choose></td>
					<td align="right"><c:choose>
							<c:when test="${bill.quantity==0}"></c:when>
							<c:otherwise>${bill.quantity}</c:otherwise>
						</c:choose></td>
					<td align="right">${bill.amount}</td>
				</tr>
			</c:forEach>
			<tr> <!--Adjust allignment in table-->
				<td colspan="3" align="right"><b>Total</td>
				<td align="right">${tenderBill.amount}</td>
			</tr>
		</table>
		<br>
		<form method="POST" id="billForm">
			<center>
				<c:if test="${tenderBill.voided==false }">
					<input type="button"
						value='<spring:message code="billing.print" />'
						onClick="printDiv();" />&nbsp;&nbsp;</c:if>
				<a href="#" onclick="javascript:jQuery('#billContainer').hide();">Hide</a>
			</center>
		</form>
	</div>

	<!-- PRINT DIV -->

	<div id="printDiv" class="hidden"
		style="margin: 10px auto; width: 981px; font-size: 1.5em; font-family: 'Dot Matrix Normal', Arial, Helvetica, sans-serif;">
		<%-- <img
			src="${pageContext.request.contextPath}/moduleResources/billing/HEADEROPDSLIP.jpg"
			width="981" height="170"></img> --%>
		<table>
			<tr><br /></tr>
			<tr><br /></tr>
			<tr><br /></tr>
			<tr><br /></tr>
			<tr>
				<td>Company name:</td>
				<td>${company.name }</td>
			</tr>
			<tr>
				<td>Address:</td>
				<td>${company.address}</td>
			</tr>
			<tr>
				<td>Date:</td>
				<td><openmrs:formatDate date="${tenderBill.createdDate}"
						type="textbox" /></td>
			</tr>
			<tr>
				<td>Bill ID:</td>
				<td>${tenderBill.receipt.id}</td>
			</tr>
		</table>
		<table width="100%" border="1">
			<thead>
				<th>Service Name</th>
				<th>Price (Rs)</th>
				<th>Quantity</th>
				<th>Amount</th>
			</thead>
			<c:forEach items="${tenderBill.billItems}" var="bill">
				<tr>
					<td>${bill.name}</td>
					<td align="right"><c:choose>
							<c:when test="${bill.unitPrice==0}"></c:when>
							<c:otherwise>${bill.unitPrice}</c:otherwise>
						</c:choose></td>
					<td align="right"><c:choose>
							<c:when test="${bill.quantity==0}"></c:when>
							<c:otherwise>${bill.quantity}</c:otherwise>
						</c:choose></td>
					<td align="right">${bill.amount}</td>
				</tr>
			</c:forEach>
			<tr>
				<td colspan="3">Total</td>
				<td align="right">${tenderBill.amount}</td>
			</tr>
		</table>
		<br> <span style="font-size: 1.5em">Total Amount:</span> <span
			id="totalValue" style="font-size: 1.5em"></span> <br /> <br /> <br />
		<br /> <br /> <br /> <span style="float: right; font-size: 1.5em">Signature
			of billing clerk/ Stamp</span>
	</div>

	<!-- END PRINT DIV -->
</c:if>


<!--List old bills -->
<div>
	<c:if test="${not empty tenderBills}">  <!--Issue 387 --update title-->
		<span class="boxHeader">List of Tender bills</span>
		<table class="box">
			<thead>
				<th>#</th>
				<th>Bill Name</th>
				<th>Action</th>
			</thead>
			<c:forEach items="${tenderBills}" var="tenderBill"
				varStatus="varStatus">
				<tr
					class='${varStatus.index % 2 == 0 ? "oddRow" : "evenRow" } <c:if test="${tenderBill.voided}">retired </c:if>'>
					<td><c:out
							value="${(( pagingUtil.currentPage - 1  ) * pagingUtil.pageSize ) + varStatus.count }" />
					</td>
					<td><c:choose>
							<c:when
								test="${tenderBill.voided == false && ( tenderBill.printed == false || ( tenderBill.printed == true && canEdit == true ))}">
								<a
									href="${pageContext.request.contextPath}/module/ehrbilling/editTenderBill.form?tenderBillId=${tenderBill.tenderBillId}&companyId=${company.companyId }">Bill
									ID <b>${tenderBill.receipt.id}</b>, <openmrs:formatDate
										date="${tenderBill.createdDate }" type="textbox" /> </a>
							</c:when>
							<c:otherwise>
		             	Bill ID <b>${tenderBill.receipt.id}</b>, <openmrs:formatDate
									date="${tenderBill.createdDate }" />
							</c:otherwise>
						</c:choose>
					</td>
					<td><input type="button" value="View"
						onclick="javascript:window.location.href='tenderBill.list?companyId=${company.companyId}&tenderBillId=${tenderBill.tenderBillId}'" />
					</td>
				</tr>

			</c:forEach>
			<tr class="paging-container">
				<td colspan="3"><%@ include file="../paging.jsp"%></td>
			</tr>
		</table>
	</c:if>

	<input type="hidden" id="total" value="${tenderBill.amount}">

	<script>
		function printDiv() {
			jQuery("div#printDiv").printArea({
				mode : "popup",
				popClose : true
			});
			jQuery("#billForm").submit();
		}
		jQuery(document).ready(function() {
			jQuery("#totalValue").html(toWords(jQuery("#total").val()));
		});
	</script>

	<%@ include file="/WEB-INF/template/footer.jsp"%>
</div>