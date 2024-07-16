import { ServiceFieldType } from "./ServiceFieldType";
import { ServiceType } from "./ServiceType";

export interface AssoServiceFieldType {
  id: number;
  service: ServiceType;
  serviceFieldType: ServiceFieldType;
  stringValue: string;
  integerValue: number;
  dateValue: Date;
  radioValue: string;
  textAreaValue: string;
  isMandatory: boolean;
}
