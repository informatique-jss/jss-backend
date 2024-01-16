package com.jss.osiris.modules.quotation.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.quotation.model.AbandonReason;
import com.jss.osiris.modules.quotation.repository.AbandonReasonRepository;
import com.jss.osiris.modules.quotation.repository.CustomerOrderRepository;
import com.jss.osiris.modules.quotation.model.CustomerOrder;

@Service
public class AbandonReasonServiceImpl implements AbandonReasonService {

    @Autowired
    AbandonReasonRepository abandonReasonRepository;

    @Autowired
    CustomerOrderRepository customerOrderRepository;

    @Override
    public List<AbandonReason> getAbandonReasons() {
        return IterableUtils.toList(abandonReasonRepository.findAll());
    }

    @Override
    public AbandonReason getAbandonReason(Integer id) {
        Optional<AbandonReason> abandonReason = abandonReasonRepository.findById(id);
        if (abandonReason.isPresent())
            return abandonReason.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AbandonReason addOrUpdateCustomerOrderAbandonReason(AbandonReason abandonReason, String id_commande) {
        Integer id_commandeParsed = Integer.parseInt(id_commande);

        Optional<CustomerOrder> customerOrderOptional = customerOrderRepository.findById(id_commandeParsed);

        if (customerOrderOptional.isPresent()) {
            CustomerOrder customerOrder = customerOrderOptional.get();
            customerOrder.setAbandonReason(abandonReason);
            customerOrderRepository.save(customerOrder);

            return abandonReason;
        } else {
            throw new RuntimeException("CustomerOrder com o ID " + id_commandeParsed + " não encontrado.");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AbandonReason addOrUpdateAbandonReason(AbandonReason abandonReason) {
        return abandonReasonRepository.save(abandonReason);
    }

}