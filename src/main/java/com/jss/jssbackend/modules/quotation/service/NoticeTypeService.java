package com.jss.jssbackend.modules.quotation.service;

import java.util.List;

import com.jss.jssbackend.modules.quotation.model.NoticeType;

public interface NoticeTypeService {
    public List<NoticeType> getNoticeTypes();

    public NoticeType getNoticeType(Integer id);
}
