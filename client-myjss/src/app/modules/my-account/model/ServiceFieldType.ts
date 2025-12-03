import { ServiceTypeFieldTypePossibleValue } from "./ServiceTypeFieldTypePossibleValue";

export interface ServiceFieldType {
  id: number;
  code: string;
  label: string;
  dataType: string;
  value: string; // Value found thanks to RNE API from the back office
  serviceFieldTypePossibleValues: ServiceTypeFieldTypePossibleValue[];
}
