import { ServiceFieldType } from "./ServiceFieldType";
import { ServiceTypeFieldTypePossibleValue } from "./ServiceTypeFieldTypePossibleValue";
import { Service } from "./Service";

export interface AssoServiceFieldType {
  id: number;
  service: Service;
  serviceFieldType: ServiceFieldType;
  stringValue: string;
  integerValue: number;
  dateValue: Date;
  selectValue: ServiceTypeFieldTypePossibleValue;
  textAreaValue: string;
  isMandatory: boolean;
  formalisteComment: string;
}
