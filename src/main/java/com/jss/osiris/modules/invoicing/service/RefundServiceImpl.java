package com.jss.osiris.modules.invoicing.service;

import java.util.List;
import java.util.Optional;

import com.jss.osiris.modules.invoicing.model.Refund;
import com.jss.osiris.modules.invoicing.repository.RefundRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RefundServiceImpl implements RefundService {

    @Autowired
    RefundRepository refundRepository;

    @Override
	@Cacheable(value = "refundList", key = "#root.methodName")
    public List<Refund> getRefunds() {
        return IterableUtils.toList(refundRepository.findAll());
    }

    @Override
	@Cacheable(value = "refund", key = "#id")
    public Refund getRefund(Integer id) {
        Optional<Refund> refund = refundRepository.findById(id);
        if (!refund.isEmpty())
            return refund.get();
        return null;
    }
	
	 @Override
	 @Caching(evict = {
            @CacheEvict(value = "refundList", allEntries = true),
            @CacheEvict(value = "refund", key = "#refund.id")
    })
    public Refund addOrUpdateRefund(
            Refund refund) {
        return refundRepository.save(refund);
    }
}
