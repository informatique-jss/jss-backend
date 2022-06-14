package com.jss.jssbackend.modules.miscellaneous.service;

import java.util.List;

import com.jss.jssbackend.modules.miscellaneous.model.Language;

public interface LanguageService {
    public List<Language> getLanguages();

    public Language getLanguage(Integer id);
}
