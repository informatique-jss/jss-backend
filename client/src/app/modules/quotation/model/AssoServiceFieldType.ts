import { ServiceFieldType } from "./ServiceFieldType";
import { ServiceFieldTypePossibleValue } from "./ServiceFieldTypePossibleValue";
import { ServiceType } from "./ServiceType";

export interface AssoServiceFieldType {
  id: number;
  service: ServiceType;
  serviceFieldType: ServiceFieldType;
  stringValue: string;
  integerValue: number;
  dateValue: Date;
  selectValue: ServiceFieldTypePossibleValue;
  textAreaValue: string;
  isMandatory: boolean;
}
