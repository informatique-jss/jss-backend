import { IAttachment } from '../../miscellaneous/model/IAttachment';
import { Service } from "./Service";
import { TypeDocument } from "./guichet-unique/referentials/TypeDocument";

export interface AssoServiceDocument extends IAttachment {
  service: Service;
  typeDocument: TypeDocument;
  isMandatory: boolean;
  formalisteComment: string;
}
