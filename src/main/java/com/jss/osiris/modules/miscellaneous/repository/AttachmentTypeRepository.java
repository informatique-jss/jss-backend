package com.jss.osiris.modules.miscellaneous.repository;

import org.springframework.data.repository.CrudRepository;

import com.jss.osiris.modules.miscellaneous.model.AttachmentType;

public interface AttachmentTypeRepository extends CrudRepository<AttachmentType, Integer> {
}