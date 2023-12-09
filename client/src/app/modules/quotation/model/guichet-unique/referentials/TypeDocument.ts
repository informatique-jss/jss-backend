import { AttachmentType } from 'src/app/modules/miscellaneous/model/AttachmentType';
import { IReferential } from '../../../../administration/model/IReferential';
export interface TypeDocument extends IReferential {
  isToDownloadOnProvision: boolean;
  attachmentType: AttachmentType;
}
