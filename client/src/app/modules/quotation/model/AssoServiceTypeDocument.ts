import { ServiceType } from "./ServiceType";
import { TypeDocument } from "./guichet-unique/referentials/TypeDocument";

export interface AssoServiceTypeDocument {
  id: number;
  service: ServiceType;
  typeDocument: TypeDocument;
  isMandatory: boolean;
}
