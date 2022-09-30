import { SortTableColumn } from "../../miscellaneous/model/SortTableColumn";
import { Confrere } from "../../quotation/model/Confrere";
import { CustomerOrder } from "../../quotation/model/CustomerOrder";
import { Invoice } from "../../quotation/model/Invoice";
import { ITiers } from "../../tiers/model/ITiers";
import { Tiers } from "../../tiers/model/Tiers";

export function getAmountRemaining(invoice: Invoice) {
  return Math.round((invoice.totalPrice - getAmountPayed(invoice)) * 100) / 100;
}

export function getColumnLink(column: SortTableColumn, element: any) {
  if (element && column.id == "customerOrderName") {
    if (element.customerOrder.responsable)
      return ['/tiers/responsable/', element.customerOrder.responsable.id];
    if (element.customerOrder.tiers)
      return ['/tiers/', element.customerOrder.tiers.id];
  }
  if (element && column.id == "customerOrderId") {
    if (element.isQuotation)
      return ['/quotation/', element.customerOrder.id];
    return ['/order/', element.customerOrder.id];
  }
  return ['/tiers', element.customerOrder.tiers.id];
}

export function getCustomerOrderNameForInvoice(element: Invoice) {
  if (element.customerOrder) {
    if (element.customerOrder.confrere)
      return element.customerOrder.confrere.label
    if (element.customerOrder.responsable)
      return element.customerOrder.responsable.firstname + " " + element.customerOrder.responsable.lastname;
    if (element.customerOrder.tiers)
      return element.customerOrder.tiers.firstname + " " + element.customerOrder.tiers.lastname;
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

export function getCustomerOrderForInvoice(invoice: Invoice): ITiers {
  if (invoice.customerOrder) {
    return getCustomerOrderForCustomerOrder(invoice.customerOrder);
  }
  return {} as ITiers;
}

export function getCustomerOrderForCustomerOrder(customerOrder: CustomerOrder): ITiers {
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

export function getResponsableName(element: any) {
  if (element.customerOrder) {
    if (element.customerOrder.responsable)
      return element.customerOrder.responsable.tiers.firstname + " " + element.customerOrder.responsable.tiers.lastname;
  }
  return "";
}

export function getAffaireListArray(invoice: any) {
  let affaires = [];
  if (invoice.customerOrder && invoice.customerOrder.provisions) {
    for (let provision of invoice.customerOrder.provisions) {
      if (provision.affaire) {
        affaires.push(provision.affaire);
      }
    }
  }
  return affaires;
}

export function getAffaireList(invoice: any) {
  return getAffaireListArray(invoice).map(affaire => {
    if (affaire.denomination) {
      return affaire.denomination;
    } else {
      return affaire.firstname + " " + affaire.lastname;
    }
  }).join(", ");
}

export function getAmountPayed(invoice: Invoice) {
  // TODO : à revoir et à passer par les lignes de compta pour les paiements partiels
  let payed = 0;
  if (invoice.payments && invoice.payments.length)
    for (let payment of invoice.payments)
      payed += payment.paymentAmount;
  if (invoice.deposits && invoice.deposits.length)
    for (let deposit of invoice.deposits)
      payed += deposit.depositAmount;

  payed = Math.round(payed * 100) / 100;
  return payed;
}
