package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.miscellaneous.model.AttachmentType;
import com.jss.osiris.modules.miscellaneous.repository.AttachmentTypeRepository;

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
