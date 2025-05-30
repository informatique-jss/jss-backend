package com.jss.osiris.modules.myjss.wordpress.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.myjss.wordpress.model.AssoMailAuthor;
import com.jss.osiris.modules.myjss.wordpress.model.Author;
import com.jss.osiris.modules.osiris.miscellaneous.model.Mail;

public interface AssoMailAuthorService {

    public AssoMailAuthor addNewAuthorFollow(Author author) throws OsirisException;

    public Boolean getIsAssoMailAuthorByMailAndAuthor(Author author);

    public AssoMailAuthor getAssoMailAuthorByMailAndAuthor(Author author);

    public AssoMailAuthor updateAuthorConsultationDate(Mail mail, Author author);

    public List<AssoMailAuthor> getAssoMailAuthorByMail();

    public AssoMailAuthor getAssoMailAuthor(Integer id);

    public void deleteAssoMailAuthor(Author author) throws OsirisValidationException;

    public AssoMailAuthor addOrUpdateAssoMailAuthor(AssoMailAuthor assoMailAuthor);
}
