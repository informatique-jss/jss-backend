export interface Siret {
  header: Header;
  etablissement: Etablissement;
}

export interface Etablissement {
  score: number;
  siren: string;
  nic: string;
  siret: string;
  statutDiffusionEtablissement: string;
  dateCreationEtablissement: string;
  trancheEffectifsEtablissement: string;
  anneeEffectifsEtablissement: string;
  activitePrincipaleRegistreMetiersEtablissement: string;
  dateDernierTraitementEtablissement: string;
  etablissementSiege: boolean;
  nombrePeriodesEtablissement: number;
  uniteLegale: UniteLegale;
  adresseEtablissement: AdresseEtablissement;
  adresse2Etablissement: Adresse2Etablissement;
  periodesEtablissement: PeriodesEtablissement[];
}

export interface PeriodesEtablissement {
  dateFin: string;
  dateDebut: string;
  etatAdministratifEtablissement: string;
  changementEtatAdministratifEtablissement: boolean;
  enseigne1Etablissement: string;
  enseigne2Etablissement: string;
  enseigne3Etablissement: string;
  changementEnseigneEtablissement: boolean;
  denominationUsuelleEtablissement: string;
  changementDenominationUsuelleEtablissement: boolean;
  activitePrincipaleEtablissement: string;
  nomenclatureActivitePrincipaleEtablissement: string;
  changementActivitePrincipaleEtablissement: boolean;
  caractereEmployeurEtablissement: string;
  changementCaractereEmployeurEtablissement: boolean;
}

export interface Adresse2Etablissement {
  complementAdresse2Etablissement: string;
  numeroVoie2Etablissement: string;
  indiceRepetition2Etablissement: string;
  typeVoie2Etablissement: string;
  libelleVoie2Etablissement: string;
  codePostal2Etablissement: string;
  libelleCommune2Etablissement: string;
  libelleCommuneEtranger2Etablissement: string;
  distributionSpeciale2Etablissement: string;
  codeCommune2Etablissement: string;
  codeCedex2Etablissement: string;
  libelleCedex2Etablissement: string;
  codePaysEtranger2Etablissement: string;
  libellePaysEtranger2Etablissement: string;
}

export interface AdresseEtablissement {
  complementAdresseEtablissement: string;
  numeroVoieEtablissement: string;
  indiceRepetitionEtablissement: string;
  typeVoieEtablissement: string;
  libelleVoieEtablissement: string;
  codePostalEtablissement: string;
  libelleCommuneEtablissement: string;
  libelleCommuneEtrangerEtablissement: string;
  distributionSpecialeEtablissement: string;
  codeCommuneEtablissement: string;
  codeCedexEtablissement: string;
  libelleCedexEtablissement: string;
  codePaysEtrangerEtablissement: string;
  libellePaysEtrangerEtablissement: string;
}

export interface UniteLegale {
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
  etatAdministratifUniteLegale: string;
  nomUniteLegale: string;
  denominationUniteLegale: string;
  denominationUsuelle1UniteLegale: string;
  denominationUsuelle2UniteLegale: string;
  denominationUsuelle3UniteLegale: string;
  activitePrincipaleUniteLegale: string;
  categorieJuridiqueUniteLegale: string;
  nicSiegeUniteLegale: string;
  nomenclatureActivitePrincipaleUniteLegale: string;
  nomUsageUniteLegale: string;
  economieSocialeSolidaireUniteLegale: string;
  caractereEmployeurUniteLegale: string;
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
