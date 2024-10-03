package com.jss.osiris.modules.osiris.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.osiris.miscellaneous.model.AttachmentType;
import com.jss.osiris.modules.osiris.miscellaneous.repository.AttachmentTypeRepository;

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
        if (attachmentType.isPresent())
            return attachmentType.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AttachmentType addOrUpdateAttachmentType(
            AttachmentType attachmentType) {
        return attachmentTypeRepository.save(attachmentType);
    }
}
