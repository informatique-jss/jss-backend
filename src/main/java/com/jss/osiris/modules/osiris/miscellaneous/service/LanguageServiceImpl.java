package com.jss.osiris.modules.osiris.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.osiris.miscellaneous.model.Language;
import com.jss.osiris.modules.osiris.miscellaneous.repository.LanguageRepository;

@Service
public class LanguageServiceImpl implements LanguageService {

    @Autowired
    LanguageRepository languageRepository;

    @Override
    public List<Language> getLanguages() {
        return IterableUtils.toList(languageRepository.findAll());
    }

    @Override
    public Language getLanguage(Integer id) {
        Optional<Language> language = languageRepository.findById(id);
        if (language.isPresent())
            return language.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Language addOrUpdateLanguage(
            Language language) {
        return languageRepository.save(language);
    }
}
