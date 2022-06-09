import { Affaire } from "./Affaire";
import { CustomerOrder } from "./CustomerOrder";
import { Domiciliation } from "./Domiciliation";
import { ProvisionType } from "./ProvisionType";
import { Quotation } from "./Quotation";
import { Shal } from "./Shal";

export interface Provision {
  id: number;
  affaire: Affaire;
  quotation: Quotation;
  customerOrder: CustomerOrder;
  provisionType: ProvisionType;
  domiciliation: Domiciliation;
  shal: Shal;
}
