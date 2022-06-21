package com.jss.osiris.modules.quotation.service;

import java.util.List;

import com.jss.osiris.modules.quotation.model.NoticeTypeFamily;

public interface NoticeTypeFamilyService {
    public List<NoticeTypeFamily> getNoticeTypeFamilies();

    public NoticeTypeFamily getNoticeTypeFamily(Integer id);
}
