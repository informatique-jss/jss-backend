import { Affaire } from "./Affaire";
import { Bodacc } from "./Bodacc";
import { CustomerOrder } from "./CustomerOrder";
import { Domiciliation } from "./Domiciliation";
import { InvoiceItem } from "./InvoiceItem";
import { ProvisionFamilyType } from "./ProvisionFamilyType";
import { ProvisionType } from "./ProvisionType";
import { Quotation } from "./Quotation";
import { Shal } from "./Shal";

export interface Provision {
  id: number;
  affaire: Affaire;
  domiciliation: Domiciliation | undefined;
  shal: Shal | undefined;
  bodacc: Bodacc | undefined;
  provisionFamilyType: ProvisionFamilyType;
  provisionType: ProvisionType;
  quotation: Quotation;
  customerOrder: CustomerOrder;
  isValidated: boolean;
  invoiceItems: InvoiceItem[]
}
