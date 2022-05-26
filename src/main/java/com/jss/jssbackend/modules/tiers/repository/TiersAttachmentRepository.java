package com.jss.jssbackend.modules.tiers.repository;

import java.util.List;

import com.jss.jssbackend.modules.tiers.model.Tiers;
import com.jss.jssbackend.modules.tiers.model.TiersAttachment;

import org.springframework.data.repository.CrudRepository;

public interface TiersAttachmentRepository extends CrudRepository<TiersAttachment, Integer> {
    List<TiersAttachment> findByTiers(Tiers tiers);
}