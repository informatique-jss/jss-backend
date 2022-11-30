package com.jss.osiris.modules.quotation.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.quotation.repository.CustomerOrderStatusRepository;

@Service
public class CustomerOrderStatusServiceImpl implements CustomerOrderStatusService {

    @Autowired
    CustomerOrderStatusRepository customerOrderStatusRepository;

    @Override
    @Cacheable(value = "customerOrderStatusList", key = "#root.methodName")
    public List<CustomerOrderStatus> getCustomerOrderStatus() {
        return IterableUtils.toList(customerOrderStatusRepository.findAll());
    }

    @Override
    @Cacheable(value = "customerOrderStatus", key = "#id")
    public CustomerOrderStatus getCustomerOrderStatus(Integer id) {
        Optional<CustomerOrderStatus> quotationStatus = customerOrderStatusRepository.findById(id);
        if (quotationStatus.isPresent())
            return quotationStatus.get();
        return null;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "customerOrderStatusList", allEntries = true),
            @CacheEvict(value = "customerOrderStatus", key = "#bodaccStatus.id")
    })
    @Transactional(rollbackFor = Exception.class)
    public CustomerOrderStatus addOrUpdateCustomerOrderStatus(CustomerOrderStatus quotationStatus) {
        return customerOrderStatusRepository.save(quotationStatus);
    }

    @Override
    public CustomerOrderStatus getCustomerOrderStatusByCode(String code) {
        return customerOrderStatusRepository.findByCode(code);
    }

    @Override
    public void updateStatusReferential() throws OsirisException {
        updateStatus(CustomerOrderStatus.OPEN, "Ouvert", "auto_awesome");
        updateStatus(CustomerOrderStatus.BEING_PROCESSED, "En cours de traitement", "groups_2");
        updateStatus(CustomerOrderStatus.WAITING_DEPOSIT, "En attente d'acompte", "hourglass_top");
        updateStatus(CustomerOrderStatus.TO_BILLED, "A facturer", "pending");
        updateStatus(CustomerOrderStatus.BILLED, "Facturée", "task_alt");
        updateStatus(CustomerOrderStatus.ABANDONED, "Abandonné", "block");

        setSuccessor(CustomerOrderStatus.OPEN, CustomerOrderStatus.WAITING_DEPOSIT);
        setSuccessor(CustomerOrderStatus.OPEN, CustomerOrderStatus.BEING_PROCESSED);
        setSuccessor(CustomerOrderStatus.WAITING_DEPOSIT, CustomerOrderStatus.BEING_PROCESSED);
        setSuccessor(CustomerOrderStatus.TO_BILLED, CustomerOrderStatus.BILLED);

        setPredecessor(CustomerOrderStatus.WAITING_DEPOSIT, CustomerOrderStatus.OPEN);
        setPredecessor(CustomerOrderStatus.TO_BILLED, CustomerOrderStatus.BEING_PROCESSED);
        setPredecessor(CustomerOrderStatus.BILLED, CustomerOrderStatus.TO_BILLED);

        // All cancelled
        setSuccessor(CustomerOrderStatus.OPEN, CustomerOrderStatus.ABANDONED);
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
            throw new OsirisException("Status code " + code + " or " + code2 + " do not exist");

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
            throw new OsirisException("Quotation status code " + code + " or " + code2 + " do not exist");

        if (sourceStatus.getPredecessors() == null)
            sourceStatus.setPredecessors(new ArrayList<CustomerOrderStatus>());

        for (CustomerOrderStatus status : sourceStatus.getPredecessors())
            if (status.getCode().equals(targetStatus.getCode()))
                return;

        sourceStatus.getPredecessors().add(targetStatus);
        addOrUpdateCustomerOrderStatus(sourceStatus);
    }

}
