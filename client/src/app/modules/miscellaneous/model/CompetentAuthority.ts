import { AssoMailCompetentAuthorityServiceFamilyGroup } from "./AssoMailCompetentAuthorityServiceFamilyGroup";
import { City } from "./City";
import { CompetentAuthorityType } from "./CompetentAuthorityType";
import { Country } from "./Country";
import { Department } from "./Department";
import { IAttachment } from './IAttachment';
import { Mail } from "./Mail";
import { Phone } from "./Phone";
import { Provider } from "./Provider";
import { Region } from "./Region";

export interface CompetentAuthority extends IAttachment {
  label: string;
  competentAuthorityType: CompetentAuthorityType;
  departments: Department[];
  phones: Phone[];
  mails: Mail[];
  cities: City[];
  regions: Region[];
  contact: string;
  mailRecipient: string;
  address: string;
  city: City;
  postalCode: string;
  cedexComplement: string;
  country: Country;
  schedulle: string;
  observations: string;
  inpiReference: string;
  azureCustomReference: string;
  intercommunityVat: string;
  isNotToReminder: boolean;
  assoMailCompetentAuthorityServiceFamilyGroups: AssoMailCompetentAuthorityServiceFamilyGroup[];
  provider: Provider;
  jssAccountNumber: string;
}
