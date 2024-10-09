package com.jss.osiris.modules.osiris.quotation.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.osiris.miscellaneous.model.ActiveDirectoryGroup;
import com.jss.osiris.modules.osiris.profile.model.Employee;
import com.jss.osiris.modules.osiris.profile.model.IOsirisUser;
import com.jss.osiris.modules.osiris.profile.service.EmployeeService;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderComment;
import com.jss.osiris.modules.osiris.quotation.repository.CustomerOrderCommentRepository;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

@Service
public class CustomerOrderCommentServiceImpl implements CustomerOrderCommentService {

    @Autowired
    CustomerOrderCommentRepository customerOrderCommentRepository;

    @Autowired
    EmployeeService employeeService;

    @Override
    public List<CustomerOrderComment> getCustomerOrderComments() {
        return IterableUtils.toList(customerOrderCommentRepository.findAll());
    }

    @Override
    public CustomerOrderComment getCustomerOrderComment(Integer id) {
        Optional<CustomerOrderComment> customerOrderComment = customerOrderCommentRepository.findById(id);
        if (customerOrderComment.isPresent())
            return customerOrderComment.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustomerOrderComment addOrUpdateCustomerOrderComment(
            CustomerOrderComment customerOrderComment) {
        return customerOrderCommentRepository.save(customerOrderComment);
    }

    @Override
    public CustomerOrderComment createCustomerOrderComment(CustomerOrder customerOrder, String contentComment) {
        CustomerOrderComment customerOrderComment = new CustomerOrderComment();
        customerOrderComment.setCustomerOrder(customerOrder);
        customerOrderComment.setComment(contentComment);

        IOsirisUser employee = employeeService.getCurrentEmployee();
        if (employee != null && employee instanceof Employee)
            customerOrderComment.setEmployee((Employee) employee);
        if (employee != null && employee instanceof Responsable)
            customerOrderComment.setCurrentCustomer((Responsable) employee);
        customerOrderComment.setCreatedDateTime(LocalDateTime.now());
        customerOrderComment.setIsRead(false);

        return addOrUpdateCustomerOrderComment(customerOrderComment);
    }

    @Override
    public CustomerOrderComment tagActiveDirectoryGroupOnCustomerOrderComment(CustomerOrderComment customerOrderComment,
            ActiveDirectoryGroup activeDirectoryGroup) {
        if (customerOrderComment != null && activeDirectoryGroup != null) {
            if (customerOrderComment.getActiveDirectoryGroups() == null
                    || customerOrderComment.getActiveDirectoryGroups().size() == 0)
                customerOrderComment.setActiveDirectoryGroups(new ArrayList<ActiveDirectoryGroup>());
            customerOrderComment.getActiveDirectoryGroups().add(activeDirectoryGroup);
        }
        return addOrUpdateCustomerOrderComment(customerOrderComment);
    }
}
