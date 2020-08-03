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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.hospitalcore.BillingService;
import org.openmrs.module.hospitalcore.model.MiscellaneousService;
import org.openmrs.module.hospitalcore.model.MiscellaneousServiceBill;
import org.openmrs.module.hospitalcore.util.Money;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/module/ehrbilling/addMiscellaneousServiceBill.form")
public class MiscellaneousServiceBillAddController {
	
	Log log = LogFactory.getLog(getClass());
	
	@RequestMapping(method = RequestMethod.POST)
	public String onSubmit(Model model, @RequestParam("serviceId") Integer miscellaneousServiceId,
	        @RequestParam("name") String name, HttpServletRequest request, Object command, BindingResult binding) {
		
		BillingService billingService = (BillingService) Context.getService(BillingService.class);
		
		MiscellaneousService miscellaneousService = null;
		int quantity = 0;
		Money itemAmount;
		Money totalAmount = new Money(BigDecimal.ZERO);
		
		miscellaneousService = billingService.getMiscellaneousServiceById(miscellaneousServiceId);
		quantity = Integer.parseInt(request.getParameter(miscellaneousServiceId + "_qty"));
		
		itemAmount = new Money(new BigDecimal(request.getParameter(miscellaneousServiceId + "_price")));
		itemAmount = itemAmount.times(quantity);
		totalAmount = totalAmount.plus(itemAmount);
		
		MiscellaneousServiceBill miscellaneousServiceBill = new MiscellaneousServiceBill();
		miscellaneousServiceBill.setCreatedDate(new Date());
		miscellaneousServiceBill.setCreator(Context.getAuthenticatedUser().getUserId());
		miscellaneousServiceBill.setLiableName(name);
		
		miscellaneousServiceBill.setAmount(totalAmount.getAmount());
		miscellaneousServiceBill.setService(miscellaneousService);
		miscellaneousServiceBill.setQuantity(quantity);
		miscellaneousServiceBill.setReceipt(billingService.createReceipt());
		miscellaneousServiceBill = billingService.saveMiscellaneousServiceBill(miscellaneousServiceBill);
		
		return "redirect:/module/ehrbilling/miscellaneousServiceBill.list?serviceId=" + miscellaneousServiceId + "&billId="
		        + miscellaneousServiceBill.getId();
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public String displayForm(@ModelAttribute("command") Object command, HttpServletRequest request, Model model) {
		
		BillingService billingService = (BillingService) Context.getService(BillingService.class);
		
		List<MiscellaneousService> listMiscellaneousService = billingService.getAllMiscellaneousService();
		
		if (listMiscellaneousService == null || listMiscellaneousService.size() == 0) {
			request.getSession().setAttribute(WebConstants.OPENMRS_MSG_ATTR, "No MiscellaneousService found.");
		} else {
			model.addAttribute("listMiscellaneousService", listMiscellaneousService);
		}
		return "module/ehrbilling/main/miscellaneousServiceBillAdd";
	}
	
}
