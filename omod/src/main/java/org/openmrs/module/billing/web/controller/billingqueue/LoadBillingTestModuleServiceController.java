package org.openmrs.module.billing.web.controller.billingqueue;

import org.openmrs.Concept;
import org.openmrs.Order;
import org.openmrs.api.context.Context;
import org.openmrs.module.hospitalcore.RadiologyService;
import org.openmrs.module.hospitalcore.model.RadiologyDepartment;
import org.openmrs.module.hospitalcore.util.RadiologyConstants;
import org.openmrs.module.hospitalcore.util.RadiologyUtil;
import org.openmrs.module.hospitalcore.util.TestModel;
import org.openmrs.module.radiologyapp.util.RadiologyAppUtil;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LoadBillingTestModuleServiceController {

    //load all  billing test modules
    public List<SimpleObject> getAllTestModules() {
        RadiologyService radiologyService = Context.getService(RadiologyService.class);
        Concept investigation = Context.getConceptService().getConcept(investigationId);
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
        Date orderDate = null;
        List<SimpleObject> testOrdersInQueue = new ArrayList<SimpleObject>();
        try {
            orderDate = dateFormatter.parse(orderDateString);
            Map<Concept, Set<Concept>> allowedInvestigations = RadiologyAppUtil.getAllowedInvestigations();
            Set<Concept> allowableTests = new HashSet<Concept>();
            if (investigation != null) {
                allowableTests = allowedInvestigations.get(investigation);
            } else {
                for (Concept c : allowedInvestigations.keySet()) {
                    allowableTests.addAll(allowedInvestigations.get(c));
                }
            }

            if (currentPage == null)
                currentPage = 1;

            List<Order> orders = radiologyService.getOrders(orderDate, phrase, allowableTests,
                    currentPage);
            List<TestModel> allTestOrders = RadiologyUtil.generateModelsFromOrders(
                    orders, allowedInvestigations);
            List<TestModel> tests = new ArrayList<TestModel>();

            for (TestModel testModel : allTestOrders) {
                //load all radiology testmodules
                tests.add(testModel);
            }
            testOrdersInQueue = SimpleObject.fromCollection(tests, ui, "startDate", "patientIdentifier", "patientName", "gender", "age", "testName", "orderId","status");

        } catch (ParseException e) {
            e.printStackTrace();
            logger.error("Error when parsing order date!"+e.getMessage());
        }
        return testOrdersInQueue;

    }
}
