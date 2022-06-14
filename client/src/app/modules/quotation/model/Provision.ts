import { Affaire } from "./Affaire";
import { CustomerOrder } from "./CustomerOrder";
import { Domiciliation } from "./Domiciliation";
import { ProvisionFamilyType } from "./ProvisionFamilyType";
import { ProvisionType } from "./ProvisionType";
import { Quotation } from "./Quotation";
import { Shal } from "./Shal";

export interface Provision {
  id: number;
  affaire: Affaire;
  quotation: Quotation;
  customerOrder: CustomerOrder;
  domiciliation: Domiciliation | undefined;
  shal: Shal | undefined;
  provisionFamilyType: ProvisionFamilyType;
  provisionType: ProvisionType;
}
