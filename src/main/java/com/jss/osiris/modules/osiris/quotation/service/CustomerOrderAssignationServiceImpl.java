package com.jss.osiris.modules.osiris.quotation.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.miscellaneous.service.NotificationService;
import com.jss.osiris.modules.osiris.profile.model.Employee;
import com.jss.osiris.modules.osiris.profile.service.EmployeeService;
import com.jss.osiris.modules.osiris.quotation.model.AssignationType;
import com.jss.osiris.modules.osiris.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderAssignation;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.osiris.quotation.model.ICustomerOrderAssignationStatistics;
import com.jss.osiris.modules.osiris.quotation.model.Provision;
import com.jss.osiris.modules.osiris.quotation.model.Service;
import com.jss.osiris.modules.osiris.quotation.repository.CustomerOrderAssignationRepository;

@org.springframework.stereotype.Service
public class CustomerOrderAssignationServiceImpl implements CustomerOrderAssignationService {

    @Autowired
    CustomerOrderAssignationRepository customerOrderAssignationRepository;

    @Autowired
    AssignationTypeService assignationTypeService;

    @Autowired
    ProvisionService provisionService;

    @Autowired
    ConstantService constantService;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    CustomerOrderService customerOrderService;

    @Autowired
    CustomerOrderStatusService customerOrderStatusService;

    @Autowired
    NotificationService notificationService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustomerOrderAssignation addOrUpdateCustomerOrderAssignation(
            CustomerOrderAssignation customerOrderAssignation) {
        return customerOrderAssignationRepository.save(customerOrderAssignation);
    }

    @Override
    public CustomerOrderAssignation getCustomerOrderAssignation(Integer idCustomerOrderAssignation) {
        Optional<CustomerOrderAssignation> customerOrderAssignation = customerOrderAssignationRepository
                .findById(idCustomerOrderAssignation);
        if (customerOrderAssignation.isPresent())
            return customerOrderAssignation.get();
        return null;
    }

