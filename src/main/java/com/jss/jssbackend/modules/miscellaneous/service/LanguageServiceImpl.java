package com.jss.jssbackend.modules.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.jssbackend.modules.miscellaneous.model.Language;
import com.jss.jssbackend.modules.miscellaneous.repository.LanguageRepository;

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
        if (!language.isEmpty())
            return language.get();
        return null;
    }
}
