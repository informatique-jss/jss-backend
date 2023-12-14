import { Invoice } from "../../quotation/model/Invoice";

export interface Rff {
  id: number;
  tiersLabel: string;
  tiersId: number;
  responsableLabel: string;
  responsableId: number;
  rffInsertion: number;
  rffFormalite: number;
  rffTotal: number;
  rffMail: string;
  startDate: Date;
  endDate: Date;
  isCancelled: boolean;
  isSent: boolean;
  invoices: Invoice[];
  rffIban: string;
  rffBic: string;
}
