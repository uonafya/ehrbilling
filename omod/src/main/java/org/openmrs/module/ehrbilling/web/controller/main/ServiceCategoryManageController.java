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
import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.hospitalcore.BillingService;
import org.openmrs.module.hospitalcore.concept.ConceptNode;
import org.openmrs.module.hospitalcore.concept.TestTree;
import org.openmrs.module.hospitalcore.model.BillableService;
import org.openmrs.module.hospitalcore.util.GlobalPropertyUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 *
 */
@Controller
@RequestMapping("/module/ehrbilling/serviceCategoryManage.form")
public class ServiceCategoryManageController {
	Log log = LogFactory.getLog(getClass());

	@RequestMapping(method = RequestMethod.GET)
	public String viewForm(Model model) {

		Integer rootServiceConceptId = GlobalPropertyUtil.getInteger(
				"billing.rootServiceConceptId", 31);
		Concept rootServiceconcept = Context.getConceptService().getConcept(
				rootServiceConceptId);
		if (rootServiceconcept != null) {
			TestTree tree = new TestTree(rootServiceconcept);
			BillingService billingService = (BillingService) Context.getService(BillingService.class);
			List<BillableService> bss = billingService.getAllServices();			
			for(BillableService bs:bss){		
				Concept serviceConcept = Context.getConceptService().getConcept(bs.getConceptId());				
				if(serviceConcept!=null){
					
					ConceptNode node = tree.findNode(serviceConcept);					
					if(node!=null){						
						while(!node.getParent().equals(tree.getRootLab())) {
							node = node.getParent();
						}
						bs.setCategory(node.getConcept());
						billingService.saveService(bs);
					} else {
						bs.setCategory(null);
						billingService.saveService(bs);
					}					
				}
			}
		}

		return "redirect:/module/ehrbilling/main.form";
	}
}
