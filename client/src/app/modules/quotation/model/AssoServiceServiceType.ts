import { Service } from "./Service";
import { ServiceType } from "./ServiceType";

export interface AssoServiceServiceType {
  id: number;
  service: Service;
  serviceType: ServiceType;
}
