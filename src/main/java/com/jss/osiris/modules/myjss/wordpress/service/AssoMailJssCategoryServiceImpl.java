package com.jss.osiris.modules.myjss.wordpress.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.myjss.wordpress.model.AssoMailJssCategory;
import com.jss.osiris.modules.myjss.wordpress.model.JssCategory;
import com.jss.osiris.modules.myjss.wordpress.repository.AssoMailJssCategoryRepository;
import com.jss.osiris.modules.osiris.miscellaneous.model.Mail;
import com.jss.osiris.modules.osiris.profile.service.EmployeeService;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

@Service
public class AssoMailJssCategoryServiceImpl implements AssoMailJssCategoryService {

    @Autowired
    AssoMailJssCategoryRepository assoMailJssCategoryRepository;

    @Autowired
    EmployeeService employeeService;

    public AssoMailJssCategory addNewJssCategoryFollow(JssCategory jssCategory) throws OsirisException {

        Responsable responsable = employeeService.getCurrentMyJssUser();
        AssoMailJssCategory asso = new AssoMailJssCategory();

        if (responsable == null)
            throw new OsirisException("unknown user");

        if (responsable.getMail() != null) {
            asso.setMail(responsable.getMail());
            asso.setJssCategory(jssCategory);
            asso.setLastConsultationDate(LocalDateTime.now());
        }
        return addOrUpdateAssoMailJssCategory(asso);
    }

    @Override
    public AssoMailJssCategory getAssoMailJssCategoryByMailAndJssCategory(JssCategory jssCategory) {
        return assoMailJssCategoryRepository.findByMailAndJssCategory(employeeService.getCurrentMyJssUser().getMail(),
                jssCategory);
    }

    @Override
    public Boolean getIsAssoMailJssCategoryByMailAndJssCategory(JssCategory jssCategory) {
        Responsable responsable = employeeService.getCurrentMyJssUser();
        AssoMailJssCategory assoMailJssCategory = null;

        if (responsable != null)
            assoMailJssCategory = assoMailJssCategoryRepository.findByMailAndJssCategory(responsable.getMail(),
                    jssCategory);
        if (assoMailJssCategory != null)
            return true;
        return false;
    }

    @Override
    public AssoMailJssCategory updateJssCategoryConsultationDate(Mail mail, JssCategory jssCategory) {
        AssoMailJssCategory AssoMailJssCategory = assoMailJssCategoryRepository.findByMailAndJssCategory(mail,
                jssCategory);
        if (AssoMailJssCategory != null)
            AssoMailJssCategory.setLastConsultationDate(LocalDateTime.now());
        return addOrUpdateAssoMailJssCategory(AssoMailJssCategory);
    }

    @Override
    public List<AssoMailJssCategory> getAssoMailJssCategoryByMail() {
        Responsable responsable = employeeService.getCurrentMyJssUser();
        return assoMailJssCategoryRepository.findByMail(responsable.getMail());
    }

    @Override
    public AssoMailJssCategory getAssoMailJssCategory(Integer id) {
        Optional<AssoMailJssCategory> AssoMailJssCategory = assoMailJssCategoryRepository.findById(id);
        if (AssoMailJssCategory.isPresent())
            return AssoMailJssCategory.get();
        return null;
    }

    @Override
    public void deleteAssoMailJssCategory(JssCategory jssCategory) throws OsirisValidationException {
        AssoMailJssCategory assoMailJssCategory = getAssoMailJssCategoryByMailAndJssCategory(jssCategory);

        if (assoMailJssCategory == null)
            throw new OsirisValidationException("assoMailJssCategory");

        assoMailJssCategoryRepository.delete(assoMailJssCategory);
    }

    @Override
    public AssoMailJssCategory addOrUpdateAssoMailJssCategory(AssoMailJssCategory assoMailJssCategory) {
        return assoMailJssCategoryRepository.save(assoMailJssCategory);
    }

}
