import { Debour } from './Debour';
export interface BankTransfert {
  id: number;
  label: string;
  transfertAmount: number;
  transfertDateTime: Date;
  debour: Debour;
  transfertIban: string;
  transfertBic: string;
  isAlreadyExported: boolean;
}
