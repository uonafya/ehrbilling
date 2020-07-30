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
			code="${error.defaultMessage}" text="${error.defaultMessage}" />
	</span>
</c:forEach>
<div>
	<b><a href="addMiscellaneousServiceBill.form">Add Miscellaneous
			Service bill</a>
	</b>
</div>
<br />
<br />
<c:if test="${not empty bill}">
	<div id="billContainer" style="margin: 10px auto; width: 981px;">
		<table>
			<tr>
				<td>Liable name:</td>
				<td>${bill.liableName }</td>
			</tr>
			<tr>
				<td>Date:</td>
				<td><openmrs:formatDate date="${bill.createdDate}"
						type="textbox" />
				</td>
			</tr>
			<tr>
				<td>Bill ID:</td>
				<td>${bill.receipt.id}</td>
			</tr>
		</table>
		<table width="100%" border="1">
			<tr> <!-- Sept 22,2012 -- Sagar Bele -- Issue 387 --Adjust allignment in table-->
				<th align="center">Service</th>
				<th align="center">Quantity</th>
				<th align="center">Price (Rs)</th>
			</tr>
			<tr> <!-- Sept 22,2012 -- Sagar Bele -- Issue 387 --Adjust allignment in table-->
				<td align="left">${bill.service.name}</td>
				<td align="right">${bill.quantity}</td>
				<td align="right">${bill.amount}</td>
			</tr>
			<tr>
				<td></td> <!-- Sept 22,2012 -- Sagar Bele -- Issue 387 --Adjust allignment in table-->
				<td  align="right"><b>Total</b></td>				
				<td align="right">${bill.amount}</td>
			</tr>
		</table>
		<br>
		<form method="POST" id="billForm">
			<center>
				<c:if test="${bill.voided==false }">
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
		<%-- ghanshyam 18-sept-2012 Support #386 [Solan][billing-3.2.7 snap shot]-misc services print out(note:-commented below line for that) --%>
		<%-- 
		<img
			src="${pageContext.request.contextPath}/moduleResources/billing/HEADEROPDSLIP.jpg"
			width="981" height="170"></img>
		--%>
			
		<table>
			<tr><br /></tr>
			<tr><br /></tr>
			<tr><br /></tr>
			<tr><br /></tr>
			<tr>
				<td>Liable name:</td>
				<td>${bill.liableName }</td>
			</tr>
			<tr>
				<td>Date:</td>
				<td><openmrs:formatDate date="${bill.createdDate}"
						type="textbox" />
				</td>
			</tr>
			<tr>
				<td>Bill ID:</td>
				<td>${bill.receipt.id}</td>
			</tr>
		</table>
		<table width="100%" border="1">
			<tr>
				<th align="center">Service</th>
				<th align="center">Quantity</th>
				<th align="center">Price (Rs)</th>
			</tr>
			<tr>
				<td align="center">${bill.service.name}</td>
				<td align="center">${bill.quantity}</td>
				<td align="center">${bill.amount}</td>
			</tr>
			<tr>
				<td></td>
				<td align="center"><b>Total</b></td>				
				<td align="center">${bill.amount}</td>
			</tr>
		</table>
		<br> <span style="font-size: 1.5em">Total Amount:</span> <span
			id="totalValue" style="font-size: 1.5em"></span> <br />
		<br />
		<br />
		<br />
		<br />
		<br /> <span style="float: right; font-size: 1.5em">Signature
			of billing clerk/ Stamp</span>
	</div>

	<!-- END PRINT DIV -->
</c:if>


<!--List old bills -->
<div>
	<form method="GET" id="selectForm">
		Service : <select name="serviceId"
			onchange="javascript:jQuery('#selectForm').submit()">
			<option value="">--Select--</option>
			<c:forEach items="${listServices}" var="service">
				<option value="${service.id }"
					<c:if test="${service.id == serviceId }">selected</c:if>>${service.name
					}</option>
			</c:forEach>
		</select>
	</form>
	<c:if test="${not empty listBills}"> <!-- Sept 22,2012 -- Sagar Bele -- Issue 387 --update title-->
		<span class="boxHeader">List of Miscellaneous Services Bills</span>
		<table class="box">
			<thead>
				<th>#</th>
				<th>Bill Name</th>
				<th>Name</th>
				<th>Service</th>
				<th>Action</th>
			</thead>
			<c:forEach items="${listBills}" var="bill" varStatus="varStatus">
				<tr
					class='${varStatus.index % 2 == 0 ? "oddRow" : "evenRow" } <c:if test="${bill.voided}">retired </c:if> '>
					<td><c:out
							value="${(( pagingUtil.currentPage - 1  ) * pagingUtil.pageSize ) + varStatus.count }" />
					</td>
					<td><c:choose>
							<c:when
								test="${ bill.voided == false && (  bill.printed == false || ( bill.printed == true && canEdit == true ))}">
								<a
									href="${pageContext.request.contextPath}/module/ehrbilling/editMiscellaneousServiceBill.form?billId=${bill.id}">Bill
									ID <b>${bill.receipt.id}</b>, <openmrs:formatDate
										date="${bill.createdDate }" type="textbox" />
								</a>
							</c:when>
							<c:otherwise>
		             	Bill ID <b>${bill.receipt.id}</b>, <openmrs:formatDate
									date="${bill.createdDate }" />
							</c:otherwise>
						</c:choose></td>
					<td>${bill.liableName }</td>
					<td>${bill.service.name }</td>
					<td><input type="button" value="View"
						onclick="javascript:window.location.href='miscellaneousServiceBill.list?billId=${bill.id}'" />
					</td>
				</tr>

			</c:forEach>
			<tr class="paging-container">
				<td colspan="3"><%@ include file="../paging.jsp"%></td>
			</tr>
		</table>
	</c:if>

	<input type="hidden" id="total" value="${bill.amount}">

	<script>
function printDiv()
{
  	jQuery("div#printDiv").printArea({mode:"popup",popClose:true});
	jQuery("#billForm").submit();
}
jQuery(document).ready(function(){
	jQuery("#totalValue").html(toWords(jQuery("#total").val()));
});
</script>

	<%@ include file="/WEB-INF/template/footer.jsp"%>
</div>