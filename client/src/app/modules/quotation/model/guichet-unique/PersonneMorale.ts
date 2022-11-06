import { AdresseEntreprise } from "./AdresseEntreprise";
import { AutresEtablissement } from "./AutresEtablissement";
import { BeneficiairesEffectif } from "./BeneficiairesEffectif";
import { Composition } from "./Composition";
import { DetailCessationEntreprise } from "./DetailCessationEntreprise";
import { EtablissementPrincipal } from "./EtablissementPrincipal";
import { Identite } from "./Identite";
import { OptionsFiscales } from "./OptionsFiscales";

export interface PersonneMorale {
  identite: Identite;
  adresseEntreprise: AdresseEntreprise;
  composition: Composition;
  etablissementPrincipal: EtablissementPrincipal;
  autresEtablissements: AutresEtablissement[];
  optionsFiscales: OptionsFiscales;
  detailCessationEntreprise: DetailCessationEntreprise;
  beneficiairesEffectifs: BeneficiairesEffectif[];
}

