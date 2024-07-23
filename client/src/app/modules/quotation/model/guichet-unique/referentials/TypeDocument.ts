import { AttachmentType } from 'src/app/modules/miscellaneous/model/AttachmentType';
import { IReferential } from '../../../../administration/model/IReferential';
import { IAttachment } from 'src/app/modules/miscellaneous/model/IAttachment';
export interface TypeDocument extends IReferential, IAttachment {
  isToDownloadOnProvision: boolean;
  attachmentType: AttachmentType;
}
