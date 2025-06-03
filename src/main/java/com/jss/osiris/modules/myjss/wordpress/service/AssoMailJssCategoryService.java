package com.jss.osiris.modules.myjss.wordpress.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.myjss.wordpress.model.AssoMailJssCategory;
import com.jss.osiris.modules.myjss.wordpress.model.JssCategory;
import com.jss.osiris.modules.osiris.miscellaneous.model.Mail;

public interface AssoMailJssCategoryService {

    public AssoMailJssCategory addNewJssCategoryFollow(JssCategory jssCategory) throws OsirisException;

    public AssoMailJssCategory getAssoMailJssCategoryByMailAndJssCategory(JssCategory jssCategory);

    public Boolean getIsAssoMailJssCategoryByMailAndJssCategory(JssCategory jssCategory);

    public AssoMailJssCategory updateJssCategoryConsultationDate(Mail mail, JssCategory jssCategory);

    public List<AssoMailJssCategory> getAssoMailJssCategoryForCurrentUser();

    public AssoMailJssCategory getAssoMailJssCategory(Integer id);

    public void deleteAssoMailJssCategory(JssCategory jssCategory) throws OsirisValidationException;

    public AssoMailJssCategory addOrUpdateAssoMailJssCategory(AssoMailJssCategory assoMailJssCategory);
}
