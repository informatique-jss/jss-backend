import { AssoAffaireOrder } from "../../my-account/model/AssoAffaireOrder";
import { Responsable } from "../../profile/model/Responsable";
import { ServiceFamilyGroup } from "./ServiceFamilyGroup";

export interface IQuotation {
  id: number;
  assoAffaireOrders: AssoAffaireOrder[];
  serviceFamilyGroup: ServiceFamilyGroup;
  isQuotation: boolean;
  responsable: Responsable | undefined;
}
