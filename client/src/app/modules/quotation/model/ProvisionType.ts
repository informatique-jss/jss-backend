import { ProvisionFamilyType } from "./ProvisionFamilyType";

export interface ProvisionType {
  id: number;
  code: string;
  label: string;
  provisionFamilyType: ProvisionFamilyType;
}
