package com.jss.osiris.libs.batch.service.threads;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.osiris.quotation.service.CustomerOrderService;

@Service
public class PurgeCustomerOrderThread implements IOsirisThread {

        @Autowired
        CustomerOrderService customerOrderService;

        public String getBatchCode() {
                return Batch.PURGE_CUSTOMER_ORDER;
        }

        @Transactional(rollbackFor = Exception.class)
        public void executeTask(Integer entityId)
                        throws OsirisValidationException, OsirisException, OsirisClientMessageException,
                        OsirisDuplicateException {

                customerOrderService.addOrUpdateCustomerOrderStatus(customerOrderService.getCustomerOrder(entityId),
                                CustomerOrderStatus.ABANDONED, true);
        }
}
