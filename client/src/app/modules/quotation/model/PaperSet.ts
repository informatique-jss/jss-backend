import { CompetentAuthority } from "../../miscellaneous/model/CompetentAuthority";
import { PaperSetType } from "../../miscellaneous/model/PaperSetType";
import { CustomerOrder } from "./CustomerOrder";

export interface PaperSet {
  id: number;
  paperSetType: PaperSetType;
  customerOrder: CustomerOrder;
  competentAuthority: CompetentAuthority;
  locationNumber: number;
  isCancelled: boolean;
  isValidated: boolean;
  creationComment: string;
  validationComment: string;
}
