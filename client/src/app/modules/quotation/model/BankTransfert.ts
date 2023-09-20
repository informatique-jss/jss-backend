import { CustomerOrder } from './CustomerOrder';
import { Invoice } from './Invoice';
export interface BankTransfert {
  id: number;
  label: string;
  transfertAmount: number;
  transfertDateTime: Date;
  transfertIban: string;
  transfertBic: string;
  isAlreadyExported: boolean;
  isSelectedForExport: boolean;
  isCancelled: boolean;
  isMatched: boolean;
  invoices: Invoice[];
  customerOrder: CustomerOrder;
}
