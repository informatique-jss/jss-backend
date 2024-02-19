package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;

import com.jss.osiris.modules.miscellaneous.model.SalesReclamationProblem;

public interface SalesReclamationProblemService {

        public List<SalesReclamationProblem> getReclamationProblems();

        public SalesReclamationProblem addOrUpdateReclamationProblem(SalesReclamationProblem salesReclamationProblem);
}
