package com.jss.osiris.modules.quotation.service;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import com.jss.osiris.modules.profile.service.EmployeeService;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.CustomerOrderComment;
import com.jss.osiris.modules.quotation.repository.CustomerOrderCommentRepository;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        customerOrderComment.setEmployee(employeeService.getCurrentEmployee());
        customerOrderComment.setCreatedDateTime(LocalDateTime.now());
        return addOrUpdateCustomerOrderComment(customerOrderComment);
    }
}
