package com.jss.osiris.modules.quotation.service.guichetUnique;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.quotation.model.Formalite;
import com.jss.osiris.modules.quotation.model.guichetUnique.FormaliteGuichetUnique;

public interface FormaliteGuichetUniqueService {

    public FormaliteGuichetUnique getFormaliteGuichetUnique(Integer id);

    public FormaliteGuichetUnique refreshFormaliteGuichetUnique(FormaliteGuichetUnique formaliteGuichetUnique,
            Formalite formalite, boolean generateInvoices)
            throws OsirisValidationException, OsirisException, OsirisClientMessageException, OsirisDuplicateException;

    public FormaliteGuichetUnique addOrUpdateFormaliteGuichetUnique(FormaliteGuichetUnique formaliteGuichetUnique);

    public List<FormaliteGuichetUnique> getFormaliteGuichetUniqueToSign();

    public List<FormaliteGuichetUnique> getFormaliteGuichetUniqueByLiasseNumber(String value);
}
