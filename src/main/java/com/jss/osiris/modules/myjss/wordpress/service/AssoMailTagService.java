package com.jss.osiris.modules.myjss.wordpress.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.myjss.wordpress.model.AssoMailTag;
import com.jss.osiris.modules.myjss.wordpress.model.Tag;
import com.jss.osiris.modules.osiris.miscellaneous.model.Mail;

public interface AssoMailTagService {
    public AssoMailTag addNewTagFollow(Tag tag) throws OsirisException;

    public AssoMailTag getAssoMailTagByMailAndTag(Tag tag);

    public Boolean getIsAssoMailTagByMailAndTag(Tag tag);

    public AssoMailTag updateTagConsultationDate(Mail mail, Tag tag);

    public List<AssoMailTag> getAssoMailTagForCurrentUser();

    public AssoMailTag getAssoMailTag(Integer id);

    public void deleteAssoMailTag(Tag tag) throws OsirisValidationException;

    public AssoMailTag addOrUpdateAssoMailTag(AssoMailTag assoMailTag);
}
