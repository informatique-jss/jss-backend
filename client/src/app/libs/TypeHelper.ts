import { IAttachment } from "../modules/miscellaneous/model/IAttachment";
import { IAttachmentCode } from "../modules/miscellaneous/model/IAttachmentCode";
import { Confrere } from "../modules/quotation/model/Confrere";
import { CustomerOrder } from "../modules/quotation/model/CustomerOrder";
import { IQuotation } from "../modules/quotation/model/IQuotation";
import { Quotation } from "../modules/quotation/model/Quotation";
import { Responsable } from "../modules/tiers/model/Responsable";
import { Tiers } from "../modules/tiers/model/Tiers";

export function instanceOfTiers(object: any): object is Tiers {
  return !instanceOfResponsable(object);
}

export function instanceOfConfrere(object: any): object is Confrere {
  if (object != null)
    return 'paperGrade' in object;
  return false;
}

export function instanceOfResponsable(object: any): object is Responsable {
  if (object != null)
    return 'isActive' in object;
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
