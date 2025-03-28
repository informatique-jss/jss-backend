import { AssoAffaireOrder } from "../../my-account/model/AssoAffaireOrder";

export interface IQuotation {
  id: number;
  assoAffaireOrders: AssoAffaireOrder[];
}
