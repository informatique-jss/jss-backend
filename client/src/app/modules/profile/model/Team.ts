import { IReferential } from "../../administration/model/IReferential";
import { Mail } from "../../miscellaneous/model/Mail";
import { Employee } from "./Employee";

export interface Team extends IReferential {
  manager: Employee;
  mails: Mail[];
}
