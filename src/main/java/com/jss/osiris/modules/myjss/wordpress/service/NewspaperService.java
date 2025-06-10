package com.jss.osiris.modules.myjss.wordpress.service;

import com.jss.osiris.modules.myjss.wordpress.model.Newspaper;

public interface NewspaperService {

    public Newspaper addOrUpdateNewspaper(Newspaper Newspaper);

    public Newspaper getNewspaper(Integer newspaperId);
}
