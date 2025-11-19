package com.jss.osiris.modules.osiris.crm.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.osiris.crm.model.Webinar;
import com.jss.osiris.modules.osiris.crm.repository.WebinarRepository;

@Service
public class WebinarServiceImpl implements WebinarService {

    @Autowired
    WebinarRepository webinarRepository;

    @Override
    public List<Webinar> getWebinars() {
        return IterableUtils.toList(webinarRepository.findAll());
    }

    @Override
    public Webinar getWebinar(Integer id) {
        Optional<Webinar> webinar = webinarRepository.findById(id);
        if (webinar.isPresent())
            return webinar.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Webinar addOrUpdateWebinar(
            Webinar webinar) {
        return webinarRepository.save(webinar);
    }

    @Override
    public Webinar getNextWebinar() {
        return webinarRepository.findFirstByWebinarDateAfterOrderByWebinarDateAsc(LocalDateTime.now());
    }

    @Override
    public Webinar getLastWebinar() {
        return webinarRepository.findFirstByWebinarDateBeforeOrderByWebinarDateDesc(LocalDateTime.now());
    }
}
