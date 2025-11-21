import { ServiceTypeFieldTypePossibleValue } from "./ServiceTypeFieldTypePossibleValue";

export interface ServiceFieldType {
  id: number;
  code: string;
  label: string;
  dataType: string;
  serviceFieldTypePossibleValues: ServiceTypeFieldTypePossibleValue[];
  jsonPathToRneValue: string;
}
