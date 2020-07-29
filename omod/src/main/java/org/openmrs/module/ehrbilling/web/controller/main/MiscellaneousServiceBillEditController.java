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
import java.util.List;

@Controller
@RequestMapping("/module/ehrbilling/editMiscellaneousServiceBill.form")
public class MiscellaneousServiceBillEditController {
	Log log = LogFactory.getLog(getClass());
	
	@RequestMapping(method=RequestMethod.POST)
	public String onSubmit(Model model,
			@RequestParam(value = "serviceId") Integer miscellaneousServiceId, 
			@RequestParam("billId") Integer billId,
			@RequestParam("name") String name,
			@RequestParam("action") String action,
			Object command, BindingResult binding,HttpServletRequest request ){

		
		BillingService billingService = (BillingService) Context.getService(BillingService.class);
		MiscellaneousServiceBill miscellaneousServiceBill = billingService.getMiscellaneousServiceBillById(billId);
		
		// if action is void
		if( "void".equalsIgnoreCase(action)){
			miscellaneousServiceBill.setVoided(true);
			billingService.saveMiscellaneousServiceBill(miscellaneousServiceBill);
			return "redirect:/module/ehrbilling/miscellaneousServiceBill.list";
		}
		

		MiscellaneousService miscellaneousService = null;
		int quantity = 0;
		Money itemAmount;
		Money totalAmount = new Money(BigDecimal.ZERO);
		
		miscellaneousService = billingService.getMiscellaneousServiceById(miscellaneousServiceId);
		quantity =Integer.parseInt(request.getParameter(miscellaneousServiceId+"_qty"));
		itemAmount = new Money(new BigDecimal(request.getParameter(miscellaneousServiceId+"_amount")));
		
		itemAmount = itemAmount.times(quantity);
		totalAmount = totalAmount.plus(itemAmount);	
		miscellaneousServiceBill.setLiableName(name);
		
		 miscellaneousService = billingService.getMiscellaneousServiceById(miscellaneousServiceId);
		miscellaneousServiceBill.setAmount(totalAmount.getAmount());
		miscellaneousServiceBill.setService(miscellaneousService);
		miscellaneousServiceBill.setQuantity(quantity);
		miscellaneousServiceBill = billingService.saveMiscellaneousServiceBill(miscellaneousServiceBill);

		
		return "redirect:/module/ehrbilling/miscellaneousServiceBill.list?serviceId="+miscellaneousServiceId+"&billId="+miscellaneousServiceBill.getId();
	}
	
	@RequestMapping(method=RequestMethod.GET)
	public String displayForm(@ModelAttribute("command") Object command, @RequestParam("billId") Integer billId,  HttpServletRequest request, Model model){

		BillingService billingService = (BillingService) Context.getService(BillingService.class);
		
		List<MiscellaneousService> listMiscellaneousService = billingService.getAllMiscellaneousService();
		
		if( listMiscellaneousService == null || listMiscellaneousService.size() == 0  )
		{
			request.getSession().setAttribute(WebConstants.OPENMRS_MSG_ATTR, "No MiscellaneousService found.");
		}else {
			model.addAttribute("listMiscellaneousService", listMiscellaneousService);
		}
		
		MiscellaneousServiceBill bill = billingService.getMiscellaneousServiceBillById(billId);		
		model.addAttribute("bill", bill);
		
		return "module/ehrbilling/main/miscellaneousServiceBillEdit";
	}

	
}
