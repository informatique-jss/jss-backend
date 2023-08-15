import { SortTableColumn } from "../../miscellaneous/model/SortTableColumn";
import { Affaire } from "../../quotation/model/Affaire";
import { Confrere } from "../../quotation/model/Confrere";
import { Invoice } from "../../quotation/model/Invoice";
import { IQuotation } from '../../quotation/model/IQuotation';
import { ITiers } from "../../tiers/model/ITiers";
import { Tiers } from "../../tiers/model/Tiers";

export function getRemainingToPay(invoice: Invoice) {
  return Math.round((invoice.totalPrice - getAmountPayed(invoice)) * 100) / 100;
}

export function getColumnLink(column: SortTableColumn, element: any) {
  if (element && column.id == "customerOrderName") {
    if (element.confrereId)
      return ['/confrere', element.confrereId];
    if (element.responsableId)
      return ['/tiers/responsable', element.responsableId];
    if (element.tiersId)
      return ['/tiers', element.tiersId];
  }
  if (element && column.id == "tiers") {
    if (element.tiersId)
      return ['/tiers', element.tiersId];
  }
  if (element && column.id == "customerOrderId" && element.customerOrderId) {
    if (element.isQuotation)
      return ['/quotation/', element.customerOrderId];
    return ['/order/', element.customerOrderId];
  }
  if (element.customerOrder && element.customerOrder.tiers)
    return ['/tiers', element.customerOrder.tiers.id];
  return null;
}

export function getCustomerOrderNameForInvoice(element: Invoice) {
  if (element.customerOrder) {
    return getCustomerOrderNameForIQuotation(element.customerOrder);
  } else {
    if (element.tiers)
      return element.tiers.firstname + " " + element.tiers.lastname;
    if (element.responsable)
      return element.responsable.firstname + " " + element.responsable.lastname;
    if (element.confrere)
      return element.confrere.label
  }
  return "";
}

export function getCustomerOrderNameForITiers(element: ITiers) {
  if ((element as Tiers).denomination)
    return (element as Tiers).denomination;
  if ((element as Tiers).firstname)
    return (element as Tiers).firstname + " " + (element as Tiers).lastname;
  if ((element as Confrere).label)
    return (element as Confrere).label;
  return "";
}

export function getCustomerOrderNameForIQuotation(element: IQuotation) {
  if (element) {
    if (element.confrere)
      return element.confrere.label
    if (element.responsable)
      return element.responsable.firstname + " " + element.responsable.lastname;
    if (element.tiers)
      return element.tiers.firstname + " " + element.tiers.lastname;
  }
  return "";
}

export function getCustomerOrderForInvoice(invoice: Invoice): ITiers {
  if (invoice.customerOrder) {
    return getCustomerOrderForIQuotation(invoice.customerOrder);
  } else {
    if (invoice.tiers)
      return invoice.tiers;
    if (invoice.responsable)
      return invoice.responsable;
    if (invoice.confrere)
      return invoice.confrere;
  }
  return {} as ITiers;
}

export function getCustomerOrderForIQuotation(customerOrder: IQuotation): ITiers {
  if (customerOrder) {
    if (customerOrder.confrere)
      return customerOrder.confrere;
    if (customerOrder.responsable)
      return customerOrder.responsable;
    if (customerOrder.tiers)
      return customerOrder.tiers;
  }
  return {} as ITiers;
}

export function getProviderLabelForInvoice(invoice: Invoice): any {
  if (invoice.confrere)
    return invoice.confrere.label;
  if (invoice.tiers)
    return invoice.tiers.denomination ? invoice.tiers.denomination : (invoice.tiers.firstname + " " + invoice.tiers.lastname);
  if (invoice.responsable)
    return invoice.responsable.firstname + " " + invoice.responsable.lastname;
  if (invoice.competentAuthority)
    return invoice.competentAuthority.label;
  return null;
}

export function getAffaireList(invoice: Invoice): string {
  if (invoice && invoice.customerOrder)
    return invoice.customerOrder.assoAffaireOrders.map(asso => (asso.affaire.denomination ? asso.affaire.denomination : (asso.affaire.firstname + ' ' + asso.affaire.lastname)) + (asso.affaire.city ? " (" + asso.affaire.city.label + ")" : "")).join(", ");
  return "";
}

export function getAffaireListForProviderInvoice(invoice: Invoice): string {
  if (invoice && invoice.customerOrderForInboundInvoice)
    return invoice.customerOrderForInboundInvoice.assoAffaireOrders.map(asso => (asso.affaire.denomination ? asso.affaire.denomination : (asso.affaire.firstname + ' ' + asso.affaire.lastname)) + (asso.affaire.city ? " (" + asso.affaire.city.label + ", " + + asso.affaire.siret + ")" : "")).join(", ");
  return "";
}

export function getAffaireListFromIQuotation(customerOrder: IQuotation): string {
  if (customerOrder)
    return customerOrder.assoAffaireOrders.map(asso => (asso.affaire.denomination ? asso.affaire.denomination : (asso.affaire.firstname + ' ' + asso.affaire.lastname)) + (asso.affaire.city ? " (" + asso.affaire.city.label + ")" : "")).join(", ");
  return "";
}

export function getAffaireListArray(invoice: Invoice): Affaire[] | undefined {
  if (invoice && invoice.customerOrder)
    return invoice.customerOrder.assoAffaireOrders.map(asso => asso.affaire);
  return undefined;
}

export function getAffaireListArrayForIQuotation(quotation: IQuotation): Affaire[] | undefined {
  if (quotation)
    return quotation.assoAffaireOrders.map(asso => asso.affaire);
  return undefined;
}

export function getResponsableName(element: any) {
  if (element.customerOrder) {
    if (element.customerOrder.responsable)
      return element.customerOrder.responsable.tiers.firstname + " " + element.customerOrder.responsable.tiers.lastname;
  }
  return "";
}

export function getAmountPayed(invoice: Invoice) {
  let payed = 0;
  if (invoice.payments && invoice.payments.length)
    for (let payment of invoice.payments)
      if (!payment.isCancelled)
        if (payment.isAppoint)
          payed += Math.abs(payment.paymentAmount)
        else
          payed += payment.paymentAmount;

  return Math.round(payed * 100) / 100;
}

export function getLetteringDate(invoice: Invoice): Date | undefined {
  if (invoice && invoice.accountingRecords)
    for (let accountingRecord of invoice.accountingRecords)
      if (accountingRecord.letteringDateTime)
        return accountingRecord.letteringDateTime;
  return undefined;
}