    private List<CustomerOrderAssignation> getCustomerOrderAssignationForCustomerOrder(Integer customerOrder) {
        return customerOrderAssignationRepository.findByCustomerOrder_Id(customerOrder);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustomerOrderAssignation addOrUpdateCustomerOrderAssignation(
            CustomerOrderAssignation customerOrderAssignation, Employee employee) {
        customerOrderAssignation = getCustomerOrderAssignation(customerOrderAssignation.getId());
        customerOrderAssignation.setEmployee(employee);
        return addOrUpdateCustomerOrderAssignation(customerOrderAssignation);
    }

    @Override
    public void completeAssignationForCustomerOrder(CustomerOrder customerOrder) throws OsirisException {
        List<AssignationType> assignationTypes = assignationTypeService.getAssignationTypes();
        List<AssignationType> assignationTypesToDefine = new ArrayList<AssignationType>();
        List<AssignationType> existingAssignationTypes = null;

        List<CustomerOrderAssignation> currentCustomerOrderAssignation = getCustomerOrderAssignationForCustomerOrder(
                customerOrder.getId());
        if (currentCustomerOrderAssignation != null)
            existingAssignationTypes = currentCustomerOrderAssignation.stream()
                    .map(CustomerOrderAssignation::getAssignationType).toList();

        if (existingAssignationTypes == null)
            existingAssignationTypes = new ArrayList<AssignationType>();

        Employee assignationEmployeeToAdd = null;
        for (AssignationType assignationType : assignationTypes) {
            if (customerOrder.getAssoAffaireOrders() != null)
                for (AssoAffaireOrder asso : customerOrder.getAssoAffaireOrders())
                    if (asso.getServices() != null)
                        for (Service service : asso.getServices())
                            if (service.getProvisions() != null)
                                for (Provision provision : service.getProvisions()) {
                                    if (provision.getProvisionType() != null && provision.getProvisionType()
                                            .getAssignationType() != null && provision.getProvisionType()
                                                    .getAssignationType().getId().equals(assignationType.getId())
                                            && !existingAssignationTypes.stream().map(AssignationType::getId).toList()
                                                    .contains(assignationType.getId())
                                            && !assignationTypesToDefine.contains(assignationType)) {
                                        assignationTypesToDefine.add(assignationType);

                                        if (provision.getProvisionType().getAssignationType().getId()
                                                .equals(constantService.getAssignationTypeEmployee().getId()))
                                            assignationEmployeeToAdd = provision.getProvisionType()
                                                    .getDefaultEmployee();
                                    }
                                }
        }

        if (customerOrder.getCustomerOrderAssignations() == null)
            customerOrder.setCustomerOrderAssignations(new ArrayList<CustomerOrderAssignation>());

        if (assignationTypesToDefine != null)
            for (AssignationType assignationTypeToAdd : assignationTypesToDefine) {
                boolean found = false;
                for (CustomerOrderAssignation customerOrderAssignation : customerOrder.getCustomerOrderAssignations()) {
                    if (customerOrderAssignation.getAssignationType().getId().equals(assignationTypeToAdd.getId())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    CustomerOrderAssignation customerOrderAssignation = new CustomerOrderAssignation();
                    customerOrderAssignation.setAssignationType(assignationTypeToAdd);
                    customerOrderAssignation.setCustomerOrder(customerOrder);
                    customerOrderAssignation.setIsAssigned(false);

                    if (assignationTypeToAdd.getId().equals(constantService.getAssignationTypeEmployee().getId()))
                        customerOrderAssignation.setEmployee(assignationEmployeeToAdd);

                    addOrUpdateCustomerOrderAssignation(customerOrderAssignation);
                }
            }
    }

    @Override
    public void assignNewProvisionToUser(Provision provision) throws OsirisException {
        if (provision != null && provision.getAssignedTo() == null && provision.getProvisionType() != null
                && provision.getProvisionType().getAssignationType() != null) {
            if (provision.getService().getAssoAffaireOrder().getCustomerOrder() != null && provision.getService()
                    .getAssoAffaireOrder().getCustomerOrder().getCustomerOrderAssignations() != null) {
                for (CustomerOrderAssignation customerOrderAssignation : provision.getService().getAssoAffaireOrder()
                        .getCustomerOrder().getCustomerOrderAssignations()) {
                    if (Boolean.TRUE.equals(customerOrderAssignation.getIsAssigned())
                            && customerOrderAssignation.getEmployee() != null
                            && customerOrderAssignation.getAssignationType().getId()
                                    .equals(provision.getProvisionType().getAssignationType().getId())) {
                        provision.setAssignedTo(customerOrderAssignation.getEmployee());
                        provisionService.addOrUpdateProvision(provision);
                    }
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer getNextOrderForFond(boolean isPriority, Integer complexity, Boolean byPassAssignation)
            throws OsirisException {
        Employee currentEmployee = employeeService.getCurrentEmployee();

        // Forced employee
        AssignationType assignationType = constantService.getAssignationTypeEmployee();
        List<CustomerOrder> customerOrders = customerOrderService.findCustomerOrderByForcedEmployeeAssigned(
                customerOrderStatusService.getCustomerOrderStatusByCode(CustomerOrderStatus.BEING_PROCESSED),
                currentEmployee);

        Integer foundCustomerOrder = findInOrdersAndAssignEmployee(customerOrders, assignationType, isPriority, true,
                null, byPassAssignation);
        if (foundCustomerOrder != null)
            return foundCustomerOrder;

        // Formaliste
        assignationType = constantService.getAssignationTypeFormaliste();
        customerOrders = customerOrderService.findCustomerOrderByFormalisteAssigned(
                employeeService.findEmployeesInTheSameOU(currentEmployee),
                customerOrderStatusService.getCustomerOrderStatusByCode(CustomerOrderStatus.BEING_PROCESSED),
                currentEmployee, assignationType);

        foundCustomerOrder = findInOrdersAndAssignEmployee(customerOrders, assignationType, isPriority, true,
                null, byPassAssignation);
        if (foundCustomerOrder != null)
            return foundCustomerOrder;

        // Announcement
        assignationType = constantService.getAssignationTypePublisciste();
        customerOrders = customerOrderService.findCustomerOrderByPubliscisteAssigned(
                employeeService.findEmployeesInTheSameOU(currentEmployee),
                customerOrderStatusService.getCustomerOrderStatusByCode(CustomerOrderStatus.BEING_PROCESSED),
                currentEmployee, assignationType);

        foundCustomerOrder = findInOrdersAndAssignEmployee(customerOrders, assignationType, isPriority, true,
                null, byPassAssignation);
        if (foundCustomerOrder != null)
            return foundCustomerOrder;

        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer getNextOrderForCommon(boolean isPriority, Integer complexity, Boolean byPassAssignation)
            throws OsirisException {
        List<Employee> productionDirector = Arrays.asList(constantService.getEmployeeProductionDirector());
        Employee currentUser = employeeService.getCurrentEmployee();
        AssignationType assignationType = null;
        List<CustomerOrder> customerOrders = null;
        Integer foundCustomerOrder = null;

        // Forced employee
        assignationType = constantService.getAssignationTypeEmployee();
        customerOrders = customerOrderService.findCustomerOrderByForcedEmployeeAssigned(
                customerOrderStatusService.getCustomerOrderStatusByCode(CustomerOrderStatus.BEING_PROCESSED),
                currentUser);

        foundCustomerOrder = findInOrdersAndAssignEmployee(customerOrders, assignationType, isPriority, true,
                complexity, byPassAssignation);
        if (foundCustomerOrder != null)
            return foundCustomerOrder;

        // Formaliste
        if (currentUser.getAdPath().contains("Formalites")) {
            assignationType = constantService.getAssignationTypeFormaliste();
            customerOrders = customerOrderService.findCustomerOrderByFormalisteAssigned(
                    productionDirector,
                    customerOrderStatusService.getCustomerOrderStatusByCode(CustomerOrderStatus.BEING_PROCESSED), null,
                    assignationType);

            foundCustomerOrder = findInOrdersAndAssignEmployee(customerOrders, assignationType, isPriority, false,
                    complexity, byPassAssignation);
            if (foundCustomerOrder != null)
                return foundCustomerOrder;
        }

        // Announcement
        if (currentUser.getAdPath().contains("Insertions")) {
            assignationType = constantService.getAssignationTypePublisciste();
            customerOrders = customerOrderService.findCustomerOrderByPubliscisteAssigned(productionDirector,
                    customerOrderStatusService.getCustomerOrderStatusByCode(CustomerOrderStatus.BEING_PROCESSED), null,
                    assignationType);

            foundCustomerOrder = findInOrdersAndAssignEmployee(customerOrders, assignationType, isPriority, false,
                    complexity, byPassAssignation);
            if (foundCustomerOrder != null)
                return foundCustomerOrder;
        }

        return null;
    }

    private Integer findInOrdersAndAssignEmployee(List<CustomerOrder> customerOrders, AssignationType assignationType,
            Boolean checkIsPriority, Boolean checkUserAssignation, Integer complexity, Boolean byPassAssignation)
            throws OsirisException {
        Employee currentEmployee = employeeService.getCurrentEmployee();
        if (customerOrders != null && customerOrders.size() > 0) {
            for (CustomerOrder customerOrder : customerOrders) {

                // Get complexity
                Integer customerOrderComplexity = 4;
                if (complexity != null)
                    customerOrderComplexity = customerOrderService.getComplexity(customerOrder);

                for (CustomerOrderAssignation customerOrderAssignation : customerOrder.getCustomerOrderAssignations()) {
                    if (customerOrderAssignation.getAssignationType().getId().equals(assignationType.getId())
                            && customerOrderAssignation.getIsAssigned() == false) {
                        if (!checkUserAssignation || customerOrderAssignation.getEmployee() != null
                                && customerOrderAssignation.getEmployee().getId().equals(currentEmployee.getId())) {
                            if (!checkIsPriority || isPriorityOrder(customerOrder)) {
                                if (complexity == null || customerOrderComplexity == complexity) {
                                    if (!byPassAssignation)
                                        assignToEmployee(customerOrderAssignation, currentEmployee);
                                    return customerOrder.getId();
                                }
                            }
                        }
                    }
                }
            }

            // If not found, try easier orders
            for (CustomerOrder customerOrder : customerOrders) {
                // Get complexity
                Integer customerOrderComplexity = 4;
                if (complexity != null)
                    customerOrderComplexity = customerOrderService.getComplexity(customerOrder);

                if (complexity != null) {
                    for (CustomerOrderAssignation customerOrderAssignation : customerOrder
                            .getCustomerOrderAssignations()) {
                        if (customerOrderAssignation.getAssignationType().getId().equals(assignationType.getId())
                                && customerOrderAssignation.getIsAssigned() == false) {
                            if (!checkUserAssignation || customerOrderAssignation.getEmployee() != null
                                    && customerOrderAssignation.getEmployee().getId().equals(currentEmployee.getId())) {
                                if (!checkIsPriority || isPriorityOrder(customerOrder)) {
                                    if (complexity == null || customerOrderComplexity > complexity) {
                                        if (!byPassAssignation)
                                            assignToEmployee(customerOrderAssignation, currentEmployee);
                                        return customerOrder.getId();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    private void assignToEmployee(CustomerOrderAssignation customerOrderAssignation, Employee currentEmployee)
            throws OsirisException {
        customerOrderAssignation.setEmployee(currentEmployee);
        customerOrderAssignation.setIsAssigned(true);
        addOrUpdateCustomerOrderAssignation(customerOrderAssignation);

        if (customerOrderAssignation.getCustomerOrder().getAssoAffaireOrders() != null)
            for (AssoAffaireOrder assoAffaireOrder : customerOrderAssignation.getCustomerOrder().getAssoAffaireOrders())
                if (assoAffaireOrder.getServices() != null)
                    for (Service service : assoAffaireOrder.getServices())
                        if (service.getProvisions() != null)
                            for (Provision provision : service.getProvisions()) {
                                assignNewProvisionToUser(provision);
                            }
    }

    @Override
    public boolean isPriorityOrder(CustomerOrder customerOrder) throws OsirisException {
        if (customerOrder.getAssoAffaireOrders() != null)
            for (AssoAffaireOrder assoAffaireOrder : customerOrder.getAssoAffaireOrders())
                if (assoAffaireOrder.getServices() != null)
                    for (Service service : assoAffaireOrder.getServices())
                        if (service.getProvisions() != null)
                            for (Provision provision : service.getProvisions())
                                if (Boolean.TRUE.equals(provision.getIsPriority())
                                        || Boolean.TRUE.equals(provision.getIsEmergency())
                                        || isOnlyAl(customerOrder))
                                    return true;
        return false;
    }

    private boolean isOnlyAl(CustomerOrder customerOrder) throws OsirisException {
        if (customerOrder != null && customerOrder.getAssoAffaireOrders() != null)
            for (AssoAffaireOrder asso : customerOrder.getAssoAffaireOrders())
                for (Service service : asso.getServices())
                    for (Provision provision : service.getProvisions())
                        if (provision.getAnnouncement() == null
                                && (provision.getSimpleProvision() == null || !provision.getProvisionFamilyType()
                                        .getId().equals(constantService.getProvisionFamilyTypeBalo().getId())
                                        && !provision.getProvisionFamilyType().getId()
                                                .equals(constantService.getProvisionFamilyTypeBodacc().getId())))
                            return false;
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String getFondTypeToUse(Integer complexity) throws OsirisException {
        Integer fondOrderId = getNextOrderForFond(false, complexity, true);
        Integer commonOrderId = getNextOrderForCommon(false, complexity, true);

        if (commonOrderId == null && fondOrderId != null)
            return "{\"type\":\"FOND\"}";

        if (commonOrderId != null && fondOrderId == null)
            return "{\"type\":\"COMMON\"}";

        if (commonOrderId != null && fondOrderId != null) {
            CustomerOrder commonOrder = customerOrderService.getCustomerOrder(commonOrderId);
            CustomerOrder fondOrder = customerOrderService.getCustomerOrder(fondOrderId);
            if (commonOrder.getProductionEffectiveDateTime().isAfter(fondOrder.getProductionEffectiveDateTime()))
                return "{\"type\":\"FOND\"}";

            return "{\"type\":\"COMMON\"}";
        }
        return "{\"type\":\"COMMON\"}";
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignImmediatlyOrder(CustomerOrder customerOrder) throws OsirisException {
        customerOrder = customerOrderService.getCustomerOrder(customerOrder.getId());
        if (customerOrder.getCustomerOrderAssignations() != null)
            for (CustomerOrderAssignation customerOrderAssignation : customerOrder.getCustomerOrderAssignations()) {
                if (customerOrderAssignation.getEmployee() != null
                        && Boolean.FALSE.equals(customerOrderAssignation.getIsAssigned()))
                    customerOrderAssignation.setIsAssigned(true);
                addOrUpdateCustomerOrderAssignation(customerOrderAssignation);
            }

        for (AssoAffaireOrder assoAffaireOrder : customerOrder.getAssoAffaireOrders())
            if (assoAffaireOrder.getServices() != null)
                for (Service service : assoAffaireOrder.getServices())
                    if (service.getProvisions() != null)
                        for (Provision provision : service.getProvisions()) {
                            assignNewProvisionToUser(provision);
                        }

        notificationService.notifyImmediateAffactationOfOrder(customerOrder);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<CustomerOrder> getOrdersToAssignForFond(Employee employee, boolean onlyCurrentUser)
            throws OsirisException {
        List<CustomerOrder> orders = new ArrayList<CustomerOrder>();

        // Formaliste
        AssignationType assignationType = constantService.getAssignationTypeFormaliste();
        orders.addAll(customerOrderService.findCustomerOrderByFormalisteAssigned(
                employeeService.findEmployeesInTheSameOU(employee),
                customerOrderStatusService.getCustomerOrderStatusByCode(CustomerOrderStatus.BEING_PROCESSED),
                onlyCurrentUser ? employeeService.getCurrentEmployee() : null,
                assignationType));

        // Announcement
        assignationType = constantService.getAssignationTypePublisciste();
        orders.addAll(customerOrderService.findCustomerOrderByPubliscisteAssigned(
                employeeService.findEmployeesInTheSameOU(employee),
                customerOrderStatusService.getCustomerOrderStatusByCode(CustomerOrderStatus.BEING_PROCESSED),
                onlyCurrentUser ? employeeService.getCurrentEmployee() : null,
                assignationType));

        orders.sort(new Comparator<CustomerOrder>() {
            @Override
            public int compare(CustomerOrder c0, CustomerOrder c1) {
                return c0.getProductionEffectiveDateTime().compareTo(c1.getProductionEffectiveDateTime());
            }
        });

        return customerOrderService.completeAdditionnalInformationForCustomerOrders(orders, false);
    }

    @Override
    public List<ICustomerOrderAssignationStatistics> getCustomerOrderAssignationStatisticsForFormalistes()
            throws OsirisException {
        return customerOrderAssignationRepository.getCustomerOrderAssignationStatisticsForFormalistes(
                customerOrderStatusService.getCustomerOrderStatusByCode(CustomerOrderStatus.BEING_PROCESSED).getId(),
                constantService.getAssignationTypeFormaliste().getId());
    }

    @Override
    public List<ICustomerOrderAssignationStatistics> getCustomerOrderAssignationStatisticsForInsertions()
            throws OsirisException {
        return customerOrderAssignationRepository.getCustomerOrderAssignationStatisticsForInsertions(
                customerOrderStatusService.getCustomerOrderStatusByCode(CustomerOrderStatus.BEING_PROCESSED).getId(),
                constantService.getAssignationTypePublisciste().getId());
    }
}
