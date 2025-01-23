
import { Document } from '../../my-account/model/Document';
import { City } from '../../profile/model/City';
import { Civility } from '../../profile/model/Civility';
import { Country } from '../../profile/model/Country';
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
  orderId: number;

  customerIsIndividual: boolean;
  customerDenomination: string;
  customerAddress: string;
  customerPostalCode: string;
  customerCity: City;
  customerCountry: Country;
  customerSiret: string;

  responsableCivility: Civility;
  responsableFirstname: string;
  responsableLastname: string;
  responsableMail: string;
  responsablePhone: string;
  siret: string;

}
