import { ServiceType } from "./ServiceType";
import { ServiceFieldType } from './ServiceFieldType';
import { AssoRadioValueServiceTypeFieldType } from './AssoRadioValueServiceTypeFieldType';

export interface AssoServiceTypeFieldType {
  id: number;
  service: ServiceType;
  serviceFieldType: ServiceFieldType;
  assoRadioValueServiceTypeFieldTypes: AssoRadioValueServiceTypeFieldType[];
  isMandatory: boolean;
}
