import { ServiceFieldType } from "./ServiceFieldType";

export interface AssoServiceTypeFieldType {
  id: number;
  serviceFieldType: ServiceFieldType;
  isMandatory: boolean;
  serviceFieldTypeDependancy: ServiceFieldType;
  serviceFieldTypeDependancyValue: string;
}
