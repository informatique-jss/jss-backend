package com.jss.osiris.modules.osiris.quotation.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.osiris.quotation.model.NoticeTypeFamily;
import com.jss.osiris.modules.osiris.quotation.repository.NoticeTypeFamilyRepository;

@Service
public class NoticeTypeFamilyServiceImpl implements NoticeTypeFamilyService {

    @Autowired
    NoticeTypeFamilyRepository noticeTypeFamilyRepository;

    @Override
    public List<NoticeTypeFamily> getNoticeTypeFamilies() {
        return IterableUtils.toList(noticeTypeFamilyRepository.findAll());
    }

    @Override
    public NoticeTypeFamily getNoticeTypeFamily(Integer id) {
        Optional<NoticeTypeFamily> noticeTypeFamily = noticeTypeFamilyRepository.findById(id);
        if (noticeTypeFamily.isPresent())
            return noticeTypeFamily.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public NoticeTypeFamily addOrUpdateNoticeTypeFamily(NoticeTypeFamily noticeTypeFamily) {
        return noticeTypeFamilyRepository.save(noticeTypeFamily);
    }
}
