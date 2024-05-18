export interface TurnoverReporting {
  invoiceDateYear: string;
  invoiceDateMonth: string;
  invoiceDateWeek: string;
  invoiceDateDay: string;
  turnoverAmountWithoutTax: number;
  turnoverAmountWithTax: number;
  turnoverAmountWithoutDebourWithoutTax: number;
  turnoverAmountWithoutDebourWithTax: number;
  nbrCustomerOrder: number;
  nbrInvoices: number;
  nbrCreditNote: number;
  tiersLabel: string;
  tiersCategory: string;
  invoiceCreator: string;
  salesEmployeeLabel: string;
  invoiceStatusLabel: string;
  nbrAnnouncement: number;
  announcementDepartment: string;
  confrereLabel: string;
  reminderType: string;
}
