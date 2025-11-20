package com.jss.osiris.modules.osiris.quotation.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.RneCompany;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.RneResult;

public interface RneDelegateService {

        public List<RneCompany> getCompanyBySiren(String siren)
                        throws OsirisException, OsirisClientMessageException;

        public List<RneCompany> getCompanyBySirens(List<String> sirens)
                        throws OsirisException, OsirisClientMessageException;

        public List<RneCompany> getCompanyBySiret(String siret)
                        throws OsirisException, OsirisClientMessageException;

        public List<RneCompany> getCompanyByDenominationAndPostalCode(String denomination, String postalCode)
                        throws OsirisException, OsirisClientMessageException;

        public RneResult getCompanyModifiedForDay(LocalDate lastExecutionDate, String lastSiret, List<String> sirens)
                        throws OsirisException, OsirisClientMessageException;

        public String getRawJsonBySiret(String siret)
                        throws URISyntaxException, IOException, InterruptedException, OsirisClientMessageException,
                        OsirisException;

}
