package com.jss.jssbackend.modules.tiers.service;

import java.util.List;

import com.jss.jssbackend.modules.tiers.model.AttachmentType;

public interface AttachmentTypeService {
    public List<AttachmentType> getAttachmentTypes();

    public AttachmentType getAttachmentType(Integer id);
}
