import { AccountingBalanceViewItem } from "./AccountingBalanceViewItem";

export interface AccountingBalanceViewTitle {
  label: string;
  subTitles: AccountingBalanceViewTitle[];
  items: AccountingBalanceViewItem[];
  soldeN: number;
  soldeN1: number;
  brutN: number;
  brutN1: number;
  amortissementN: number;
  amortissementN1: number;
  actifBilan: boolean;
  totals: AccountingBalanceViewTitle[];
}
