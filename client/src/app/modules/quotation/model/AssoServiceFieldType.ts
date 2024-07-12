import { ServiceType } from "./ServiceType";
import { ServiceFieldType } from './ServiceFieldType';

export interface AssoServiceFieldType {
  id: number;
  service: ServiceType;
  serviceFieldType: ServiceFieldType;
  isMandatory: boolean;
}
