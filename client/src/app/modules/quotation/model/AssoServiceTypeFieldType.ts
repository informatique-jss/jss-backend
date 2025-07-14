import { ServiceFieldType } from './ServiceFieldType';
import { ServiceType } from "./ServiceType";

export interface AssoServiceTypeFieldType {
  id: number;
  service: ServiceType;
  serviceFieldType: ServiceFieldType;
  isMandatory: boolean;
  serviceFieldTypeDependancy: ServiceFieldType;
  serviceFieldTypeDependancyValue: string;
}
