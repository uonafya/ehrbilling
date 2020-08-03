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
<%@ include file="../includes/js_css.jsp"%>

<openmrs:require privilege="View Bills" otherwise="/login.htm"
	redirect="/module/ehrbilling/main.form" />
<spring:message var="pageTitle" code="ehrbilling.patient.find" scope="page" />
<openmrs:globalProperty key="hospitalcore.hospitalName"
	defaultValue="ddu" var="hospitalName" />
<br />
<p>
	<b><a href="searchDriver.form"><spring:message
				code="ehrbilling.ambulance" />
	</a>
	</b>&nbsp; | &nbsp; <b><a href="searchCompany.form"><spring:message
				code="ehrbilling.tender" />
	</a>
	</b>&nbsp; | &nbsp; <b><a href="miscellaneousServiceBill.list"><spring:message
				code="ehrbilling.miscellaneousService" />
	</a>
	</b>
</p>
<br />
<openmrs:require privilege="View Patients" otherwise="/login.htm"
	redirect="/index.htm" />

<script type="text/javascript">

	jQuery(document).ready(function(){
		jQuery("#searchbox").showPatientSearchBox({
			searchBoxView: "${hospitalName}/default",
			resultView: "/module/ehrbilling/patientsearch/${hospitalName}/main",
			rowPerPage: 15,
			beforeNewSearch: function(){
				jQuery("#patientSearchResultSection").hide();
			},
			success: function(data){
				jQuery("#patientSearchResultSection").show();
			}
		});
		

		jQuery("#billId", "#billSearch").keyup(function(event){				
			if(event.keyCode == 13){	
				jQuery("#billSearch").ajaxSubmit();
			}
		});
	});
</script>






<b class="boxHeader"><spring:message code="Patient.find" />
</b>
<div class="box" id="searchbox"></div>
<br />


<form id="billSearch" action="${pageContext.request.contextPath}/module/hospitalcore/findBill.htm" method="GET">
	<b class="boxHeader"><spring:message code="billing.Patient.find.byBillId" />
	</b>
	<div class="box" id="searchboxBillId">
		<table cellspacing="10">
			<tbody>
				<tr>
					<td>Bill Id </td>
					<td>
						<input id="billId" name="billId" style="width:300px;">
					</td>
					<td id="searchLoaderBillId"></td>
				</tr>
			</tbody>
		</table>
	</div>
	<br />
</form>
<!-- End-->

<div id="patientSearchResultSection" style="display: none;">
	<div class="boxHeader">Found Patients</div>
	<div class="box" id="patientSearchResult"></div>
</div>
<%
/**
* Search bill by Bill Id
*/
if (request.getParameter("Found") == null) {
} else {
    out.println("<font color='red'><b>"+request.getParameter("Found")+"</b>!</font>");
}
%>
<%@ include file="/WEB-INF/template/footer.jsp" %>