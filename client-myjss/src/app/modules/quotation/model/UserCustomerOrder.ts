
import { Document } from '../../my-account/model/Document';
import { Responsable } from '../../profile/model/Responsable';
import { ServiceTypeChosen } from './ServiceTypeChosen';

export interface UserCustomerOrder {
  billingDocument: Document;
  paperDocument: Document;
  digitalDocument: Document;
  serviceTypes: ServiceTypeChosen[];
  isCustomerOrder: boolean;
  isDraft: boolean;
  preTaxPrice: number | undefined;
  vatPrice: number | undefined;
  totalPrice: number | undefined;
  dummyResponsable: Responsable;
  isEmergency: boolean;
}
