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
import org.openmrs.module.hospitalcore.model.Company;
import org.openmrs.module.hospitalcore.model.TenderBill;
import org.openmrs.module.hospitalcore.util.PagingUtil;
import org.openmrs.module.hospitalcore.util.RequestUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 *
 */
@Controller
@RequestMapping("/module/ehrbilling/tenderBill.list")
public class TenderBillListController {
	
	@RequestMapping(method = RequestMethod.POST)
	public String printBill(@RequestParam("tenderBillId") Integer tenderBillId, @RequestParam("companyId") Integer companyId) {
		BillingService billingService = (BillingService) Context.getService(BillingService.class);
		TenderBill tenderBill = billingService.getTenderBillById(tenderBillId);
		if (tenderBill != null && !tenderBill.getPrinted()) {
			tenderBill.setPrinted(true);
			billingService.saveTenderBill(tenderBill);
		}
		return "redirect:/module/ehrbilling/tenderBill.list?companyId=" + companyId;
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public String listBill(@RequestParam("companyId") Integer companyId,
	        @RequestParam(value = "pageSize", required = false) Integer pageSize,
	        @RequestParam(value = "currentPage", required = false) Integer currentPage,
	        @RequestParam(value = "tenderBillId", required = false) Integer tenderBillId, Model model,
	        HttpServletRequest request) {
		BillingService billingService = (BillingService) Context.getService(BillingService.class);
		Company company = billingService.getCompanyById(companyId);
		if (company != null) {
			int total = billingService.countListTenderBillByCompany(company);
			PagingUtil pagingUtil = new PagingUtil(RequestUtil.getCurrentLink(request), pageSize, currentPage, total);
			model.addAttribute("tenderBills",
			    billingService.listTenderBillByCompany(pagingUtil.getStartPos(), pagingUtil.getPageSize(), company));
			model.addAttribute("pagingUtil", pagingUtil);
			model.addAttribute("company", company);
		}
		if (tenderBillId != null) {
			TenderBill tenderBill = billingService.getTenderBillById(tenderBillId);
			model.addAttribute("tenderBill", tenderBill);
		}
		model.addAttribute("companyId", companyId);
		User user = Context.getAuthenticatedUser();
		
		model.addAttribute("canEdit", user.hasPrivilege(BillingConstants.PRIV_EDIT_BILL_ONCE_PRINTED));
		return "/module/ehrbilling/main/tenderBillList";
	}
}
