export interface Siren {
  header: Header;
  uniteLegale: UnitesLegale;
}


export interface Comptage {
  valeur: Valeur;
  nombre: number;
  facettes: Valeur[];
}

export interface Valeur {
}

export interface UnitesLegale {
  score: number;
  siren: string;
  statutDiffusionUniteLegale: string;
  unitePurgeeUniteLegale: boolean;
  dateCreationUniteLegale: string;
  sigleUniteLegale: string;
  sexeUniteLegale: string;
  prenom1UniteLegale: string;
  prenom2UniteLegale: string;
  prenom3UniteLegale: string;
  prenom4UniteLegale: string;
  prenomUsuelUniteLegale: string;
  pseudonymeUniteLegale: string;
  identifiantAssociationUniteLegale: string;
  trancheEffectifsUniteLegale: string;
  anneeEffectifsUniteLegale: string;
  dateDernierTraitementUniteLegale: string;
  nombrePeriodesUniteLegale: number;
  categorieEntreprise: string;
  anneeCategorieEntreprise: string;
  periodesUniteLegale: PeriodesUniteLegale[];
}

export interface PeriodesUniteLegale {
  dateFin: string;
  dateDebut: string;
  etatAdministratifUniteLegale: string;
  changementEtatAdministratifUniteLegale: boolean;
  nomUniteLegale: string;
  changementNomUniteLegale: boolean;
  nomUsageUniteLegale: string;
  changementNomUsageUniteLegale: boolean;
  denominationUniteLegale: string;
  changementDenominationUniteLegale: boolean;
  denominationUsuelle1UniteLegale: string;
  denominationUsuelle2UniteLegale: string;
  denominationUsuelle3UniteLegale: string;
  changementDenominationUsuelleUniteLegale: boolean;
  categorieJuridiqueUniteLegale: string;
  changementCategorieJuridiqueUniteLegale: boolean;
  activitePrincipaleUniteLegale: string;
  nomenclatureActivitePrincipaleUniteLegale: string;
  changementActivitePrincipaleUniteLegale: boolean;
  nicSiegeUniteLegale: string;
  changementNicSiegeUniteLegale: boolean;
  economieSocialeSolidaireUniteLegale: string;
  changementEconomieSocialeSolidaireUniteLegale: boolean;
  caractereEmployeurUniteLegale: string;
  changementCaractereEmployeurUniteLegale: boolean;
}

export interface Header {
  statut: number;
  message: string;
  total: number;
  debut: number;
  nombre: number;
  curseur: string;
  curseurSuivant: string;
}
