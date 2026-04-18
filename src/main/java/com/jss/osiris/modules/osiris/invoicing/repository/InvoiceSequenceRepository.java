package com.jss.osiris.modules.osiris.invoicing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.jss.osiris.modules.osiris.invoicing.model.InvoiceSequence;

import jakarta.persistence.LockModeType;

@Repository
public interface InvoiceSequenceRepository extends JpaRepository<InvoiceSequence, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM InvoiceSequence s")
    InvoiceSequence findAndLock();
}