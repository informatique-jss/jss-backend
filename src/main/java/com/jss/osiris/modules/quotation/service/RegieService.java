package com.jss.osiris.modules.quotation.service;

import java.util.List;

import com.jss.osiris.modules.quotation.model.Regie;

public interface RegieService {
    public List<Regie> getRegies();

    public Regie getRegie(Integer id);
	
	 public Regie addOrUpdateRegie(Regie regie);
}
