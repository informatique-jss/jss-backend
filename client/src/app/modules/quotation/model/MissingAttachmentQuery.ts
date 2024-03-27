import { AssoServiceDocument } from './AssoServiceDocument';
export interface MissingAttachmentQuery {
  id: number | undefined;
  assoServiceDocument: AssoServiceDocument[];
  comment: string;
  sendToMe: boolean;
  copyToMe: boolean;
  createdDateTime: Date | undefined;
  firstCustomerReminderDateTime: Date | undefined;
  secondCustomerReminderDateTime: Date | undefined;
  thirdCustomerReminderDateTime: Date | undefined;
}
