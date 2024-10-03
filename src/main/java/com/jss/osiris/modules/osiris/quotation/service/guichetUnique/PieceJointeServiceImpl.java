package com.jss.osiris.modules.osiris.quotation.service.guichetUnique;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.PiecesJointe;
import com.jss.osiris.modules.osiris.quotation.repository.guichetUnique.PieceJointeRepository;

@Service
public class PieceJointeServiceImpl implements PieceJointeService {

    PieceJointeRepository pieceJointeRepository;

    @Override
    public List<PiecesJointe> getPieceJointes() {
        return IterableUtils.toList(pieceJointeRepository.findAll());
    }

    @Override
    public PiecesJointe getPieceJointe(Integer id) {
        Optional<PiecesJointe> pieceJointe = pieceJointeRepository.findByAttachmentId(id + "");
        if (pieceJointe.isPresent())
            return pieceJointe.get();
        return null;
    }

    @Override
    public PiecesJointe addOrUpdatePieceJointe(PiecesJointe piecesJointe) {
        return pieceJointeRepository.save(piecesJointe);
    }
}
