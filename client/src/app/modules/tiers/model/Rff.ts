import { Invoice } from "../../quotation/model/Invoice";

export interface Rff {
  id: number;
  tiersLabel: string;
  tiersId: number;
  responsableLabel: string;
  responsableId: number;
  rffInsertion: number;
  rffFormalite: number;
  startDate: Date;
  endDate: Date;
  isCancelled: boolean;
  isSent: boolean;
  invoices: Invoice[];
}


