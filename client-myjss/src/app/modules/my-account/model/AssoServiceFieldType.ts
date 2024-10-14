import { ServiceFieldType } from "./ServiceFieldType";
import { ServiceTypeFieldTypePossibleValue } from "./ServiceTypeFieldTypePossibleValue";


export interface AssoServiceFieldType {
  id: number;
  serviceFieldType: ServiceFieldType;
  stringValue: string;
  integerValue: number;
  dateValue: Date;
  selectValue: ServiceTypeFieldTypePossibleValue;
  textAreaValue: string;
  isMandatory: boolean;
  formalisteComment: string;
}
