import { IReferential } from "../../administration/model/IReferential";
import { City } from "../../miscellaneous/model/City";
import { Country } from "../../miscellaneous/model/Country";

export interface BuildingDomiciliation extends IReferential {
  address: string;
  postalCode: string;
  city: City;
  country: Country;
}
