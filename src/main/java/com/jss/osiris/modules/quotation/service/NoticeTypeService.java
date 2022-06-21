package com.jss.osiris.modules.quotation.service;

import java.util.List;

import com.jss.osiris.modules.quotation.model.NoticeType;

public interface NoticeTypeService {
    public List<NoticeType> getNoticeTypes();

    public NoticeType getNoticeType(Integer id);
}
