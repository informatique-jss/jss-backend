import { TutelleCuratelle } from "./referentials/TutelleCuratelle";

export interface CapaciteJuridique {
  statutDecede: boolean;
  dateDeces: string;
  tutelleCuratelle: TutelleCuratelle;
  DatePriseEffetCapaciteJuridique: string;
  mineurEmancipe: boolean;
}

