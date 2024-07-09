import { PaperSetType } from "../../miscellaneous/model/PaperSetType";
import { CustomerOrder } from "./CustomerOrder";

export interface PaperSet {
  id: number;
  paperSetType: PaperSetType;
  customerOrder: CustomerOrder;
  locationNumber: number;
  isCancelled: boolean;
  isDone: boolean;
}
