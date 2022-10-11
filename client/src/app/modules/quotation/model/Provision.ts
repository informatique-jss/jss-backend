import { Affaire } from "./Affaire";
import { Announcement } from "./Announcement";
import { Bodacc } from "./Bodacc";
import { CustomerOrder } from "./CustomerOrder";
import { Domiciliation } from "./Domiciliation";
import { InvoiceItem } from "./InvoiceItem";
import { ProvisionFamilyType } from "./ProvisionFamilyType";
import { ProvisionType } from "./ProvisionType";
import { Quotation } from "./Quotation";

export interface Provision {
  id: number;
  affaire: Affaire;
  domiciliation: Domiciliation | undefined;
  announcement: Announcement | undefined;
  bodacc: Bodacc | undefined;
  provisionFamilyType: ProvisionFamilyType;
  provisionType: ProvisionType;
  quotation: Quotation;
  customerOrder: CustomerOrder;
  isValidated: boolean;
  invoiceItems: InvoiceItem[]
}
