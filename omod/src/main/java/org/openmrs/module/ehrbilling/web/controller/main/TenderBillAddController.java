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
import org.openmrs.module.hospitalcore.model.Company;
import org.openmrs.module.hospitalcore.model.Tender;
import org.openmrs.module.hospitalcore.model.TenderBill;
import org.openmrs.module.hospitalcore.model.TenderBillItem;
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
@RequestMapping("/module/ehrbilling/addTenderBill.form")
public class TenderBillAddController {
	
	Log log = LogFactory.getLog(getClass());
	
	@RequestMapping(method = RequestMethod.POST)
	public String onSubmit(Model model, @RequestParam("companyId") Integer companyId,
	        @RequestParam("tenderIds") Integer[] tenderIds, HttpServletRequest request, Object command, BindingResult binding) {
		
		validateQty(tenderIds, binding, request);
		if (binding.hasErrors()) {
			model.addAttribute("errors", binding.getAllErrors());
			return "module/ehrbilling/main/tenderBillAdd";
		}
		
		BillingService billingService = (BillingService) Context.getService(BillingService.class);
		
		Company company = billingService.getCompanyById(companyId);
		
		TenderBill tenderBill = new TenderBill();
		tenderBill.setCompany(company);
		tenderBill.setCreatedDate(new Date());
		tenderBill.setCreator(Context.getAuthenticatedUser());
		
		Tender tender = null;
		int quantity = 0;
		Money itemAmount;
		Money totalAmount = new Money(BigDecimal.ZERO);
		for (Integer id : tenderIds) {
			
			tender = billingService.getTenderById(id);
			quantity = Integer.parseInt(request.getParameter(id + "_qty"));
			itemAmount = new Money(tender.getPrice());
			itemAmount = itemAmount.times(quantity);
			totalAmount = totalAmount.plus(itemAmount);
			
			TenderBillItem item = new TenderBillItem();
			item.setName(tender.getName() + "_" + tender.getNumber());
			item.setCreatedDate(new Date());
			item.setTender(tender);
			item.setUnitPrice(tender.getPrice());
			item.setQuantity(quantity);
			item.setTenderBill(tenderBill);
			item.setAmount(itemAmount.getAmount());
			tenderBill.addBillItem(item);
		}
		tenderBill.setAmount(totalAmount.getAmount());
		tenderBill.setReceipt(billingService.createReceipt());
		tenderBill = billingService.saveTenderBill(tenderBill);
		
		return "redirect:/module/ehrbilling/tenderBill.list?companyId=" + companyId + "&tenderBillId="
		        + tenderBill.getTenderBillId();
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public String displayForm(@ModelAttribute("command") Object command, @RequestParam("companyId") Integer companyId,
	        HttpServletRequest request, Model model) {
		
		BillingService billingService = (BillingService) Context.getService(BillingService.class);
		
		List<Tender> listTender = billingService.getActiveTenders();
		
		if (listTender == null || listTender.size() == 0) {
			request.getSession().setAttribute(WebConstants.OPENMRS_MSG_ATTR, "No Tender Service found.");
		} else {
			model.addAttribute("listTender", listTender);
		}
		model.addAttribute("companyId", companyId);
		return "module/ehrbilling/main/tenderBillAdd";
	}
	
	private void validateQty(Integer[] ids, BindingResult binding, HttpServletRequest request) {
		for (int id : ids) {
			try {
				Integer.parseInt(request.getParameter(id + "_qty"));
			}
			catch (Exception e) {
				binding.reject("billing.bill.quantity.invalid", "Quantity is invalid");
				return;
			}
			
		}
	}
	
}
