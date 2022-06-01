import { CustomerOrder } from "../modules/quotation/model/CustomerOrder";
import { IQuotation } from "../modules/quotation/model/IQuotation";
import { Quotation } from "../modules/quotation/model/Quotation";
import { Responsable } from "../modules/tiers/model/Responsable";
import { Tiers } from "../modules/tiers/model/Tiers";

export function instanceOfTiers(object: any): object is Tiers {
  if (object != null)
    return 'isIndividual' in object;
  return false;
}

export function instanceOfResponsable(object: any): object is Responsable {
  if (object != null)
    return 'isActive' in object;
  return false;
}


export function instanceOfQuotation(object: IQuotation): object is Quotation {
  if (object != null)
    return 'customerOrder' in object;
  return false;
}

export function instanceOfCustomerOrder(object: IQuotation): object is CustomerOrder {
  if (object != null)
    return !('customerOrder' in object);
  return false;
}
