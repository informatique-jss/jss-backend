import { IReferential } from "../../my-account/model/IReferential";
import { City } from "../../profile/model/City";
import { Country } from "../../profile/model/Country";

export interface BuildingDomiciliation extends IReferential {
  address: string;
  postalCode: string;
  cedexComplement: string;
  city: City;
  country: Country;
  isDisabled: boolean;
}
