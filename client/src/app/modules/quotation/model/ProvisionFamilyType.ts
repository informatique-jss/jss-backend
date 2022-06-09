import { ProvisionType } from "./ProvisionType";

export interface ProvisionFamilyType {
  id: number;
  code: string;
  label: string;
  provisionTypes: ProvisionType[];
}
