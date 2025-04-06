import { ServiceType } from "../../my-account/model/ServiceType";

export interface ServiceFamily {
  id: number;
  code: string;
  label: string;
  customLabel: string;
  myJssIcon: string;
  comment: string;
  services: ServiceType[];
}
