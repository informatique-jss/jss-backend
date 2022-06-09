package com.jss.jssbackend.modules.quotation.service;

import java.util.List;

import com.jss.jssbackend.modules.quotation.model.NoticeTypeFamily;

public interface NoticeTypeFamilyService {
    public List<NoticeTypeFamily> getNoticeTypeFamilies();

    public NoticeTypeFamily getNoticeTypeFamily(Integer id);
}
