package com.jss.osiris.modules.quotation.service;

import java.util.List;
import java.util.Optional;

import com.jss.osiris.modules.quotation.model.ShalNoticeTemplate;
import com.jss.osiris.modules.quotation.repository.ShalNoticeTemplateRepository;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShalNoticeTemplateServiceImpl implements ShalNoticeTemplateService {

    @Autowired
    ShalNoticeTemplateRepository shalNoticeTemplateRepository;

    @Override
    public List<ShalNoticeTemplate> getShalNoticeTemplates() {
        return IterableUtils.toList(shalNoticeTemplateRepository.findAll());
    }

    @Override
    public ShalNoticeTemplate getShalNoticeTemplate(Integer id) {
        Optional<ShalNoticeTemplate> shalNoticeTemplate = shalNoticeTemplateRepository.findById(id);
        if (!shalNoticeTemplate.isEmpty())
            return shalNoticeTemplate.get();
        return null;
    }
}
