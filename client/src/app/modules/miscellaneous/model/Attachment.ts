import { AzureInvoice } from "../../invoicing/model/AzureInvoice";
import { AzureReceipt } from "../../invoicing/model/AzureReceipt";
import { Invoice } from "../../quotation/model/Invoice";
import { Provision } from "../../quotation/model/Provision";
import { TypeDocument } from "../../quotation/model/guichet-unique/referentials/TypeDocument";
import { AttachmentType } from "./AttachmentType";
import { UploadedFile } from "./UploadedFile";


export interface Attachment {
  id: number;
  attachmentType: AttachmentType;
  uploadedFile: UploadedFile;
  isDisabled: boolean;
  azureReceipt: AzureReceipt;
  azureInvoice: AzureInvoice;
  provision: Provision;
  invoice: Invoice;
  isAlreadySent: boolean;
  typeDocument: TypeDocument;
  creatDateTime: Date;
}

