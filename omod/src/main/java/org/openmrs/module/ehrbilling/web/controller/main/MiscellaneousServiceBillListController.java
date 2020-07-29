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

import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.hospitalcore.BillingConstants;
import org.openmrs.module.hospitalcore.BillingService;
import org.openmrs.module.hospitalcore.model.MiscellaneousService;
import org.openmrs.module.hospitalcore.model.MiscellaneousServiceBill;
import org.openmrs.module.hospitalcore.util.PagingUtil;
import org.openmrs.module.hospitalcore.util.RequestUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;


/**
 * New Requirement : Add field quantity in Miscellaneous Services Bill
 */
@Controller
@RequestMapping("/module/ehrbilling/miscellaneousServiceBill.list")
public class MiscellaneousServiceBillListController {

	@RequestMapping(method=RequestMethod.POST)
	public String printBill(@RequestParam("billId") Integer miscellaneousServiceBillId){
		BillingService billingService = (BillingService)Context.getService(BillingService.class);
    	MiscellaneousServiceBill miscellaneousServiceBill = billingService.getMiscellaneousServiceBillById(miscellaneousServiceBillId);
    	if( miscellaneousServiceBill != null && !miscellaneousServiceBill.getPrinted()){
    		miscellaneousServiceBill.setPrinted(true);
    		billingService.saveMiscellaneousServiceBill(miscellaneousServiceBill);
    	}
    	return "redirect:/module/ehrbilling/miscellaneousServiceBill.list";
	}
	@RequestMapping(method=RequestMethod.GET)
	public String listBill(@RequestParam(value="pageSize",required=false)  Integer pageSize, 
	                       @RequestParam(value="currentPage",required=false)  Integer currentPage,
	                       @RequestParam(value="billId",required=false) Integer miscellaneousServiceBillId,
	                       @RequestParam(value="serviceId", required=false) Integer serviceId,
	                         Model model, HttpServletRequest request){
		BillingService billingService = (BillingService)Context.getService(BillingService.class);
		MiscellaneousService service = billingService.getMiscellaneousServiceById(serviceId);
		int total = billingService.countListMiscellaneousServiceBill(service);
		PagingUtil pagingUtil = new PagingUtil(RequestUtil.getCurrentLink(request), pageSize, currentPage, total);
		model.addAttribute("listBills", billingService.listMiscellaneousServiceBill(pagingUtil.getStartPos(), pagingUtil.getPageSize(), service) );
		model.addAttribute("pagingUtil", pagingUtil);
		if( miscellaneousServiceBillId != null ){
			MiscellaneousServiceBill miscellaneousServiceBill = billingService.getMiscellaneousServiceBillById(miscellaneousServiceBillId);
			model.addAttribute("bill", miscellaneousServiceBill);
		}
		User user = Context.getAuthenticatedUser();
		model.addAttribute("canEdit", user.hasPrivilege(BillingConstants.PRIV_EDIT_BILL_ONCE_PRINTED) );
		
		model.addAttribute("listServices", billingService.getAllMiscellaneousService());
		model.addAttribute("serviceId", serviceId);
		return "/module/ehrbilling/main/miscellaneousServiceBillList";
	}
}
