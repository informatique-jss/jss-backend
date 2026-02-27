
export interface GuichetUniqueDepositInfoDto {
  depositDate: Date;
  waitingForValidationPartnerCenterName: string;
  waitingForValidationFromDate: Date;
  askingMissingDocumentDates: Date[];
  validationDate: Date;
}
