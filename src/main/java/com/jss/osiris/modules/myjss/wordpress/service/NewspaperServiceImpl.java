package com.jss.osiris.modules.myjss.wordpress.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.myjss.wordpress.model.Newspaper;
import com.jss.osiris.modules.myjss.wordpress.model.Subscription;
import com.jss.osiris.modules.myjss.wordpress.repository.NewspaperRepository;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

@Service
public class NewspaperServiceImpl implements NewspaperService {

    @Autowired
    NewspaperRepository newspaperRepository;

    @Autowired
    SubscriptionService subscriptionService;

    public Newspaper addOrUpdateNewspaper(Newspaper Newspaper) {
        return newspaperRepository.save(Newspaper);
    }

    @Override
    public Newspaper getNewspaper(Integer newspaperId) {
        return newspaperRepository.findById(newspaperId).get();
    }

    @Override
    public List<Newspaper> getNewspapers() {
        return IterableUtils.toList(newspaperRepository.findAll());
    }

    @Override
    public List<Newspaper> getNewspaperForYear(Integer year) throws IOException {
        LocalDateTime startOfYear = LocalDateTime.of(year, 1, 1, 0, 0);
        LocalDateTime startOfNextYear = startOfYear.plusYears(1);
        List<Newspaper> fetchedNewspapers = newspaperRepository.findByDateRange(startOfYear, startOfNextYear);
        for (Newspaper newspaper : fetchedNewspapers) {
            newspaper.setNewspaperImage(Files.readAllBytes(Paths.get(newspaper.getUploadedFileImage().getPath())));
        }
        return fetchedNewspapers;
    }

    @Override
    public Boolean canSeeAllNewspapersOfKiosk(Responsable responsable) throws IOException {

        List<Subscription> subscriptions = subscriptionService.getSubscriptionsForMail(responsable.getMail());

        return subscriptionService.isResponsableHasFullValidSubscription(responsable, subscriptions);
    }

    @Override
    public List<Integer> getSeeableNewspapersForResponsable(Responsable responsable) {

        List<Newspaper> newspapers = getNewspapers();
        List<Integer> seeableNewspapersIds = new ArrayList<>();

        for (Newspaper newspaper : newspapers) {
            if (subscriptionService.canResponsableSeeKioskNewspaper(responsable, newspaper)) {
                seeableNewspapersIds.add(newspaper.getId());
            }
        }

        return seeableNewspapersIds;
    }
}
