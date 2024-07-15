import { ServiceType } from "./ServiceType";
import { ServiceFieldType } from './ServiceFieldType';

export interface AssoServiceTypeFieldType {
  id: number;
  service: ServiceType;
  serviceFieldType: ServiceFieldType;
  isMandatory: boolean;
}
