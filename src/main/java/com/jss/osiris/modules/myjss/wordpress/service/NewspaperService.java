package com.jss.osiris.modules.myjss.wordpress.service;

import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jss.osiris.modules.myjss.wordpress.model.Newspaper;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

public interface NewspaperService {

    public Newspaper addOrUpdateNewspaper(Newspaper newspaper);

    public Newspaper getNewspaper(Integer newspaperId);

    public List<Newspaper> getNewspapers();

    public Page<Newspaper> getNewspapers(Pageable pageable);

    public List<Newspaper> getNewspaperForYear(Integer year) throws IOException;

    public Boolean canSeeAllNewspapersOfKiosk(Responsable responsable) throws IOException;

    public List<Integer> getSeeableNewspapersForResponsable(Responsable responsable);

    public byte[] getNewspaperFrontImage(Integer newspaperId) throws IOException;

}
