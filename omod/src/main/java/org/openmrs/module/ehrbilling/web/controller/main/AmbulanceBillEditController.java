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
import org.openmrs.module.hospitalcore.model.Ambulance;
import org.openmrs.module.hospitalcore.model.AmbulanceBill;
import org.openmrs.module.hospitalcore.model.AmbulanceBillItem;
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
@RequestMapping("/module/ehrbilling/editAmbulanceBill.form")
public class AmbulanceBillEditController {
	
	Log log = LogFactory.getLog(getClass());
	
	@RequestMapping(method = RequestMethod.GET)
	public String displayForm(@RequestParam("driverId") Integer driverId,
	        @RequestParam("ambulanceBillId") Integer ambulanceBillId, HttpServletRequest request, Model model) {
		
		BillingService billingService = (BillingService) Context.getService(BillingService.class);
		
		List<Ambulance> listAmbulance = billingService.getActiveAmbulances();
		
		if (listAmbulance == null || listAmbulance.size() == 0) {
			request.getSession().setAttribute(WebConstants.OPENMRS_MSG_ATTR, "No Ambulance Service found.");
		} else {
			model.addAttribute("listAmbulance", listAmbulance);
		}
		model.addAttribute("driverId", driverId);
		
		AmbulanceBill ambulanceBill = billingService.getAmbulanceBillById(ambulanceBillId);
		
		model.addAttribute("ambulanceBill", ambulanceBill);
		return "module/ehrbilling/main/ambulanceBillEdit";
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String onSubmit(Model model, @RequestParam("ambulanceBillId") Integer ambulanceBillId,
	        @RequestParam("driverId") Integer driverId, @RequestParam("ambulanceIds") Integer[] ambulanceIds,
	        @RequestParam("action") String action, HttpServletRequest request, Object command, BindingResult binding) {
		
		validate(ambulanceIds, binding, request);
		if (binding.hasErrors()) {
			model.addAttribute("errors", binding.getAllErrors());
			return "module/ehrbilling/main/ambulanceBillAdd";
		}
		
		BillingService billingService = (BillingService) Context.getService(BillingService.class);
		
		AmbulanceBill ambulanceBill = billingService.getAmbulanceBillById(ambulanceBillId);
		
		if ("void".equalsIgnoreCase(action)) {
			ambulanceBill.setVoided(true);
			ambulanceBill.setVoidedDate(new Date());
			for (AmbulanceBillItem item : ambulanceBill.getBillItems()) {
				item.setVoided(true);
				item.setVoidedDate(new Date());
			}
			billingService.saveAmbulanceBill(ambulanceBill);
			return "redirect:/module/ehrbilling/ambulanceBill.list?driverId=" + driverId;
		}
		
		ambulanceBill.setPrinted(false);
		
		// void old items and reset amount
		Map<Integer, AmbulanceBillItem> mapOldItems = new HashMap<Integer, AmbulanceBillItem>();
		for (AmbulanceBillItem item : ambulanceBill.getBillItems()) {
			item.setVoided(true);
			item.setVoidedDate(new Date());
			mapOldItems.put(item.getAmbulanceBillItemId(), item);
		}
		ambulanceBill.setAmount(BigDecimal.ZERO);
		
		Ambulance ambulance = null;
		Money itemAmount;
		Money totalAmount = new Money(BigDecimal.ZERO);
		AmbulanceBillItem item;
		for (Integer id : ambulanceIds) {
			
			ambulance = billingService.getAmbulanceById(id);
			BigDecimal amount = new BigDecimal(request.getParameter(id + "_amount"));
			itemAmount = new Money(amount);
			totalAmount = totalAmount.plus(itemAmount);
			
			Integer numberOfTrip = Integer.parseInt(request.getParameter(id + "_numOfTrip"));
			
			//New Requirement:Edit ambulance bill with all details
			String patientName = (request.getParameter(id + "_patientName"));
			String receiptNumber = (request.getParameter(id + "_receiptNumber"));
			String origin = (request.getParameter(id + "_origin"));
			String destination = (request.getParameter(id + "_destination"));
			
			String sItemId = request.getParameter(id + "_itemId");
			
			if (sItemId != null) {
				item = mapOldItems.get(Integer.parseInt(sItemId));
				item.setVoided(false);
				item.setVoidedDate(null);
				item.setAmount(itemAmount.getAmount());
				item.setNumberOfTrip(numberOfTrip);
				//New Requirement [Billing]Edit ambulance bill with all details
				item.setPatientName(patientName);
				item.setReceiptNumber(receiptNumber);
				item.setOrigin(origin);
				item.setDestination(destination);
			} else {
				item = new AmbulanceBillItem();
				item.setName(ambulance.getName());
				item.setCreatedDate(new Date());
				item.setAmbulance(ambulance);
				item.setAmbulanceBill(ambulanceBill);
				item.setAmount(itemAmount.getAmount());
				item.setNumberOfTrip(numberOfTrip);
				// New Requirement [Billing]Edit ambulance bill with all details
				item.setPatientName(patientName);
				item.setReceiptNumber(receiptNumber);
				item.setOrigin(origin);
				item.setDestination(destination);
				ambulanceBill.addBillItem(item);
			}
		}
		ambulanceBill.setAmount(totalAmount.getAmount());
		ambulanceBill = billingService.saveAmbulanceBill(ambulanceBill);
		
		return "redirect:/module/ehrbilling/ambulanceBill.list?driverId=" + driverId + "&ambulanceBillId="
		        + ambulanceBill.getAmbulanceBillId();
	}
	
	private void validate(Integer[] ids, BindingResult binding, HttpServletRequest request) {
		for (int id : ids) {
			try {
				Integer.parseInt(request.getParameter(id + "_numOfTrip"));
			}
			catch (Exception e) {
				binding.reject("billing.bill.quantity.invalid", "Number of trip is invalid");
				return;
			}
			try {
				new BigDecimal(request.getParameter(id + "_amount"));
			}
			catch (Exception e) {
				binding.reject("billing.bill.quantity.invalid", "Amount is invalid");
				return;
			}
			
		}
	}
	
}
