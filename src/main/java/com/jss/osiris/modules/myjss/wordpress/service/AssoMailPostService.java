package com.jss.osiris.modules.myjss.wordpress.service;

import java.util.List;

import com.jss.osiris.modules.myjss.wordpress.model.AssoMailPost;
import com.jss.osiris.modules.myjss.wordpress.model.Post;
import com.jss.osiris.modules.myjss.wordpress.model.ReadingFolder;
import com.jss.osiris.modules.osiris.miscellaneous.model.Mail;

public interface AssoMailPostService {

    public AssoMailPost addOrUpdateAssoMailPost(AssoMailPost assoMailPost);

    public AssoMailPost getAssoMailPostByMailAndPost(Mail mail, Post post);

    public List<AssoMailPost> getAssoMailPostsByMail(Mail mail);

    public void deleteAssoMailPost(AssoMailPost assoMailPost);

    public List<AssoMailPost> getAssoMailPostsByReadingFolder(ReadingFolder readingFolder);
}
