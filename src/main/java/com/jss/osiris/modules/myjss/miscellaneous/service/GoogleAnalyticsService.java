package com.jss.osiris.modules.myjss.miscellaneous.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.quotation.model.Affaire;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.IQuotation;
import com.jss.osiris.modules.osiris.quotation.model.ServiceFamily;
import com.jss.osiris.modules.osiris.quotation.model.ServiceType;

@Service
public interface GoogleAnalyticsService {

        public void trackPurchase(CustomerOrder customerOrder) throws OsirisException;

        public void trackViewItemList(List<ServiceType> serviceTypes, Affaire affaire, ServiceFamily itemList,
                        String gaClientId) throws OsirisException;

        public void trackAddToCart(ServiceType serviceType, Affaire affaire, String gaClientId) throws OsirisException;

        public void trackRemoveFromCart(ServiceType serviceTypes, Affaire affaire, String gaClientId)
                        throws OsirisException;

        public void trackBeginCheckout(IQuotation quotation, String gaClientId) throws OsirisException;

        public void trackAddPaymentInfo(IQuotation quotation, String gaClientId) throws OsirisException;

}
