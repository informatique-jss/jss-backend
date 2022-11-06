import { DocumentExtension } from "./referentials/DocumentExtension";
import { TaciteReconduction } from "./referentials/TaciteReconduction";
import { TypeDocument } from "./referentials/TypeDocument";

export interface PiecesJointe {
  nomDocument: string;
  typeDocument: TypeDocument;
  langueDocument: string; // TODO : référentiel ?
  numeroPiece: string;
  debutValidite: Date;
  finValidite: Date;
  taciteReconduction: TaciteReconduction;
  autoriteDelivrance: string; // TODO : référentiel ?
  paysLieuDelivrance: string;
  communeLieuDelivrance: string;
  observations: string;
  documentBase64: string;
  documentExtension: DocumentExtension;
  sousTypeDocument: string;// TODO : référentiel ?
  path: string;
  attachmentId: number;
  codeInseeCommuneLieuDelivrance: string;
  codePostalLieuDelivrance: string;
}

