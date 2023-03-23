package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.miscellaneous.model.AttachmentType;
import com.jss.osiris.modules.miscellaneous.repository.AttachmentTypeRepository;

@Service
public class AttachmentTypeServiceImpl implements AttachmentTypeService {

    @Autowired
    AttachmentTypeRepository attachmentTypeRepository;

    @Override
    @Cacheable(value = "attachmentTypeList", key = "#root.methodName")
    public List<AttachmentType> getAttachmentTypes() {
        return IterableUtils.toList(attachmentTypeRepository.findAll());
    }

    @Override
    @Cacheable(value = "attachmentType", key = "#id")
    public AttachmentType getAttachmentType(Integer id) {
        Optional<AttachmentType> attachmentType = attachmentTypeRepository.findById(id);
        if (attachmentType.isPresent())
            return attachmentType.get();
        return null;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "attachmentTypeList", allEntries = true),
            @CacheEvict(value = "attachmentType", key = "#attachmentType.id")
    })
    @Transactional(rollbackFor = Exception.class)
    public AttachmentType addOrUpdateAttachmentType(
            AttachmentType attachmentType) {
        return attachmentTypeRepository.save(attachmentType);
    }
}
