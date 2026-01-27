package com.jss.osiris.modules.osiris.quotation.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.miscellaneous.model.ActiveDirectoryGroup;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderComment;
import com.jss.osiris.modules.osiris.quotation.model.Provision;
import com.jss.osiris.modules.osiris.quotation.model.Quotation;

public interface CustomerOrderCommentService {
        public List<CustomerOrderComment> getCustomerOrderComments();

        public CustomerOrderComment getCustomerOrderComment(Integer id);

        public List<CustomerOrderComment> getCustomerOrderCommentForOrder(CustomerOrder customerOrder);

        public CustomerOrderComment addOrUpdateCustomerOrderComment(CustomerOrderComment customerOrderComment);

        public CustomerOrderComment createCustomerOrderComment(CustomerOrder customerOrder, String contentComment,
                        Boolean doNotNotify, Boolean isToDisplayToCustomer)
                        throws OsirisException;

        public CustomerOrderComment tagActiveDirectoryGroupOnCustomerOrderComment(
                        CustomerOrderComment customerOrderComment,
                        ActiveDirectoryGroup activeDirectoryGroup);

        public List<CustomerOrderComment> getCustomerOrderCommentForQuotation(Quotation quotation);

        public List<CustomerOrderComment> getCustomerOrderCommentForProvision(Provision provision);

        public List<CustomerOrderComment> getCommentsFromTchatForOrder(CustomerOrder customerOrder);

}
