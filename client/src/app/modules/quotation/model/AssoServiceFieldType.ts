import { ServiceFieldType } from "./ServiceFieldType";
import { ServiceTypeFieldTypePossibleValue } from "./ServiceTypeFieldTypePossibleValue";
import { ServiceType } from "./ServiceType";

export interface AssoServiceFieldType {
  id: number;
  service: ServiceType;
  serviceFieldType: ServiceFieldType;
  stringValue: string;
  integerValue: number;
  dateValue: Date;
  selectValue: ServiceTypeFieldTypePossibleValue;
  textAreaValue: string;
  isMandatory: boolean;
}
