package com.jss.osiris.modules.quotation.repository;

import org.springframework.data.repository.CrudRepository;

import com.jss.osiris.modules.quotation.model.Announcement;

public interface AnnouncementRepository extends CrudRepository<Announcement, Integer> {
}