package com.jss.osiris.libs.batch.service.threads.indexation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.modules.myjss.wordpress.service.PostService;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;

@Service
public class IndexPostThread extends IndexThread {

    @Autowired
    PostService postService;

    @Override
    public String getBatchCode() {
        return Batch.REINDEX_POST;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public IId getEntity(Integer entityId) {
        return postService.getPost(entityId);
    }

}
