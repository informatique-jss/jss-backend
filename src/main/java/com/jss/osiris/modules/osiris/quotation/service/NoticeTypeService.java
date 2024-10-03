package com.jss.osiris.modules.osiris.quotation.service;

import java.util.List;

import com.jss.osiris.modules.osiris.quotation.model.NoticeType;

public interface NoticeTypeService {
    public List<NoticeType> getNoticeTypes();

    public NoticeType getNoticeType(Integer id);

    public NoticeType addOrUpdateNoticeType(NoticeType noticeType);
}
