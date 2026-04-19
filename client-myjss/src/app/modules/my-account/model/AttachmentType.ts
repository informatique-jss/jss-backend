import { IAttachment } from "./IAttachment";


export interface AttachmentType extends IAttachment {
  id: number;
  label: string;
  code: string;
  description: string;
  isDocumentDateRequired: boolean;
}
