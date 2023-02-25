import { AttachmentType } from '../../miscellaneous/model/AttachmentType';
import { TypeDocument } from './guichet-unique/referentials/TypeDocument';
export interface AttachmentTypeMailQuery {
  attachmentTypes: AttachmentType[];
  typeDocument: TypeDocument[];
  comment: string;
  sendToMe: boolean;
  copyToMe: boolean;
}
