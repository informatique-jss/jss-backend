package com.jss.osiris.modules.quotation.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.quotation.model.OfferReason;
import com.jss.osiris.modules.quotation.repository.OfferReasonRepository;
import com.jss.osiris.modules.quotation.repository.CustomerOrderRepository;
import com.jss.osiris.modules.quotation.model.CustomerOrder;

@Service
public class OfferReasonServiceImpl implements OfferReasonService {

    @Autowired
    OfferReasonRepository offerReasonRepository;

    @Autowired
    CustomerOrderRepository customerOrderRepository;

    @Override
    public List<OfferReason> getOfferReasons() {
        return IterableUtils.toList(offerReasonRepository.findAll());
    }

    @Override
    public OfferReason getOfferReason(Integer id) {
        Optional<OfferReason> offerReason = offerReasonRepository.findById(id);
        if (offerReason.isPresent())
            return offerReason.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OfferReason addOrUpdateCustomerOrderOfferReason(OfferReason offerReason, String id_commande) {
        Integer id_commandeParsed = Integer.parseInt(id_commande);

        Optional<CustomerOrder> customerOrderOptional = customerOrderRepository.findById(id_commandeParsed);

        if (customerOrderOptional.isPresent()) {
            CustomerOrder customerOrder = customerOrderOptional.get();
            customerOrder.setOfferReason(offerReason);
            customerOrderRepository.save(customerOrder);

            return offerReason;
        } else {
            throw new RuntimeException("CustomerOrder com o ID " + id_commandeParsed + " não encontrado.");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OfferReason addOrUpdateOfferReason(OfferReason offerReason) {
        return offerReasonRepository.save(offerReason);
    }

}