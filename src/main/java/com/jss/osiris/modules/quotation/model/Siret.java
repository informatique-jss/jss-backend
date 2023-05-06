package com.jss.osiris.modules.quotation.model;

import java.io.Serializable;

public class Siret implements Serializable {
	private Header header;
	private Etablissement etablissement;

	public Header getHeader() {
		return header;
	}

	public void setHeader(Header header) {
		this.header = header;
	}

	public Etablissement getEtablissement() {
		return etablissement;
	}

	public void setEtablissement(Etablissement etablissement) {
		this.etablissement = etablissement;
	}

}