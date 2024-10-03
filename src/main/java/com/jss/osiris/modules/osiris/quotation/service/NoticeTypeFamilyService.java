package com.jss.osiris.modules.osiris.quotation.service;

import java.util.List;

import com.jss.osiris.modules.osiris.quotation.model.NoticeTypeFamily;

public interface NoticeTypeFamilyService {
    public List<NoticeTypeFamily> getNoticeTypeFamilies();

    public NoticeTypeFamily getNoticeTypeFamily(Integer id);

    public NoticeTypeFamily addOrUpdateNoticeTypeFamily(NoticeTypeFamily noticeTypeFamily);

}
