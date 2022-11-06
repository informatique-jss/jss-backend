import { RegistreEirlDeLancienneEirl } from "./referentials/RegistreEirlDeLancienneEirl";

export interface AncienneEIRL {
  identificationSiren: string;
  ancienneDenominationEIRL: string;
  registreEirlDeLancienneEirl: RegistreEirlDeLancienneEirl;
  lieuImmatriculationAncienneEIRL: string;
  dateEffet: Date;
}

