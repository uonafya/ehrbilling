/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.ehrbilling.includable.billcalculator.bd_teaching;

import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.ehrbilling.includable.billcalculator.BillCalculator;
import org.openmrs.module.hospitalcore.concept.TestTree;
import org.openmrs.module.hospitalcore.model.PatientServiceBillItem;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BillCalculatorImpl implements BillCalculator {
	
	private static Map<String, Set<Concept>> testTreeMap;
	
	/**
	 * Get the percentage of price to pay If patient category is RSBY or BPL, the bill should be
	 * 100% free
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public BigDecimal getRate(Map<String, Object> parameters) {
		BigDecimal rate = new BigDecimal(0);
		Map<String, String> attributes = (Map<String, String>) parameters.get("attributes");
		String patientCategory = attributes.get("Patient Category");
		PatientServiceBillItem item = (PatientServiceBillItem) parameters.get("billItem");
		if (patientCategory != null) {
			
			if (patientCategory.contains("General")) {
				rate = new BigDecimal(1);
			}
		} else {
			rate = new BigDecimal(1);
		}
		
		return rate;
	}
	
	/**
	 * Build test tree map for senior citizen billing
	 */
	private static void buildTestTreeMap() {
		testTreeMap = new HashMap<String, Set<Concept>>();
		
		// General lab
		buildTestTree("GENERAL LABORATORY");
		buildTestTree("RADIOLOGY");
		buildTestTree("ULTRASOUND");
		buildTestTree("CARDIOLOGY");
	}
	
	/**
	 * Build test tree for a specific tests
	 * 
	 * @param conceptName
	 */
	private static void buildTestTree(String conceptName) {
		Concept generalLab = Context.getConceptService().getConcept(conceptName);
		TestTree tree = new TestTree(generalLab);
		if (tree.getRootNode() != null) {
			testTreeMap.put(conceptName, tree.getConceptSet());
		}
	}
	
	/**
	 * Determine whether a bill should be free or not. By default, all bills are not free
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean isFreeBill(Map<String, Object> parameters) {
		Map<String, String> attributes = (Map<String, String>) parameters.get("attributes");
		String patientCategory = attributes.get("Patient Category");
		if (patientCategory != null) {
			
			if (patientCategory.contains("General")) {
				return false;
			}
		} else {
			return false;
		}
		return true;
	}
}
