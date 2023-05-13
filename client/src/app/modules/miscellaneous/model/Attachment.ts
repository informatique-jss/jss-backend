import { AzureReceipt } from "../../invoicing/model/AzureReceipt";
import { Provision } from "../../quotation/model/Provision";
import { AttachmentType } from "./AttachmentType";
import { UploadedFile } from "./UploadedFile";


export interface Attachment {
  id: number;
  attachmentType: AttachmentType;
  uploadedFile: UploadedFile;
  isDisabled: boolean;
  azureReceipt: AzureReceipt;
  provision: Provision;
}

