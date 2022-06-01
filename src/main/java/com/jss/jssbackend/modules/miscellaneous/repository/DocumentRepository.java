package com.jss.jssbackend.modules.miscellaneous.repository;

import com.jss.jssbackend.modules.miscellaneous.model.Document;

import org.springframework.data.repository.CrudRepository;

public interface DocumentRepository extends CrudRepository<Document, Integer> {
}