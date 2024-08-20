import { DocumentAssocieInfogreffe } from "./DocumentAssocieInfogreffe";
import { FormaliteInfogreffe } from "./FormaliteInfogreffe";

export interface EvenementInfogreffe {
  id: number;
  date: Date;
  codeEtat: string;
  documentsAssocies: DocumentAssocieInfogreffe[];
  evtCommentaire: null;
  formaliteInfogreffe: FormaliteInfogreffe;
}
