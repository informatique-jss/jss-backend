package com.jss.osiris.modules.myjss.quotation.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.ValidationHelper;
import com.jss.osiris.modules.myjss.profile.model.UserScope;
import com.jss.osiris.modules.myjss.profile.service.UserScopeService;
import com.jss.osiris.modules.osiris.quotation.model.IQuotation;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

@Service
public class MyJssQuotationValidationHelper {

        @Autowired
        ValidationHelper validationHelper;

        @Autowired
        UserScopeService userScopeService;

        public boolean canSeeQuotation(IQuotation quotation) {
                if (quotation != null && quotation.getResponsable() != null) {
                        List<UserScope> userScope = userScopeService.getUserScope();
                        if (userScope != null)
                                for (UserScope scope : userScope)
                                        if (scope.getResponsableViewed().getId()
                                                        .equals(quotation.getResponsable().getId()))
                                                return true;
                }
                return false;
        }

        public boolean canSeeResponsable(Responsable responsable) {
                if (responsable == null || responsable.getId() == null)
                        return false;

                List<Responsable> userScopes = userScopeService.getUserCurrentScopeResponsables();
                for (Responsable userScope : userScopes) {
                        if (userScope.getId().equals(responsable.getId()))
                                return true;
                }
                return false;
        }
}
