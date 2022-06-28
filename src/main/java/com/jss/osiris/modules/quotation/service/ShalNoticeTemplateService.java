package com.jss.osiris.modules.quotation.service;

import java.util.List;

import com.jss.osiris.modules.quotation.model.ShalNoticeTemplate;

public interface ShalNoticeTemplateService {
    public List<ShalNoticeTemplate> getShalNoticeTemplates();

    public ShalNoticeTemplate getShalNoticeTemplate(Integer id);
	
	 public ShalNoticeTemplate addOrUpdateShalNoticeTemplate(ShalNoticeTemplate shalNoticeTemplate);
}
