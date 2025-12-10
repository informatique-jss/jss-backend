import { Responsable } from "../modules/profile/model/Responsable";
import { Tiers } from "../modules/profile/model/Tiers";

export function instanceOfTiers(object: any): object is Tiers {
  return !instanceOfResponsable(object);
}

export function instanceOfResponsable(object: any): object is Responsable {
  if (object != null)
    return 'isActive' in object;
  return false;
}

export function isInt(value: any) {
  return !isNaN(value) &&
    parseInt(value) == value &&
    !isNaN(parseInt(value, 10));
}
