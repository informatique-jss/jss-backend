package com.jss.jssbackend.modules.tiers.service;

import java.util.List;
import java.util.Optional;

import com.jss.jssbackend.modules.tiers.model.AttachmentType;
import com.jss.jssbackend.modules.tiers.repository.AttachmentTypeRepository;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AttachmentTypeServiceImpl implements AttachmentTypeService {

    @Autowired
    AttachmentTypeRepository attachmentTypeRepository;

    @Override
    public List<AttachmentType> getAttachmentTypes() {
        return IterableUtils.toList(attachmentTypeRepository.findAll());
    }

    @Override
    public AttachmentType getAttachmentType(Integer id) {
        Optional<AttachmentType> attachmentType = attachmentTypeRepository.findById(id);
        if (!attachmentType.isEmpty())
            return attachmentType.get();
        return null;
    }
}
