import { IReferential } from "../../miscellaneous/model/IReferential";
import { Country } from "./Country";

export interface City extends IReferential {
  postalCode: string;
  country: Country;
}

