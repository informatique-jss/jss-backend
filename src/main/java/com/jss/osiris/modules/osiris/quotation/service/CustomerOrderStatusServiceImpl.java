package com.jss.osiris.modules.osiris.quotation.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.osiris.quotation.repository.CustomerOrderStatusRepository;

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
    public CustomerOrderStatus addOrUpdateCustomerOrderStatus(CustomerOrderStatus quotationStatus) {
        return customerOrderStatusRepository.save(quotationStatus);
    }

    @Override
    public CustomerOrderStatus getCustomerOrderStatusByCode(String code) {
        return customerOrderStatusRepository.findByCode(code);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatusReferential() throws OsirisException {
        updateStatus(CustomerOrderStatus.DRAFT, "Ouvert", "auto_awesome");
        updateStatus(CustomerOrderStatus.BEING_PROCESSED, "En cours de traitement", "groups_2");
        updateStatus(CustomerOrderStatus.WAITING_DEPOSIT, "En attente d'acompte", "hourglass_top");
        updateStatus(CustomerOrderStatus.TO_BILLED, "A facturer", "pending");
        updateStatus(CustomerOrderStatus.BILLED, "Facturée", "task_alt");
        updateStatus(CustomerOrderStatus.ABANDONED, "Abandonné", "block");

        setSuccessor(CustomerOrderStatus.ABANDONED, CustomerOrderStatus.DRAFT);
        setSuccessor(CustomerOrderStatus.ABANDONED, CustomerOrderStatus.BEING_PROCESSED);
        setSuccessor(CustomerOrderStatus.DRAFT, CustomerOrderStatus.WAITING_DEPOSIT);
        setSuccessor(CustomerOrderStatus.DRAFT, CustomerOrderStatus.BEING_PROCESSED);
        setSuccessor(CustomerOrderStatus.WAITING_DEPOSIT, CustomerOrderStatus.BEING_PROCESSED);
        setSuccessor(CustomerOrderStatus.TO_BILLED, CustomerOrderStatus.BILLED);
        setSuccessor(CustomerOrderStatus.BEING_PROCESSED, CustomerOrderStatus.WAITING_DEPOSIT);

        setPredecessor(CustomerOrderStatus.WAITING_DEPOSIT, CustomerOrderStatus.DRAFT);
        setPredecessor(CustomerOrderStatus.TO_BILLED, CustomerOrderStatus.BEING_PROCESSED);
        setPredecessor(CustomerOrderStatus.BILLED, CustomerOrderStatus.TO_BILLED);

        // All cancelled
        setSuccessor(CustomerOrderStatus.DRAFT, CustomerOrderStatus.ABANDONED);
        setSuccessor(CustomerOrderStatus.WAITING_DEPOSIT, CustomerOrderStatus.ABANDONED);
        setSuccessor(CustomerOrderStatus.BEING_PROCESSED, CustomerOrderStatus.ABANDONED);
        setSuccessor(CustomerOrderStatus.TO_BILLED, CustomerOrderStatus.ABANDONED);

    }

    protected void updateStatus(String code, String label, String icon) {
        CustomerOrderStatus CustomerOrderStatus = getCustomerOrderStatusByCode(code);
        if (getCustomerOrderStatusByCode(code) == null)
            CustomerOrderStatus = new CustomerOrderStatus();
        CustomerOrderStatus.setPredecessors(null);
        CustomerOrderStatus.setSuccessors(null);
        CustomerOrderStatus.setCode(code);
        CustomerOrderStatus.setLabel(label);
        CustomerOrderStatus.setIcon(icon);
        addOrUpdateCustomerOrderStatus(CustomerOrderStatus);
    }

    protected void setSuccessor(String code, String code2) throws OsirisException {
        CustomerOrderStatus sourceStatus = getCustomerOrderStatusByCode(code);
        CustomerOrderStatus targetStatus = getCustomerOrderStatusByCode(code2);
        if (sourceStatus == null || targetStatus == null)
            throw new OsirisException(null, "Status code " + code + " or " + code2 + " do not exist");

        if (sourceStatus.getSuccessors() == null)
            sourceStatus.setSuccessors(new ArrayList<CustomerOrderStatus>());

        for (CustomerOrderStatus status : sourceStatus.getSuccessors())
            if (status.getCode().equals(targetStatus.getCode()))
                return;

        sourceStatus.getSuccessors().add(targetStatus);
        addOrUpdateCustomerOrderStatus(sourceStatus);
    }

    protected void setPredecessor(String code, String code2) throws OsirisException {
        CustomerOrderStatus sourceStatus = getCustomerOrderStatusByCode(code);
        CustomerOrderStatus targetStatus = getCustomerOrderStatusByCode(code2);
        if (sourceStatus == null || targetStatus == null)
            throw new OsirisException(null, "Quotation status code " + code + " or " + code2 + " do not exist");

        if (sourceStatus.getPredecessors() == null)
            sourceStatus.setPredecessors(new ArrayList<CustomerOrderStatus>());

        for (CustomerOrderStatus status : sourceStatus.getPredecessors())
            if (status.getCode().equals(targetStatus.getCode()))
                return;

        sourceStatus.getPredecessors().add(targetStatus);
        addOrUpdateCustomerOrderStatus(sourceStatus);
    }

}
