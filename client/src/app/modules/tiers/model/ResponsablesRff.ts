import { Mail } from "../../miscellaneous/model/Mail";
import { Phone } from "../../miscellaneous/model/Phone";
import { Responsable } from "./Responsable";

export interface ResponsablesRff {
  id: number;
  responsableLastName: string | null;
  responsableFirstName: string | null;
  responsables: Responsable[];
  function: string;
  responsableMail: string | null;
  responsableTel: string | null;
  responsableCa: number;
  responsableNbrAl: number;
  responsableNbrForm: number;
  responsableTotalCde: number;
  responsableSub: boolean;
  responsableGift: number;
  responsableOthers: string;

}
