import { IAttachment } from "./IAttachment";
import { TypeDocument } from "./TypeDocument";

export interface AssoServiceDocument extends IAttachment {
  id: number;
  typeDocument: TypeDocument;
  isMandatory: boolean;
  formalisteComment: string;
}
