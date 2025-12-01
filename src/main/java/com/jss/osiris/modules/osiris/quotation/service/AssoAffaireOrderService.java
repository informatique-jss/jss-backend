package com.jss.osiris.modules.osiris.quotation.service;

import java.util.ArrayList;
import java.util.List;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.osiris.quotation.model.Affaire;
import com.jss.osiris.modules.osiris.quotation.model.AffaireSearch;
import com.jss.osiris.modules.osiris.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.osiris.quotation.model.AssoAffaireOrderSearchResult;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.IQuotation;
import com.jss.osiris.modules.osiris.quotation.model.Quotation;

public interface AssoAffaireOrderService {
        public List<AssoAffaireOrder> getAssoAffaireOrders();

        public AssoAffaireOrder getAssoAffaireOrder(Integer id);

        public AssoAffaireOrder addOrUpdateAssoAffaireOrder(AssoAffaireOrder assoAffaireOrder)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public AssoAffaireOrder addOrUpdateAssoAffaireOrderFromUser(AssoAffaireOrder assoAffaireOrder)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public ArrayList<AssoAffaireOrderSearchResult> searchForAsso(AffaireSearch affaireSearch);

        public void reindexAffaires() throws OsirisException;

        public AssoAffaireOrder completeAssoAffaireOrder(AssoAffaireOrder assoAffaireOrder, IQuotation customerOrder,
                        Boolean isFromUser)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public List<AssoAffaireOrder> getAssoAffaireOrderForCustomerOrder(CustomerOrder customerOrder)
                        throws OsirisException;

        public List<AssoAffaireOrder> getAssoAffaireOrderForQuotation(Quotation quotation) throws OsirisException;

        public List<AssoAffaireOrder> populateTransientField(List<AssoAffaireOrder> assoAffaireOrders)
                        throws OsirisException;

        List<AssoAffaireOrder> getAssoAffaireOrderByAffaire(Affaire affaire);
}
