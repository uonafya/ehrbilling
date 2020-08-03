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
import org.openmrs.module.hospitalcore.model.AmbulanceBill;
import org.openmrs.module.hospitalcore.model.Driver;
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
@RequestMapping("/module/ehrbilling/ambulanceBill.list")
public class AmbulanceBillListController {
	
	@RequestMapping(method = RequestMethod.POST)
	public String printBill(@RequestParam("ambulanceBillId") Integer ambulanceBillId,
	        @RequestParam("driverId") Integer driverId) {
		BillingService billingService = (BillingService) Context.getService(BillingService.class);
		AmbulanceBill ambulanceBill = billingService.getAmbulanceBillById(ambulanceBillId);
		if (ambulanceBill != null && !ambulanceBill.getPrinted()) {
			ambulanceBill.setPrinted(true);
			billingService.saveAmbulanceBill(ambulanceBill);
		}
		return "redirect:/module/ehrbilling/ambulanceBill.list?driverId=" + driverId;
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public String listBill(@RequestParam("driverId") Integer driverId,
	        @RequestParam(value = "pageSize", required = false) Integer pageSize,
	        @RequestParam(value = "currentPage", required = false) Integer currentPage,
	        @RequestParam(value = "ambulanceBillId", required = false) Integer ambulanceBillId, Model model,
	        HttpServletRequest request) {
		BillingService billingService = (BillingService) Context.getService(BillingService.class);
		Driver driver = billingService.getDriverById(driverId);
		if (driver != null) {
			int total = billingService.countListAmbulanceBillByDriver(driver);
			PagingUtil pagingUtil = new PagingUtil(RequestUtil.getCurrentLink(request), pageSize, currentPage, total);
			model.addAttribute("ambulanceBills",
			    billingService.listAmbulanceBillByDriver(pagingUtil.getStartPos(), pagingUtil.getPageSize(), driver));
			model.addAttribute("pagingUtil", pagingUtil);
			model.addAttribute("driver", driver);
		}
		if (ambulanceBillId != null) {
			AmbulanceBill ambulanceBill = billingService.getAmbulanceBillById(ambulanceBillId);
			model.addAttribute("ambulanceBill", ambulanceBill);
		}
		model.addAttribute("driverId", driverId);
		User user = Context.getAuthenticatedUser();
		
		model.addAttribute("canEdit", user.hasPrivilege(BillingConstants.PRIV_EDIT_BILL_ONCE_PRINTED));
		return "/module/ehrbilling/main/ambulanceBillList";
	}
}
