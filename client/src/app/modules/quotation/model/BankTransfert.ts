import { Debour } from './Debour';
import { Invoice } from './Invoice';
export interface BankTransfert {
  id: number;
  label: string;
  transfertAmount: number;
  transfertDateTime: Date;
  transfertIban: string;
  transfertBic: string;
  isAlreadyExported: boolean;
  debours: Debour[];
  invoices: Invoice[];
}
