package com.jss.osiris.modules.quotation.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.quotation.repository.CustomerOrderStatusRepository;

@Service
public class CustomerOrderStatusServiceImpl implements CustomerOrderStatusService {

    @Autowired
    CustomerOrderStatusRepository customerOrderStatusRepository;

    @Override
    public List<CustomerOrderStatus> getCustomerOrderStatus() {
        return IterableUtils.toList(customerOrderStatusRepository.findAll());
    }

    @Override
    public CustomerOrderStatus getCustomerOrderStatus(Integer id) {
        Optional<CustomerOrderStatus> quotationStatus = customerOrderStatusRepository.findById(id);
        if (quotationStatus.isPresent())
            return quotationStatus.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustomerOrderStatus addOrUpdateCustomerOrderStatus(CustomerOrderStatus quotationStatus) {
        return customerOrderStatusRepository.save(quotationStatus);
    }

    @Override
    public CustomerOrderStatus getCustomerOrderStatusByCode(String code) {
        return customerOrderStatusRepository.findByCode(code);
    }

    @PostConstruct
    public void updateStatusReferential() throws Exception {
        updateStatus(CustomerOrderStatus.OPEN, "Ouvert", "auto_awesome");
        updateStatus(CustomerOrderStatus.TO_VERIFY, "A vérifier", "search");
        updateStatus(CustomerOrderStatus.VALIDATED_BY_JSS, "Validé par JSS", "task_alt");
        updateStatus(CustomerOrderStatus.SENT_TO_CUSTOMER, "Envoyé au client", "outgoing_mail");
        updateStatus(CustomerOrderStatus.VALIDATED_BY_CUSTOMER, "Validé par le client", "approval");
        updateStatus(CustomerOrderStatus.REFUSED_BY_CUSTOMER, "Refusé par le client", "remove_shopping_cart");
        updateStatus(CustomerOrderStatus.ABANDONED, "Abandonné", "block");
        updateStatus(CustomerOrderStatus.BILLED, "Facturée", "task_alt");
        updateStatus(CustomerOrderStatus.WAITING_DEPOSIT, "En attente d'acompte", "hourglass_top");
        updateStatus(CustomerOrderStatus.BEING_PROCESSED, "En cours de traitement", "groups_2");
        updateStatus(CustomerOrderStatus.TO_BILLED, "A facturer", "pending");

        setSuccessor(CustomerOrderStatus.OPEN, CustomerOrderStatus.TO_VERIFY);
        setSuccessor(CustomerOrderStatus.TO_VERIFY, CustomerOrderStatus.VALIDATED_BY_JSS);
        setSuccessor(CustomerOrderStatus.VALIDATED_BY_JSS, CustomerOrderStatus.SENT_TO_CUSTOMER);
        setSuccessor(CustomerOrderStatus.SENT_TO_CUSTOMER, CustomerOrderStatus.VALIDATED_BY_CUSTOMER);
        setSuccessor(CustomerOrderStatus.SENT_TO_CUSTOMER, CustomerOrderStatus.REFUSED_BY_CUSTOMER);
        setSuccessor(CustomerOrderStatus.REFUSED_BY_CUSTOMER, CustomerOrderStatus.TO_VERIFY);
        setSuccessor(CustomerOrderStatus.VALIDATED_BY_CUSTOMER, CustomerOrderStatus.WAITING_DEPOSIT);
        setSuccessor(CustomerOrderStatus.WAITING_DEPOSIT, CustomerOrderStatus.BEING_PROCESSED);
        setSuccessor(CustomerOrderStatus.BEING_PROCESSED, CustomerOrderStatus.TO_BILLED);
        setSuccessor(CustomerOrderStatus.TO_BILLED, CustomerOrderStatus.BILLED);

        setPredecessor(CustomerOrderStatus.TO_VERIFY, CustomerOrderStatus.OPEN);
        setPredecessor(CustomerOrderStatus.VALIDATED_BY_JSS, CustomerOrderStatus.TO_VERIFY);
        setPredecessor(CustomerOrderStatus.SENT_TO_CUSTOMER, CustomerOrderStatus.TO_VERIFY);
        setPredecessor(CustomerOrderStatus.TO_BILLED, CustomerOrderStatus.BEING_PROCESSED);

        // All cancelled

        setSuccessor(CustomerOrderStatus.OPEN, CustomerOrderStatus.ABANDONED);
        setSuccessor(CustomerOrderStatus.TO_VERIFY, CustomerOrderStatus.ABANDONED);
        setSuccessor(CustomerOrderStatus.VALIDATED_BY_JSS, CustomerOrderStatus.ABANDONED);
        setSuccessor(CustomerOrderStatus.SENT_TO_CUSTOMER, CustomerOrderStatus.ABANDONED);
        setSuccessor(CustomerOrderStatus.VALIDATED_BY_CUSTOMER,
                CustomerOrderStatus.ABANDONED);
        setSuccessor(CustomerOrderStatus.REFUSED_BY_CUSTOMER, CustomerOrderStatus.ABANDONED);
        setSuccessor(CustomerOrderStatus.WAITING_DEPOSIT, CustomerOrderStatus.ABANDONED);
        setSuccessor(CustomerOrderStatus.BEING_PROCESSED, CustomerOrderStatus.ABANDONED);
        setSuccessor(CustomerOrderStatus.TO_BILLED, CustomerOrderStatus.ABANDONED);

    }

    protected void updateStatus(String code, String label, String icon) {
        CustomerOrderStatus CustomerOrderStatus = getCustomerOrderStatusByCode(code);
        if (getCustomerOrderStatusByCode(code) == null)
            CustomerOrderStatus = new CustomerOrderStatus();
        CustomerOrderStatus.setCode(code);
        CustomerOrderStatus.setLabel(label);
        CustomerOrderStatus.setIcon(icon);
        addOrUpdateCustomerOrderStatus(CustomerOrderStatus);
    }

    protected void setSuccessor(String code, String code2) throws Exception {
        CustomerOrderStatus sourceStatus = getCustomerOrderStatusByCode(code);
        CustomerOrderStatus targetStatus = getCustomerOrderStatusByCode(code2);
        if (sourceStatus == null || targetStatus == null)
            throw new Exception("Status code " + code + " or " + code2 + " do not exist");

        if (sourceStatus.getSuccessors() == null)
            sourceStatus.setSuccessors(new ArrayList<CustomerOrderStatus>());

        for (CustomerOrderStatus status : sourceStatus.getSuccessors())
            if (status.getCode().equals(targetStatus.getCode()))
                return;

        sourceStatus.getSuccessors().add(targetStatus);
        addOrUpdateCustomerOrderStatus(sourceStatus);
    }

    protected void setPredecessor(String code, String code2) throws Exception {
        CustomerOrderStatus sourceStatus = getCustomerOrderStatusByCode(code);
        CustomerOrderStatus targetStatus = getCustomerOrderStatusByCode(code2);
        if (sourceStatus == null || targetStatus == null)
            throw new Exception("Quotation status code " + code + " or " + code2 + " do not exist");

        if (sourceStatus.getPredecessors() == null)
            sourceStatus.setPredecessors(new ArrayList<CustomerOrderStatus>());

        for (CustomerOrderStatus status : sourceStatus.getPredecessors())
            if (status.getCode().equals(targetStatus.getCode()))
                return;

        sourceStatus.getPredecessors().add(targetStatus);
        addOrUpdateCustomerOrderStatus(sourceStatus);
    }

}
