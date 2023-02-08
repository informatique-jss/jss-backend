package com.jss.osiris.modules.quotation.service;

import java.util.ArrayList;
import java.util.List;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.profile.model.Employee;
import com.jss.osiris.modules.quotation.model.AffaireSearch;
import com.jss.osiris.modules.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.quotation.model.AssoAffaireOrderSearchResult;
import com.jss.osiris.modules.quotation.model.IQuotation;

public interface AssoAffaireOrderService {
        public List<AssoAffaireOrder> getAssoAffaireOrders();

        public AssoAffaireOrder getAssoAffaireOrder(Integer id);

        public AssoAffaireOrder addOrUpdateAssoAffaireOrder(AssoAffaireOrder assoAffaireOrder)
                        throws OsirisException, OsirisClientMessageException;

        public AssoAffaireOrder addOrUpdateAssoAffaireOrderFromUser(AssoAffaireOrder assoAffaireOrder)
                        throws OsirisException, OsirisClientMessageException;

        public void updateAssignedToForAsso(AssoAffaireOrder asso, Employee employee);

        public ArrayList<AssoAffaireOrderSearchResult> searchForAsso(AffaireSearch affaireSearch);

        public void reindexAffaires();

        public AssoAffaireOrder completeAssoAffaireOrder(AssoAffaireOrder assoAffaireOrder, IQuotation customerOrder)
                        throws OsirisException, OsirisClientMessageException;

}
