import { Attachment } from '../../miscellaneous/model/Attachment';
import { AssoServiceDocument } from './AssoServiceDocument';
import { AssoServiceFieldType } from './AssoServiceFieldType';
export interface MissingAttachmentQuery {
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
