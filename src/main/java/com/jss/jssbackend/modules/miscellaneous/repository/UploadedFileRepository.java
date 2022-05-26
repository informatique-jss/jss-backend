package com.jss.jssbackend.modules.miscellaneous.repository;

import com.jss.jssbackend.modules.miscellaneous.model.UploadedFile;

import org.springframework.data.repository.CrudRepository;

public interface UploadedFileRepository extends CrudRepository<UploadedFile, Integer> {
}