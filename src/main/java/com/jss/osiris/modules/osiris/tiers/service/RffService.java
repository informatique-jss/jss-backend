package com.jss.osiris.modules.osiris.tiers.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.osiris.invoicing.model.Invoice;
import com.jss.osiris.modules.osiris.tiers.model.Rff;
import com.jss.osiris.modules.osiris.tiers.model.RffSearch;

public interface RffService {

    public Rff getRff(Integer id);

    public List<Rff> getRffs(RffSearch rffSearch) throws OsirisException;

    public Rff addOrUpdateRff(Rff rff);

    public Rff cancelRff(Rff rff);

    public Invoice generateInvoiceForRff(Rff rff)
            throws OsirisValidationException, OsirisException, OsirisClientMessageException, OsirisDuplicateException;

    public Rff sendRff(Rff rff, Float amount, boolean sendToMe) throws OsirisException, OsirisClientMessageException;

}
