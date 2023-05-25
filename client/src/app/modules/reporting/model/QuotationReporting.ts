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
  provisionStatus: string;
  provisionAssignedToLabel: string;
  publicationDateMonth: string;
  salesEmployeeLabel: string;
  quotationStatusLabel: string;
  quotationCreatedDateMonth: string;
  quotationCreatedDateDay: string;
  characterNumber: number;
  publicationDateWeek: string;
  confrereAnnouncementLabel: string;
  noticeTypeFamilyLabel: string;
  noticeTypeLabel: string;
  quotationCreator: string;
  preTaxPriceWithoutDebour: number;
  customerOrderOriginLabel: string;
}
