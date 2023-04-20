import { CustomerOrder } from "./CustomerOrder";
import { Debour } from "./Debour";
import { Invoice } from "./Invoice";
import { OwncloudGreffeFile } from "./OwncloudGreffeFile";

export interface OwncloudGreffeInvoice {
  id: number;
  owncloudGreffeFile: OwncloudGreffeFile;
  numero: string;
  date: Date;
  customerReference: string;
  preTaxPrice: number;
  vatPrice: number;
  totalTaxedPrice: number;
  nonTaxablePrice: number;
  totalPrice: number;
  customerOrder: CustomerOrder;
  debour: Debour;
  invoice: Invoice;
}
