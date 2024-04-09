import { ServiceFamilyGroup } from "../../quotation/model/ServiceFamilyGroup";
import { Mail } from "./Mail";

export interface AssoMailCompetentAuthorityServiceFamilyGroup {
  id: number;
  mails: Mail[];
  serviceFamilyGroup: ServiceFamilyGroup;
}

