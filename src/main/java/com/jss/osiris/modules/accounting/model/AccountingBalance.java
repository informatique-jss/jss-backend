package com.jss.osiris.modules.accounting.model;

public interface AccountingBalance {

	public Float getCreditAmount();

	public Float getDebitAmount();

	public String getAccountingAccountLabel();

	public String getPrincipalAccountingAccountCode();

	public String getPrincipalAccountingAccountLabel();

	public String getAccountingAccountClassLabel();

	public String getAccountingAccountSubNumber();

	public Float getEchoir30();

	public Float getEchoir60();

	public Float getEchoir90();

	public Float getEchu30();

	public Float getEchu60();

	public Float getEchu90();

}
