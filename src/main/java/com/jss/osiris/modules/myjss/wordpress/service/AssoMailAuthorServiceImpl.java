package com.jss.osiris.modules.myjss.wordpress.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.myjss.wordpress.model.AssoMailAuthor;
import com.jss.osiris.modules.myjss.wordpress.model.Author;
import com.jss.osiris.modules.myjss.wordpress.repository.AssoMailAuthorRepository;
import com.jss.osiris.modules.osiris.miscellaneous.model.Mail;
import com.jss.osiris.modules.osiris.profile.service.EmployeeService;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

@Service
public class AssoMailAuthorServiceImpl implements AssoMailAuthorService {

    @Autowired
    AssoMailAuthorRepository assoMailAuthorRepository;

    @Autowired
    EmployeeService employeeService;

    @Override
    public AssoMailAuthor addNewAuthorFollow(Author author) throws OsirisException {

        Responsable responsable = employeeService.getCurrentMyJssUser();
        AssoMailAuthor asso = new AssoMailAuthor();

        if (responsable == null)
            throw new OsirisException("unknown user");

        if (responsable.getMail() != null) {
            asso.setMail(responsable.getMail());
            asso.setAuthor(author);
            asso.setLastConsultationDate(LocalDateTime.now());
        }
        return addOrUpdateAssoMailAuthor(asso);
    }

    @Override
    public AssoMailAuthor getAssoMailAuthor(Integer id) {
        Optional<AssoMailAuthor> assoMailAuthor = assoMailAuthorRepository.findById(id);
        if (assoMailAuthor.isPresent())
            return assoMailAuthor.get();
        return null;
    }

    @Override
    public AssoMailAuthor updateAuthorConsultationDate(Mail mail, Author author) {
        AssoMailAuthor assoMailAuthor = assoMailAuthorRepository.findByMailAndAuthor(mail, author);
        if (assoMailAuthor != null) {
            assoMailAuthor.setLastConsultationDate(LocalDateTime.now());
            addOrUpdateAssoMailAuthor(assoMailAuthor);
        }
        return assoMailAuthor;
    }

    @Override
    public List<AssoMailAuthor> getAssoMailAuthorByMail() {
        Responsable responsable = employeeService.getCurrentMyJssUser();
        return assoMailAuthorRepository.findByMail(responsable.getMail());
    }

    @Override
    public void deleteAssoMailAuthor(Author author) throws OsirisValidationException {
        AssoMailAuthor assoMailAuthor = getAssoMailAuthorByMailAndAuthor(author);

        if (assoMailAuthor == null)
            throw new OsirisValidationException("assoMailAuthor");

        assoMailAuthorRepository.delete(assoMailAuthor);
    }

    @Override
    public Boolean getIsAssoMailAuthorByMailAndAuthor(Author author) {
        Responsable responsable = employeeService.getCurrentMyJssUser();
        AssoMailAuthor assoMailAuthor = null;

        if (responsable != null)
            assoMailAuthor = assoMailAuthorRepository.findByMailAndAuthor(responsable.getMail(), author);
        if (assoMailAuthor != null)
            return true;
        return false;
    }

    @Override
    public AssoMailAuthor getAssoMailAuthorByMailAndAuthor(Author author) {
        return assoMailAuthorRepository.findByMailAndAuthor(employeeService.getCurrentMyJssUser().getMail(), author);
    }

    @Override
    public AssoMailAuthor addOrUpdateAssoMailAuthor(AssoMailAuthor assoMailAuthor) {
        return assoMailAuthorRepository.save(assoMailAuthor);
    }
}
