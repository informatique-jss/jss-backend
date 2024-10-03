package com.jss.osiris.modules.osiris.miscellaneous.service;

import java.util.List;

import com.jss.osiris.modules.osiris.miscellaneous.model.AttachmentType;

public interface AttachmentTypeService {
    public List<AttachmentType> getAttachmentTypes();

    public AttachmentType getAttachmentType(Integer id);

    public AttachmentType addOrUpdateAttachmentType(AttachmentType attachmentType);
}
