import { IAttachmentCode } from "../modules/miscellaneous/model/IAttachmentCode";
import { CustomerOrder } from "../modules/quotation/model/CustomerOrder";
import { FormaliteGuichetUnique } from "../modules/quotation/model/guichet-unique/FormaliteGuichetUnique";
import { FormaliteInfogreffe } from "../modules/quotation/model/infogreffe/FormaliteInfogreffe";
import { IQuotation } from "../modules/quotation/model/IQuotation";
import { Quotation } from "../modules/quotation/model/Quotation";
import { Responsable } from "../modules/tiers/model/Responsable";
import { Tiers } from "../modules/tiers/model/Tiers";

export function instanceOfTiers(object: any): object is Tiers {
  return !instanceOfResponsable(object);
}

export function instanceOfResponsable(object: any): object is Responsable {
  if (object != null)
    return 'isActive' in object;
  return false;
}

export function instanceOfFormaliteInfogreffe(object: any): object is FormaliteInfogreffe {
  if (object != null)
    return 'greffeDestinataire' in object;
  return false;
}

export function instanceOfFormaliteGuichetUnique(object: any): object is FormaliteGuichetUnique {
  if (object != null)
    return 'liasseNumber' in object;
  return false;
}

export function instanceOfQuotation(object: IQuotation): object is Quotation {
  return object.isQuotation;
}

export function instanceOfCustomerOrder(object: IQuotation): object is CustomerOrder {
  return !object.isQuotation;
}

export function instanceOfIAttachmentCode(object: any): object is IAttachmentCode {
  if (object != null)
    return 'code' in object;
  return false;
}

export function isInt(value: any) {
  return !isNaN(value) &&
    parseInt(value) == value &&
    !isNaN(parseInt(value, 10));
}
