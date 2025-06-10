package com.jss.osiris.modules.myjss.wordpress.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.myjss.wordpress.model.Newspaper;
import com.jss.osiris.modules.myjss.wordpress.repository.NewspaperRepository;

@Service
public class NewspaperServiceImpl implements NewspaperService {

    @Autowired
    NewspaperRepository newspaperRepository;

    public Newspaper addOrUpdateNewspaper(Newspaper Newspaper) {
        return newspaperRepository.save(Newspaper);
    }

    @Override
    public Newspaper getNewspaper(Integer newspaperId) {
        return newspaperRepository.findById(newspaperId).get();
    }

}
