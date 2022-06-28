import { IReferential } from "../../administration/model/IReferential";
import { Department } from "../../miscellaneous/model/Department";

export interface CharacterPrice extends IReferential {
  id: number;
  price: number;
  departments: Department[];
  startDate: Date;
}

