import { IReferential } from "../../administration/model/IReferential";
import { Country } from "./Country";
import { Department } from "./Department";

export interface City extends IReferential {
  postalCode: string;
  department: Department;
  country: Country;
}

