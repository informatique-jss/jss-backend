export interface Rna {
  association: Association;
}

export interface Association {
  id: number;
  is_waldec: string;
  id_association: string;
  id_ex_association: string;
  siret?: any;
  numero_reconnaissance_utilite_publique?: any;
  code_gestion: string;
  date_creation: string;
  date_derniere_declaration: string;
  date_publication_creation: string;
  date_declaration_dissolution?: any;
  nature: string;
  groupement: string;
  titre: string;
  titre_court: string;
  objet: string;
  objet_social1: string;
  objet_social2: string;
  l1_adresse_import?: any;
  l2_adresse_import?: any;
  l3_adresse_import?: any;
  adresse_siege?: any;
  adresse_numero_voie: string;
  adresse_repetition?: any;
  adresse_type_voie: string;
  adresse_libelle_voie: string;
  adresse_distribution: string;
  adresse_code_insee: string;
  adresse_code_postal: string;
  adresse_libelle_commune: string;
  adresse_gestion_nom?: any;
  adresse_gestion_format_postal?: any;
  adresse_gestion_geo?: any;
  adresse_gestion_libelle_voie: string;
  adresse_gestion_distribution: string;
  adresse_gestion_code_postal: string;
  adresse_gestion_acheminement: string;
  adresse_gestion_pays: string;
  dirigeant_civilite: string;
  telephone?: any;
  site_web?: any;
  email?: any;
  autorisation_publication_web: string;
  observation?: any;
  position_activite: string;
  derniere_maj: string;
  created_at: string;
  updated_at: string;
}
