package com.jss.osiris.modules.quotation.service;

import com.jss.osiris.modules.quotation.model.Debour;

public interface DebourService {
    public Debour getDebour(Integer id);

    public Debour addOrUpdateDebour(Debour debour);
}
