import { ServiceFamilyGroup } from "./ServiceFamilyGroup";

export interface ServiceFamily {
  id: number;
  code: string;
  label: string;
  serviceFamilyGroup: ServiceFamilyGroup;
}
