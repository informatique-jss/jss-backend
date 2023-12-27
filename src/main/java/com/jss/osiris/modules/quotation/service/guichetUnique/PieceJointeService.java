package com.jss.osiris.modules.quotation.service.guichetUnique;

import java.util.List;

import com.jss.osiris.modules.quotation.model.guichetUnique.PiecesJointe;

public interface PieceJointeService {
    public List<PiecesJointe> getPieceJointes();

    public PiecesJointe getPieceJointe(Integer id);

    public PiecesJointe addOrUpdatePieceJointe(PiecesJointe piecesJointe);
}
