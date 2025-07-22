import { Responsable } from '../../profile/model/Responsable';
export interface BillingClosureReceiptValue {
  eventDateTime: Date;
  eventDateString: string;
  eventDescription: string;
  eventCbLink: string;
  creditAmount: number;
  debitAmount: number;
  remainingDebitAmount: number;
  directDebitTransfertDate: Date;
  responsable: Responsable;
  affaireLists: string;
  serviceLists: string;
  idInvoice: number;
  idCustomerOrder: number;
}
