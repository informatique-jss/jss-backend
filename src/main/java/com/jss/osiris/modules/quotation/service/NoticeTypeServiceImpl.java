package com.jss.osiris.modules.quotation.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.NoticeType;
import com.jss.osiris.modules.quotation.repository.NoticeTypeRepository;

@Service
public class NoticeTypeServiceImpl implements NoticeTypeService {

    @Autowired
    NoticeTypeRepository noticeTypeRepository;

    @Override
    public List<NoticeType> getNoticeTypes() {
        return IterableUtils.toList(noticeTypeRepository.findAll());
    }

    @Override
    public NoticeType getNoticeType(Integer id) {
        Optional<NoticeType> noticeType = noticeTypeRepository.findById(id);
        if (!noticeType.isEmpty())
            return noticeType.get();
        return null;
    }
}
