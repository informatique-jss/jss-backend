import { IReferential } from '../../../../administration/model/IReferential';
import { AttachmentType } from '../../../../miscellaneous/model/AttachmentType';
export interface TypeDocument extends IReferential {
  isToDownloadOnProvision: boolean;
  attachmentType: AttachmentType;
  customLabel: string;
}
