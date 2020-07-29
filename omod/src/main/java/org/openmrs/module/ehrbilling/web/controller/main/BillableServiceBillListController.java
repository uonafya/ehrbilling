/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.ehrbilling.web.controller.main;

import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.ehrbilling.includable.billcalculator.BillCalculatorService;
import org.openmrs.module.hospitalcore.BillingConstants;
import org.openmrs.module.hospitalcore.BillingService;
import org.openmrs.module.hospitalcore.model.PatientServiceBill;
import org.openmrs.module.hospitalcore.util.HospitalCoreUtils;
import org.openmrs.module.hospitalcore.util.PagingUtil;
import org.openmrs.module.hospitalcore.util.PatientUtils;
import org.openmrs.module.hospitalcore.util.RequestUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;


/**
 *
 */
@Controller
@RequestMapping("/module/ehrbilling/patientServiceBill.list")
public class BillableServiceBillListController {

	@RequestMapping(method=RequestMethod.GET)
	public String viewForm( Model model, @RequestParam("patientId") Integer patientId, @RequestParam(value="billId",required=false) Integer billId
	                        ,@RequestParam(value="pageSize",required=false)  Integer pageSize, 
		                    @RequestParam(value="currentPage",required=false)  Integer currentPage,
		                    HttpServletRequest request){
		
		BillingService billingService = Context.getService(BillingService.class);
		
		Patient patient = Context.getPatientService().getPatient(patientId);
		Map<String, String> attributes = PatientUtils.getAttributes(patient);
		BillCalculatorService calculator = new BillCalculatorService();		
		
		model.addAttribute("freeBill", calculator.isFreeBill(HospitalCoreUtils.buildParameters("attributes", attributes)));
		
		if( patient != null ){
			
			int total = billingService.countListPatientServiceBillByPatient(patient);
			// ghanshyam 12-sept-2012 Bug #357 [billing][3.2.7-SNAPSHOT] Error screen appears on clicking next page or changing page size in list of bills
			PagingUtil pagingUtil = new PagingUtil(RequestUtil.getCurrentLink(request), pageSize, currentPage, total, patientId);
			model.addAttribute("pagingUtil", pagingUtil);
			model.addAttribute("patient", patient);
			model.addAttribute("listBill", billingService.listPatientServiceBillByPatient(pagingUtil.getStartPos(), pagingUtil.getPageSize(), patient));
		}
		if( billId != null ){
			PatientServiceBill bill = billingService.getPatientServiceBillById(billId);			
			
			bill.setFreeBill(calculator.isFreeBill(HospitalCoreUtils.buildParameters("attributes", attributes)));
			model.addAttribute("bill", bill);
		}
		User user = Context.getAuthenticatedUser();
		
		model.addAttribute("canEdit", user.hasPrivilege(BillingConstants.PRIV_EDIT_BILL_ONCE_PRINTED) );		
		return "/module/ehrbilling/main/billableServiceBillList";
	}

	@RequestMapping(method=RequestMethod.POST)
	public String onSubmit(@RequestParam("patientId") Integer patientId, @RequestParam("billId") Integer billId){
		BillingService billingService = (BillingService)Context.getService(BillingService.class);
    	PatientServiceBill patientSerciceBill = billingService.getPatientServiceBillById(billId);
    	if( patientSerciceBill != null && !patientSerciceBill.getPrinted()){
    		patientSerciceBill.setPrinted(true);
    		Map<String, String> attributes = PatientUtils.getAttributes(patientSerciceBill.getPatient());
			BillCalculatorService calculator = new BillCalculatorService();
			patientSerciceBill.setFreeBill(calculator.isFreeBill(HospitalCoreUtils.buildParameters("attributes", attributes)));			
    		billingService.saveBillEncounterAndOrder(patientSerciceBill);
    	}
		return "redirect:/module/ehrbilling/patientServiceBill.list?patientId="+patientId;
	}
}
