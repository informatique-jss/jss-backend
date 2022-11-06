package com.jss.osiris.modules.quotation.repository.guichetUnique;

import org.springframework.data.repository.CrudRepository;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.Status;

public interface StatusRepository extends CrudRepository<Status, String> {
}
