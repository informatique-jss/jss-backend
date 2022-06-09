package com.jss.jssbackend.modules.quotation.service;

import java.util.List;
import java.util.Optional;

import com.jss.jssbackend.modules.quotation.model.NoticeTypeFamily;
import com.jss.jssbackend.modules.quotation.repository.NoticeTypeFamilyRepository;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        if (!noticeTypeFamily.isEmpty())
            return noticeTypeFamily.get();
        return null;
    }
}
