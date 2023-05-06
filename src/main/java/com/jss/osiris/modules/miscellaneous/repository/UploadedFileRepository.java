package com.jss.osiris.modules.miscellaneous.repository;

import com.jss.osiris.libs.QueryCacheCrudRepository;

import com.jss.osiris.modules.miscellaneous.model.UploadedFile;

public interface UploadedFileRepository extends QueryCacheCrudRepository<UploadedFile, Integer> {
}