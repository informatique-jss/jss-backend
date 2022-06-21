package com.jss.osiris.modules.quotation.model;

import java.util.List;

public class Etablissement {
	private int score;
	private String siren;
	private String nic;
	private String siret;
	private String statutDiffusionEtablissement;
	private String dateCreationEtablissement;
	private String trancheEffectifsEtablissement;
	private String anneeEffectifsEtablissement;
	private String activitePrincipaleRegistreMetiersEtablissement;
	private String dateDernierTraitementEtablissement;
	private boolean etablissementSiege;
	private int nombrePeriodesEtablissement;
	private UniteLegale uniteLegale;
	private AdresseEtablissement adresseEtablissement;
	private Adresse2Etablissement adresse2Etablissement;
	private List<PeriodesEtablissement> periodesEtablissement;

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public String getSiren() {
		return siren;
	}

	public void setSiren(String siren) {
		this.siren = siren;
	}

	public String getNic() {
		return nic;
	}

	public void setNic(String nic) {
		this.nic = nic;
	}

	public String getSiret() {
		return siret;
	}

	public void setSiret(String siret) {
		this.siret = siret;
	}

	public String getStatutDiffusionEtablissement() {
		return statutDiffusionEtablissement;
	}

	public void setStatutDiffusionEtablissement(String statutDiffusionEtablissement) {
		this.statutDiffusionEtablissement = statutDiffusionEtablissement;
	}

	public String getDateCreationEtablissement() {
		return dateCreationEtablissement;
	}

	public void setDateCreationEtablissement(String dateCreationEtablissement) {
		this.dateCreationEtablissement = dateCreationEtablissement;
	}

	public String getTrancheEffectifsEtablissement() {
		return trancheEffectifsEtablissement;
	}

	public void setTrancheEffectifsEtablissement(String trancheEffectifsEtablissement) {
		this.trancheEffectifsEtablissement = trancheEffectifsEtablissement;
	}

	public String getAnneeEffectifsEtablissement() {
		return anneeEffectifsEtablissement;
	}

	public void setAnneeEffectifsEtablissement(String anneeEffectifsEtablissement) {
		this.anneeEffectifsEtablissement = anneeEffectifsEtablissement;
	}

	public String getActivitePrincipaleRegistreMetiersEtablissement() {
		return activitePrincipaleRegistreMetiersEtablissement;
	}

	public void setActivitePrincipaleRegistreMetiersEtablissement(
			String activitePrincipaleRegistreMetiersEtablissement) {
		this.activitePrincipaleRegistreMetiersEtablissement = activitePrincipaleRegistreMetiersEtablissement;
	}

	public String getDateDernierTraitementEtablissement() {
		return dateDernierTraitementEtablissement;
	}

	public void setDateDernierTraitementEtablissement(String dateDernierTraitementEtablissement) {
		this.dateDernierTraitementEtablissement = dateDernierTraitementEtablissement;
	}

	public boolean isEtablissementSiege() {
		return etablissementSiege;
	}

	public void setEtablissementSiege(boolean etablissementSiege) {
		this.etablissementSiege = etablissementSiege;
	}

	public int getNombrePeriodesEtablissement() {
		return nombrePeriodesEtablissement;
	}

	public void setNombrePeriodesEtablissement(int nombrePeriodesEtablissement) {
		this.nombrePeriodesEtablissement = nombrePeriodesEtablissement;
	}

	public UniteLegale getUniteLegale() {
		return uniteLegale;
	}

	public void setUniteLegale(UniteLegale uniteLegale) {
		this.uniteLegale = uniteLegale;
	}

	public AdresseEtablissement getAdresseEtablissement() {
		return adresseEtablissement;
	}

	public void setAdresseEtablissement(AdresseEtablissement adresseEtablissement) {
		this.adresseEtablissement = adresseEtablissement;
	}

	public Adresse2Etablissement getAdresse2Etablissement() {
		return adresse2Etablissement;
	}

	public void setAdresse2Etablissement(Adresse2Etablissement adresse2Etablissement) {
		this.adresse2Etablissement = adresse2Etablissement;
	}

	public List<PeriodesEtablissement> getPeriodesEtablissement() {
		return periodesEtablissement;
	}

	public void setPeriodesEtablissement(List<PeriodesEtablissement> periodesEtablissement) {
		this.periodesEtablissement = periodesEtablissement;
	}

}