package com.jss.osiris.libs.mail;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.mail.model.IndexationMail;

public interface IndexationMailService {

    public boolean attachIndexationMailToEntity(IndexationMail mail) throws OsirisException;
}
