import { AssoAffaireOrder } from "../../my-account/model/AssoAffaireOrder";
import { Voucher } from "../../my-account/model/Voucher";
import { Responsable } from "../../profile/model/Responsable";
import { IDocument } from "./IDocument";
import { ServiceFamilyGroup } from "./ServiceFamilyGroup";

export interface IQuotation extends IDocument {
  id: number;
  assoAffaireOrders: AssoAffaireOrder[];
  serviceFamilyGroup: ServiceFamilyGroup | undefined;
  isQuotation: boolean;
  responsable: Responsable | undefined;
  description: string;
  voucher: Voucher;
}
