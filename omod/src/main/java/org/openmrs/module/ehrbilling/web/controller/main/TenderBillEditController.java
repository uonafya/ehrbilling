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
import org.openmrs.module.hospitalcore.model.Tender;
import org.openmrs.module.hospitalcore.model.TenderBill;
import org.openmrs.module.hospitalcore.model.TenderBillItem;
import org.openmrs.module.hospitalcore.util.Money;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/module/ehrbilling/editTenderBill.form")
public class TenderBillEditController {
	
	Log log = LogFactory.getLog(getClass());
	
	@RequestMapping(method = RequestMethod.GET)
	public String displayForm(@RequestParam("companyId") Integer companyId,
	        @RequestParam("tenderBillId") Integer tenderBillId, HttpServletRequest request, Model model) {
		
		BillingService billingService = (BillingService) Context.getService(BillingService.class);
		
		List<Tender> listTender = billingService.getActiveTenders();
		
		if (listTender == null || listTender.size() == 0) {
			request.getSession().setAttribute(WebConstants.OPENMRS_MSG_ATTR, "No Tender Service found.");
		} else {
			model.addAttribute("listTender", listTender);
		}
		model.addAttribute("companyId", companyId);
		
		TenderBill tenderBill = billingService.getTenderBillById(tenderBillId);
		
		model.addAttribute("tenderBill", tenderBill);
		return "module/ehrbilling/main/tenderBillEdit";
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String onSubmit(Model model, @RequestParam("tenderBillId") Integer tenderBillId,
	        @RequestParam("companyId") Integer companyId, @RequestParam("tenderIds") Integer[] tenderIds,
	        @RequestParam("action") String action, HttpServletRequest request, Object command, BindingResult binding) {
		
		validateQty(tenderIds, binding, request);
		if (binding.hasErrors()) {
			model.addAttribute("errors", binding.getAllErrors());
			return "module/ehrbilling/main/tenderBillAdd";
		}
		
		BillingService billingService = (BillingService) Context.getService(BillingService.class);
		
		TenderBill tenderBill = billingService.getTenderBillById(tenderBillId);
		if ("void".equalsIgnoreCase(action)) {
			tenderBill.setVoided(true);
			tenderBill.setVoidedDate(new Date());
			for (TenderBillItem item : tenderBill.getBillItems()) {
				item.setVoided(true);
				item.setVoidedDate(new Date());
			}
			billingService.saveTenderBill(tenderBill);
			return "redirect:/module/ehrbilling/tenderBill.list?companyId=" + companyId;
		}
		
		tenderBill.setPrinted(false);
		
		// void old items and reset amount
		Map<Integer, TenderBillItem> mapOldItems = new HashMap<Integer, TenderBillItem>();
		for (TenderBillItem item : tenderBill.getBillItems()) {
			item.setVoided(true);
			item.setVoidedDate(new Date());
			mapOldItems.put(item.getTenderBillItemId(), item);
		}
		tenderBill.setAmount(BigDecimal.ZERO);
		
		Tender tender = null;
		int quantity = 0;
		Money itemAmount;
		Money totalAmount = new Money(BigDecimal.ZERO);
		TenderBillItem item;
		for (Integer id : tenderIds) {
			
			tender = billingService.getTenderById(id);
			quantity = Integer.parseInt(request.getParameter(id + "_qty"));
			itemAmount = new Money(tender.getPrice());
			itemAmount = itemAmount.times(quantity);
			totalAmount = totalAmount.plus(itemAmount);
			
			String sItemId = request.getParameter(id + "_itemId");
			
			if (sItemId != null) {
				item = mapOldItems.get(Integer.parseInt(sItemId));
				item.setVoided(false);
				item.setVoidedDate(null);
				item.setQuantity(quantity);
				item.setAmount(itemAmount.getAmount());
			} else {
				item = new TenderBillItem();
				item.setName(tender.getName() + "_" + tender.getNumber());
				item.setCreatedDate(new Date());
				item.setTender(tender);
				item.setUnitPrice(tender.getPrice());
				item.setQuantity(quantity);
				item.setTenderBill(tenderBill);
				item.setAmount(itemAmount.getAmount());
				tenderBill.addBillItem(item);
			}
		}
		tenderBill.setAmount(totalAmount.getAmount());
		tenderBill = billingService.saveTenderBill(tenderBill);
		
		return "redirect:/module/ehrbilling/tenderBill.list?companyId=" + companyId + "&tenderBillId="
		        + tenderBill.getTenderBillId();
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
