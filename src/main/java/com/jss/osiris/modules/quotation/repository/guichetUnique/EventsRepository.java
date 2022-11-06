package com.jss.osiris.modules.quotation.repository.guichetUnique;

import org.springframework.data.repository.CrudRepository;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.Events;

public interface EventsRepository extends CrudRepository<Events, String> {
}
