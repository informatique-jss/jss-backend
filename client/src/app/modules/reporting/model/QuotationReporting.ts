export interface QuotationReporting {
  affaireId: number;
  affaireSiren: string;
  affaireSiret: number;
  waitedCompetentAuthorityLabel: string;
  affaireLabel: string;
  customerOrderId: number;
  provisionId: number;
  provisionTypeLabel: string;
  provisionFamilyTypeLabel: string;
  customerOrderLabel: string;
  tiersLabel: string;
  preTaxPrice: number;
  preTaxPriceWithoutDebour: number;
  taxedPrice: number;
  provisionStatus: string;
  provisionAssignedToLabel: string;
  publicationDateMonth: string;
  salesEmployeeLabel: string;
  customerOrderStatusLabel: string;
  customerOrderCreatedDateMonth: string;
  characterNumber: number;
  invoiceDateMonth: string;
  publicationDateWeek: string;
  confrereAnnouncementLabel: string;
  noticeTypeFamilyLabel: string;
  noticeTypeLabel: string;
}
