import { ServiceFieldTypePossibleValue } from "./ServiceFieldTypePossibleValue";

export interface ServiceFieldType {
  id: number;
  code: string;
  label: string;
  dataType: string;
  serviceFieldTypePossibleValues: ServiceFieldTypePossibleValue[];
  isMandatory: boolean;
}
