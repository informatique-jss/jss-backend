import { ServiceTypeFieldTypePossibleValue } from "./ServiceTypeFieldTypePossibleValue";

export interface ServiceFieldType {
  id: number;
  code: string;
  label: string;
  dataType: string;
  value: string; // Only for client purposes
  serviceFieldTypePossibleValues: ServiceTypeFieldTypePossibleValue[];
}
