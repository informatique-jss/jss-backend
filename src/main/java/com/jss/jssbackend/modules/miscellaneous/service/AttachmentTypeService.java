package com.jss.jssbackend.modules.miscellaneous.service;

import java.util.List;

import com.jss.jssbackend.modules.miscellaneous.model.AttachmentType;

public interface AttachmentTypeService {
    public List<AttachmentType> getAttachmentTypes();

    public AttachmentType getAttachmentType(Integer id);
}
