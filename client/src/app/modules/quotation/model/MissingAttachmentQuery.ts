import { Attachment } from '../../miscellaneous/model/Attachment';
import { IAttachment } from '../../miscellaneous/model/IAttachment';
import { AssoServiceDocument } from './AssoServiceDocument';
import { AssoServiceFieldType } from './AssoServiceFieldType';
export interface MissingAttachmentQuery extends IAttachment {
  id: number | undefined;
  assoServiceDocument: AssoServiceDocument[];
  assoServiceFieldType: AssoServiceFieldType[];
  attachments: Attachment[];
  comment: string;
  sendToMe: boolean;
  copyToMe: boolean;
  createdDateTime: Date | undefined;
  firstCustomerReminderDateTime: Date | undefined;
  secondCustomerReminderDateTime: Date | undefined;
  thirdCustomerReminderDateTime: Date | undefined;
}
