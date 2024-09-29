import { DocumentAssocieInfogreffe } from "./DocumentAssocieInfogreffe";
import { FormaliteInfogreffe } from "./FormaliteInfogreffe";

export interface EvenementInfogreffe {
  createdDate: Date;
  codeEtat: string;
  documentsAssocies: DocumentAssocieInfogreffe[];
  evtCommentaire: null;
  formaliteInfogreffe: FormaliteInfogreffe;
}
