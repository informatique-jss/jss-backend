package com.jss.osiris.modules.quotation.repository.guichetUnique;

import java.util.Optional;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.quotation.model.guichetUnique.PiecesJointe;

public interface PieceJointeRepository extends QueryCacheCrudRepository<PiecesJointe, String> {

    Optional<PiecesJointe> findByAttachmentId(String id);
}
