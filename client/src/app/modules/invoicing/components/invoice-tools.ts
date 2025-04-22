import { SortTableColumn } from "../../miscellaneous/model/SortTableColumn";
import { Affaire } from "../../quotation/model/Affaire";
import { Invoice } from "../../quotation/model/Invoice";
import { IQuotation } from '../../quotation/model/IQuotation';
import { Tiers } from "../../tiers/model/Tiers";
import { InvoiceSearchResult } from "../model/InvoiceSearchResult";

export function getRemainingToPay(invoice: Invoice) {
  if (invoice.provider)
    return Math.round((invoice.totalPrice - (-getAmountPayed(invoice))) * 100) / 100;
  return Math.round((invoice.totalPrice - getAmountPayed(invoice)) * 100) / 100;
}

export function getColumnLink(column: SortTableColumn<InvoiceSearchResult>, element: InvoiceSearchResult) {
  if (element && column.id == "responsable") {
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
    return ['/order', element.customerOrderId];
  }
  return null;
}

export function getCustomerOrderNameForInvoice(element: Invoice) {
  if (element.customerOrder) {
    return getCustomerOrderNameForIQuotation(element.customerOrder);
  } else {
    if (element.responsable)
      return element.responsable.firstname + " " + element.responsable.lastname;
  }
  return "";
}

export function getCustomerOrderNameForTiers(element: Tiers) {
  if ((element as Tiers).denomination)
    return (element as Tiers).denomination;
  if ((element as Tiers).firstname)
    return (element as Tiers).firstname + " " + (element as Tiers).lastname;
  return "";
}

export function getCustomerOrderNameForIQuotation(element: IQuotation) {
  if (element) {
    if (element.responsable)
      return element.responsable.firstname + " " + element.responsable.lastname;
  }
  return "";
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
          payed += -payment.paymentAmount; // Appoint is on the opposite side in customer point of view (because it's a gain / lost for us when it's a lost / gain for him)
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

