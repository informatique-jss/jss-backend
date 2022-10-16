package com.jss.osiris.modules.quotation.service;

import java.util.List;

import com.jss.osiris.modules.quotation.model.AffaireStatus;

public interface AffaireStatusService {
    public List<AffaireStatus> getAffaireStatus();

    public AffaireStatus getAffaireStatus(Integer id);
	
	 public AffaireStatus addOrUpdateAffaireStatus(AffaireStatus affaireStatus);
}
