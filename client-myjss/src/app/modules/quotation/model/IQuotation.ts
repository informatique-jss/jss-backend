import { AssoAffaireOrder } from "../../my-account/model/AssoAffaireOrder";
import { Responsable } from "../../profile/model/Responsable";
import { IDocument } from "./IDocument";
import { ServiceFamilyGroup } from "./ServiceFamilyGroup";

export interface IQuotation extends IDocument {
  id: number;
  assoAffaireOrders: AssoAffaireOrder[];
  serviceFamilyGroup: ServiceFamilyGroup;
  isQuotation: boolean;
  responsable: Responsable | undefined;
}
