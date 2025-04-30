import { AssoAffaireOrder } from "../../my-account/model/AssoAffaireOrder";
import { ServiceFamilyGroup } from "./ServiceFamilyGroup";

export interface IQuotation {
  id: number;
  assoAffaireOrders: AssoAffaireOrder[];
  serviceFamilyGroup: ServiceFamilyGroup;
  isQuotation: boolean;
}
