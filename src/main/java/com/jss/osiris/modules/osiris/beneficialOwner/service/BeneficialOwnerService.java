package com.jss.osiris.modules.osiris.beneficialOwner.service;

import java.util.List;

import com.jss.osiris.modules.osiris.beneficialOwner.model.BeneficialOwner;
import com.jss.osiris.modules.osiris.quotation.model.Formalite;

public interface BeneficialOwnerService {

    public BeneficialOwner addOrUpdateBeneficialOwner(BeneficialOwner beneficialOwner);

    public BeneficialOwner getBeneficialOwner(Integer id);

    public List<BeneficialOwner> getBeneficialOwnersByFormalite(Formalite formalite);

    public Boolean deleteBeneficialOwner(BeneficialOwner beneficialOwner);
}
