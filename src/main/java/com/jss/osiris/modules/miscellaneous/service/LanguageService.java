package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;

import com.jss.osiris.modules.miscellaneous.model.Language;

public interface LanguageService {
    public List<Language> getLanguages();

    public Language getLanguage(Integer id);
	
	 public Language addOrUpdateLanguage(Language language);
}
