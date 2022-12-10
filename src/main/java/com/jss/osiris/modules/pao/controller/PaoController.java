package com.jss.osiris.modules.pao.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jss.osiris.libs.ValidationHelper;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.pao.model.Journal;
import com.jss.osiris.modules.pao.service.JournalService;

@RestController
public class PaoController {

    private static final String inputEntryPoint = "/pao";

    @Autowired
    ValidationHelper validationHelper;

    @Autowired
    JournalService journalService;

    @GetMapping(inputEntryPoint + "/journals")
    public ResponseEntity<List<Journal>> getJournals() {
        return new ResponseEntity<List<Journal>>(journalService.getJournals(), HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/journal")
    public ResponseEntity<Journal> getJournal(@RequestParam Integer id) {
        return new ResponseEntity<Journal>(journalService.getJournal(id), HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/journal")
    public ResponseEntity<Journal> addOrUpdateJournal(
            @RequestBody Journal journal) throws OsirisValidationException, OsirisException {
        if (journal.getId() != null)
            validationHelper.validateReferential(journal, true, "journals");
        validationHelper.validateString(journal.getLabel(), true, "label");
        validationHelper.validateDate(journal.getJournalDate(), true, "journalDate");

        return new ResponseEntity<Journal>(journalService.addOrUpdateJournal(journal), HttpStatus.OK);
    }

}