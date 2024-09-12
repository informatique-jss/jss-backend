import { EvenementInfogreffe } from "./EvenementInfogreffe";

export interface DocumentAssocieInfogreffe {
  urlTelechargement: string;
  typeDocument: string;
  evenementInfogreffe: EvenementInfogreffe;
  attachments: [];
}
