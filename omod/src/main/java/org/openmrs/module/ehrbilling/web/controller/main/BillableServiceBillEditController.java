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

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.Integer;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.ehrbilling.includable.billcalculator.BillCalculatorService;
import org.openmrs.module.hospitalcore.BillingService;
import org.openmrs.module.hospitalcore.model.BillableService;
import org.openmrs.module.hospitalcore.model.PatientServiceBill;
import org.openmrs.module.hospitalcore.model.PatientServiceBillItem;
import org.openmrs.module.hospitalcore.util.HospitalCoreUtils;
import org.openmrs.module.hospitalcore.util.Money;
import org.openmrs.module.hospitalcore.util.PatientUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 */
@Controller
@RequestMapping("/module/ehrbilling/editPatientServiceBill.form")
public class BillableServiceBillEditController {
	
	private Log logger = LogFactory.getLog(getClass());
	
	private java.lang.Object BigDecimal;
	
	@RequestMapping(method = RequestMethod.GET)
	public String viewForm(Model model, @RequestParam("billId") Integer billId, @RequestParam("patientId") Integer patientId) {
		
		BillingService billingService = Context.getService(BillingService.class);
		List<BillableService> services = billingService.getAllServices();
		Map<Integer, BillableService> mapServices = new HashMap<Integer, BillableService>();
		for (BillableService ser : services) {
			mapServices.put(ser.getConceptId(), ser);
		}
		Integer conceptId = Integer.valueOf(Context.getAdministrationService().getGlobalProperty(
		    "billing.rootServiceConceptId"));
		Concept concept = Context.getConceptService().getConcept(conceptId);
		model.addAttribute("tabs", billingService.traversTab(concept, mapServices, 1));
		model.addAttribute("patientId", patientId);
		PatientServiceBill bill = billingService.getPatientServiceBillById(billId);
		
		model.addAttribute("bill", bill);
		return "/module/ehrbilling/main/billableServiceBillEdit";
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String onSubmit(Model model, Object command, BindingResult bindingResult, HttpServletRequest request,
	        @RequestParam("cons") Integer[] cons, @RequestParam("patientId") Integer patientId,
	        @RequestParam("billId") Integer billId, @RequestParam("action") String action,
	        @RequestParam(value = "description", required = false) String description) {
		
		validate(cons, bindingResult, request);
		if (bindingResult.hasErrors()) {
			model.addAttribute("errors", bindingResult.getAllErrors());
			return "module/ehrbilling/main/patientServiceBillEdit";
		}
		BillingService billingService = Context.getService(BillingService.class);
		
		PatientServiceBill bill = billingService.getPatientServiceBillById(billId);
		
		// Get the BillCalculator to calculate the rate of bill item the patient
		// has to pay
		Patient patient = Context.getPatientService().getPatient(patientId);
		Map<String, String> attributes = PatientUtils.getAttributes(patient);
		BillCalculatorService calculator = new BillCalculatorService();
		
		if (!"".equals(description))
			bill.setDescription(description);
		
		if ("void".equalsIgnoreCase(action)) {
			bill.setVoided(true);
			bill.setVoidedDate(new Date());
			for (PatientServiceBillItem item : bill.getBillItems()) {
				item.setVoided(true);
				item.setVoidedDate(new Date());
				/* these 5 lines of code written only due to voided item is being updated in "billing_patient_service_bill_item" table
				  but not being updated in "orders" table */
				Order ord = item.getOrder();
				if (ord != null) {
					ord.setVoided(true);
					ord.setDateVoided(new Date());
				}
				item.setOrder(ord);
			}
			billingService.savePatientServiceBill(bill);
			
			return "redirect:/module/ehrbilling/patientServiceBillEdit.list?patientId=" + patientId;
		}
		
		// void old items and reset amount
		Map<Integer, PatientServiceBillItem> mapOldItems = new HashMap<Integer, PatientServiceBillItem>();
		for (PatientServiceBillItem item : bill.getBillItems()) {
			item.setVoided(true);
			item.setVoidedDate(new Date());
			// Bug #323 [BILLING] When a bill with a lab\radiology order is edited the order is re-sent
			Order ord = item.getOrder();
			/*[Billing - Bug error in edit bill.
			  the problem was while we are editing the bill of other than lab and radiology.
			*/
			if (ord != null) {
				ord.setVoided(true);
				ord.setDateVoided(new Date());
			}
			item.setOrder(ord);
			mapOldItems.put(item.getPatientServiceBillItemId(), item);
		}
		bill.setAmount(BigDecimal.ZERO);
		bill.setPrinted(false);
		
		PatientServiceBillItem item;
		int quantity = 0;
		Money itemAmount;
		Money mUnitPrice;
		Money totalAmount = new Money(BigDecimal.ZERO);
		BigDecimal totalActualAmount = new BigDecimal(0);
		BigDecimal unitPrice;
		String name;
		BillableService service;
		
		for (int conceptId : cons) {
			
			unitPrice = NumberUtils.createBigDecimal(request.getParameter(conceptId + "_unitPrice"));
			quantity = NumberUtils.createInteger(request.getParameter(conceptId + "_qty"));
			name = request.getParameter(conceptId + "_name");
			service = billingService.getServiceByConceptId(conceptId);
			
			mUnitPrice = new Money(unitPrice);
			itemAmount = mUnitPrice.times(quantity);
			totalAmount = totalAmount.plus(itemAmount);
			
			String sItemId = request.getParameter(conceptId + "_itemId");
			
			if (sItemId == null) {
				item = new PatientServiceBillItem();
				
				// Get the ratio for each bill item
				Map<String, Object> parameters = HospitalCoreUtils.buildParameters("patient", patient, "attributes",
				    attributes, "billItem", item);
				BigDecimal rate = calculator.getRate(parameters);
				
				item.setAmount(itemAmount.getAmount());
				item.setActualAmount(item.getAmount().multiply(rate));
				totalActualAmount = totalActualAmount.add(item.getActualAmount());
				item.setCreatedDate(new Date());
				item.setName(name);
				item.setPatientServiceBill(bill);
				item.setQuantity(quantity);
				item.setService(service);
				item.setUnitPrice(unitPrice);
				bill.addBillItem(item);
			} else {
				
				item = mapOldItems.get(Integer.parseInt(sItemId));
				
				// Get the ratio for each bill item
				Map<String, Object> parameters = HospitalCoreUtils.buildParameters("patient", patient, "attributes",
				    attributes, "billItem", item);
				BigDecimal rate = calculator.getRate(parameters);
				
				// Edited Quantity and Amount information is lost in database
				if (quantity != item.getQuantity()) {
					item.setVoided(true);
					item.setVoidedDate(new Date());
				} else {
					item.setVoided(false);
					item.setVoidedDate(null);
				}
				// Bug When a bill with a lab\radiology order is edited the order is re-sent
				Order ord = item.getOrder();
				if (ord != null) {
					ord.setVoided(false);
					ord.setDateVoided(null);
				}
				item.setOrder(ord);
				// Edited Quantity and Amount information is lost in database
				if (quantity != item.getQuantity()) {
					item = new PatientServiceBillItem();
					item.setService(service);
					item.setUnitPrice(unitPrice);
					item.setQuantity(quantity);
					item.setName(name);
					item.setCreatedDate(new Date());
					item.setOrder(ord);
					bill.addBillItem(item);
				}
				item.setAmount(itemAmount.getAmount());
				item.setActualAmount(item.getAmount().multiply(rate));
				totalActualAmount = totalActualAmount.add(item.getActualAmount());
			}
		}
		bill.setAmount(totalAmount.getAmount());
		bill.setActualAmount(totalActualAmount);
		
		// Determine whether the bill is free or not
		
		bill.setFreeBill(calculator.isFreeBill(HospitalCoreUtils.buildParameters("attributes", attributes)));
		logger.info("Is free bill: " + bill.getFreeBill());
		
		bill = billingService.savePatientServiceBill(bill);
		//No Queue to be generated from Old bill
		return "redirect:/module/billing/patientServiceBillEdit.list?patientId=" + patientId + "&billId=" + billId;
	}
	
	private void validate(Integer[] ids, BindingResult binding, HttpServletRequest request) {
		for (Integer id : ids) {
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
