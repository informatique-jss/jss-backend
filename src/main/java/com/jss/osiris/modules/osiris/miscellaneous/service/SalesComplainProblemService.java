package com.jss.osiris.modules.osiris.miscellaneous.service;

import java.util.List;

import com.jss.osiris.modules.osiris.miscellaneous.model.SalesComplainProblem;

public interface SalesComplainProblemService {

        public List<SalesComplainProblem> getComplainProblems();

        public SalesComplainProblem addOrUpdateComplainProblem(SalesComplainProblem salesComplainProblem);
}
