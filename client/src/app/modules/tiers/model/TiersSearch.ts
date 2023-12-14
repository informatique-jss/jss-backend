import { Employee } from "../../profile/model/Employee";
import { Responsable } from "./Responsable";
import { Tiers } from "./Tiers";

export interface TiersSearch {
  tiers: Tiers;
  responsable: Responsable;
  salesEmployee: Employee;
  startDate: Date;
  endDate: Date;
  label: string;
}
