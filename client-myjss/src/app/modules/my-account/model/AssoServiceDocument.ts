import { TypeDocument } from "./TypeDocument";

export interface AssoServiceDocument {
  id: number;
  typeDocument: TypeDocument;
  isMandatory: boolean;
  formalisteComment: string;
}
