package com.jss.osiris.modules.accounting.model;

import java.util.List;

public class AccountingBalanceViewTitle {
    private String label;

    private List<AccountingBalanceViewTitle> subTitles;

    private List<AccountingBalanceViewItem> items;

    private Float soldeN;
    private Float soldeN1;
    private Float brutN;
    private Float brutN1;
    private Float amortissementN;
    private Float amortissementN1;

    private boolean isActifBilan;

    private List<AccountingBalanceViewTitle> totals;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Float getSoldeN() {
        return soldeN;
    }

    public void setSoldeN(Float soldeN) {
        this.soldeN = soldeN;
    }

    public Float getSoldeN1() {
        return soldeN1;
    }

    public void setSoldeN1(Float soldeN1) {
        this.soldeN1 = soldeN1;
    }

    public Float getBrutN() {
        return brutN;
    }

    public void setBrutN(Float brutN) {
        this.brutN = brutN;
    }

    public Float getBrutN1() {
        return brutN1;
    }

    public void setBrutN1(Float brutN1) {
        this.brutN1 = brutN1;
    }

    public Float getAmortissementN() {
        return amortissementN;
    }

    public void setAmortissementN(Float amortissementN) {
        this.amortissementN = amortissementN;
    }

    public Float getAmortissementN1() {
        return amortissementN1;
    }

    public void setAmortissementN1(Float amortissementN1) {
        this.amortissementN1 = amortissementN1;
    }

    public boolean isActifBilan() {
        return isActifBilan;
    }

    public void setActifBilan(boolean isActifBilan) {
        this.isActifBilan = isActifBilan;
    }

    public List<AccountingBalanceViewTitle> getSubTitles() {
        return subTitles;
    }

    public void setSubTitles(List<AccountingBalanceViewTitle> subTitles) {
        this.subTitles = subTitles;
    }

    public List<AccountingBalanceViewItem> getItems() {
        return items;
    }

    public void setItems(List<AccountingBalanceViewItem> items) {
        this.items = items;
    }

    public List<AccountingBalanceViewTitle> getTotals() {
        return totals;
    }

    public void setTotals(List<AccountingBalanceViewTitle> totals) {
        this.totals = totals;
    }

}
