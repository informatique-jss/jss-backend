package com.jss.osiris.modules.myjss.quotation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.ValidationHelper;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.myjss.profile.service.UserScopeService;
import com.jss.osiris.modules.osiris.profile.service.EmployeeService;
import com.jss.osiris.modules.osiris.quotation.controller.QuotationValidationHelper;
import com.jss.osiris.modules.osiris.quotation.model.Affaire;
import com.jss.osiris.modules.osiris.quotation.model.IQuotation;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

@Service
public class MyJssQuotationValidationHelper {

        @Autowired
        ValidationHelper validationHelper;

        @Autowired
        UserScopeService userScopeService;

        @Autowired
        EmployeeService employeeService;

        @Autowired
        QuotationValidationHelper quotationValidationHelper;

        public boolean canSeeQuotation(IQuotation quotation) {
                if (quotation != null && quotation.getResponsable() != null && employeeService.getCurrentMyJssUser()
                                .getId().equals(quotation.getResponsable().getId()))
                        return true;
                return false;
        }

        public boolean canSeeResponsable(Responsable responsable) {
                if (responsable == null || responsable.getId() == null)
                        return false;

                return responsable.getId().equals(employeeService.getCurrentMyJssUser().getId());
        }

        public void validateAffaire(Affaire affaire) throws OsirisValidationException, OsirisException {
                quotationValidationHelper.validateAffaire(affaire);
        }
}
