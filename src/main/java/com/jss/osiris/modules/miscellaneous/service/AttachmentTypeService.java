package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;

import com.jss.osiris.modules.miscellaneous.model.AttachmentType;

public interface AttachmentTypeService {
    public List<AttachmentType> getAttachmentTypes();

    public AttachmentType getAttachmentType(Integer id);
}
