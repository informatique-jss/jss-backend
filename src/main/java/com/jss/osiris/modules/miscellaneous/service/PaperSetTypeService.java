package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;

import com.jss.osiris.modules.miscellaneous.model.PaperSetType;

public interface PaperSetTypeService {
    public List<PaperSetType> getPaperSetTypes();

    public PaperSetType getPaperSetType(Integer id);
	
	 public PaperSetType addOrUpdatePaperSetType(PaperSetType paperSetType);
}
