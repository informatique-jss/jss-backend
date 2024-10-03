package com.jss.osiris.modules.osiris.miscellaneous.service;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.osiris.miscellaneous.model.SalesComplainProblem;
import com.jss.osiris.modules.osiris.miscellaneous.repository.SalesComplainProblemRepository;

@Service
public class SalesComplainProblemServiceImpl implements SalesComplainProblemService {

        @Autowired
        SalesComplainProblemRepository salesComplainProblemRepository;

        @Override
        public List<SalesComplainProblem> getComplainProblems() {
                return IterableUtils.toList(salesComplainProblemRepository.findAll());
        }

        @Override
        @Transactional(rollbackFor = Exception.class)
        public SalesComplainProblem addOrUpdateComplainProblem(SalesComplainProblem salesComplainProblem) {
                return salesComplainProblemRepository.save(salesComplainProblem);
        }
}
