import { Employee } from '../../profile/model/Employee';
import { Confrere } from '../../quotation/model/Confrere';
import { MailComputeResult } from '../../quotation/model/MailComputeResult';
import { Quotation } from '../../quotation/model/Quotation';
import { Responsable } from '../../tiers/model/Responsable';
import { Tiers } from '../../tiers/model/Tiers';
import { Attachment } from './Attachment';
export interface CustomerMail {
  id: number;
  headerPicture: string;
  title: string;
  subtitle: string;
  label: string;
  labelSubtitle: string;
  explaination: string;
  explainationElements: string;
  explaination2: string;
  explaination3: string;
  paymentExplaination: string;
  paymentExplaination2: string;
  cbExplanation: string;
  cbLink: string;
  paymentExplainationWarning: string;
  preTaxPriceTotal: number;
  discountTotal: number;
  priceTotal: number;
  preTaxPriceTotalWithDicount: number;
  totalSubtitle: string;
  greetings: string;
  sendToMe: boolean;
  mailComputeResult: MailComputeResult;
  replyTo: Employee;
  replyToMail: string;
  sendToMeEmployee: Employee;
  subject: string;
  createdDateTime: Date;
  quotation: Quotation;
  tiers: Tiers;
  responsable: Responsable;
  confrere: Confrere;
  attachments: Attachment[];
  isSent: boolean;
  isCancelled: boolean;
  toSendAfter: Date;
}
