package com.jss.osiris.modules.accounting.model;

import java.math.BigDecimal;

public interface AccountingBalance {

	public BigDecimal getCreditAmount();

	public BigDecimal getDebitAmount();

	public String getAccountingAccountLabel();

	public String getPrincipalAccountingAccountCode();

	public String getPrincipalAccountingAccountLabel();

	public String getAccountingAccountClassLabel();

	public String getAccountingAccountSubNumber();

	public BigDecimal getEchoir30();

	public BigDecimal getEchoir60();

	public BigDecimal getEchoir90();

	public BigDecimal getEchu30();

	public BigDecimal getEchu60();

	public BigDecimal getEchu90();

}
