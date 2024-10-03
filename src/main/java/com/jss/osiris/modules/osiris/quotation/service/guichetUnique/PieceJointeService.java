package com.jss.osiris.modules.osiris.quotation.service.guichetUnique;

import java.util.List;

import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.PiecesJointe;

public interface PieceJointeService {
    public List<PiecesJointe> getPieceJointes();

    public PiecesJointe getPieceJointe(Integer id);

    public PiecesJointe addOrUpdatePieceJointe(PiecesJointe piecesJointe);
}
