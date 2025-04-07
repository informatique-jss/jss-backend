import { ServiceFamilyGroup } from "./ServiceFamilyGroup";

export interface ServiceFamily {
  id: number;
  code: string;
  label: string;
  customLabel: string;
  myJssIcon: string;
  comment: string;
  serviceFamilyGroup: ServiceFamilyGroup;
}
