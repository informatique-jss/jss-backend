package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.miscellaneous.model.SalesReclamationProblem;
import com.jss.osiris.modules.miscellaneous.repository.SalesReclamationProblemRepository;

@Service
public class SalesReclamationProblemServiceImpl implements SalesReclamationProblemService {

        @Autowired
        SalesReclamationProblemRepository salesReclamationProblemRepository;

        @Override
        public List<SalesReclamationProblem> getReclamationProblems() {
                return IterableUtils.toList(salesReclamationProblemRepository.findAll());
        }

        @Override
        @Transactional(rollbackFor = Exception.class)
        public SalesReclamationProblem addOrUpdateReclamationProblem(SalesReclamationProblem salesReclamationProblem) {
                return salesReclamationProblemRepository.save(salesReclamationProblem);
        }
}
