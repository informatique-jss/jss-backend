import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ACCOUNTING, ACCOUNTING_RESPONSIBLE, ADMINISTRATEURS, BETA_TESTEURS, LoginService } from '../routing/login-dialog/login.service';

@Injectable({
  providedIn: 'root'
})
export class HabilitationsService {
  constructor(http: HttpClient,
    private loginService: LoginService,
  ) {
  }

  canDisplayNotifications() {
    return true;
  }

  canDisplayManagementFieldsInIncidentReport() {
    return this.loginService.hasGroup([ADMINISTRATEURS])
  }

  canDisplayKanban() {
    return true;
  }

  canViewIndicatorModule() {
    return true;
  }

  canViewIndicators() {
    return this.loginService.hasGroup([BETA_TESTEURS], false)
  }

  canDisplayMyIndicatorsForEverybody() {
    return this.loginService.hasGroup([ADMINISTRATEURS])
  }

  isAdministrator() {
    return this.loginService.hasGroup([ADMINISTRATEURS])
  }

  canDisplayExtendentMonitoring() {
    return this.loginService.hasGroup([ADMINISTRATEURS])
  }


  canEditAllCustomerOrderComments() {
    return this.loginService.hasGroup([ADMINISTRATEURS])
  }

  canUpdateIncidentResponsibility() {
    return this.loginService.hasGroup([ADMINISTRATEURS])
  }

  canViewDashboardModule() {
    return true;
  }

  canViewReportingModule() {
    return true;
  }

  canViewTiersModule() {
    return true;
  }

  canViewConfrereModule() {
    return true;
  }

  canViewCompetentAuthorityModule() {
    return true;
  }

  canEditProviderModule() {
    return this.loginService.hasGroup([ACCOUNTING_RESPONSIBLE])
  }

  canViewProviderModule() {
    return true;
  }

  canViewQuotationModule() {
    return true;
  }

  canViewCustomerOrderModule() {
    return true;
  }

  canViewPaperSetModule() {
    return true;
  }

  canViewRecurringCustomerOrderModule() {
    return true;
  }

  canViewAdministrationModule() {
    return this.loginService.hasGroup([ADMINISTRATEURS])
  }

  canViewSupervisionModule() {
    return this.loginService.hasGroup([ADMINISTRATEURS])
  }

  canViewAccountingModule() {
    return true;
  }

  canViewInvoiceModule() {
    return true;
  }

  canViewAffaireModule() {
    return true;
  }

  canViewProvisionModule() {
    return true;
  }

  canViewLogModule() {
    return this.loginService.hasGroup([ADMINISTRATEURS]);
  }

  canAddCreditNoteForCustomerOrderInvoice() {
    return this.loginService.hasGroup([ADMINISTRATEURS])
  }

  canAddNewInvoice() {
    return this.loginService.hasGroup([ACCOUNTING, ACCOUNTING_RESPONSIBLE])
  }

  canEditPreTaxPriceReinvoiced() {
    return this.loginService.hasGroup([ACCOUNTING, ACCOUNTING_RESPONSIBLE])
  }

  canAddNewInvoiceForPreviousExercize() {
    return this.loginService.hasGroup([ACCOUNTING_RESPONSIBLE])
  }

  canUpdateAccountingRecordsOnBilanJournal() {
    return this.loginService.hasGroup([ACCOUNTING_RESPONSIBLE])
  }

  canDeleteAccountingRecordsOnBilanJournal() {
    return this.loginService.hasGroup([ACCOUNTING_RESPONSIBLE])
  }

  canManuallyLetterAs400AccountingRecords() {
    return this.loginService.hasGroup([ACCOUNTING_RESPONSIBLE, ACCOUNTING])
  }

  canManuallyLetterAccountingRecords() {
    return this.loginService.hasGroup([ACCOUNTING, ACCOUNTING_RESPONSIBLE])
  }

  canAddNewAzureInvoice() {
    return this.loginService.hasGroup([ACCOUNTING, ACCOUNTING_RESPONSIBLE])
  }

  canCancelInvoice() {
    return this.loginService.hasGroup([ACCOUNTING_RESPONSIBLE])
  }

  canAddNewAccountingRecord() {
    return this.loginService.hasGroup([ACCOUNTING_RESPONSIBLE])
  }

  canAddNewCashPayment() {
    return this.loginService.hasGroup([ACCOUNTING_RESPONSIBLE])
  }

  canDisplayBilan() {
    return this.loginService.hasGroup([ACCOUNTING_RESPONSIBLE])
  }

  canDisplayProfitLost() {
    return this.loginService.hasGroup([ACCOUNTING_RESPONSIBLE])
  }

  canDisplayFae() {
    return this.loginService.hasGroup([ACCOUNTING_RESPONSIBLE])
  }

  canDisplayFnp() {
    return this.loginService.hasGroup([ACCOUNTING_RESPONSIBLE])
  }

  canDisplayTreasure() {
    return this.loginService.hasGroup([ACCOUNTING_RESPONSIBLE])
  }

  canDisplaySuspiciousInvoice() {
    return this.loginService.hasGroup([ACCOUNTING_RESPONSIBLE])
  }

  canImportOfxFile() {
    return this.loginService.hasGroup([ACCOUNTING, ACCOUNTING_RESPONSIBLE])
  }

  canAddCheckPayment() {
    return this.loginService.hasGroup([ACCOUNTING, ACCOUNTING_RESPONSIBLE])
  }

  canModifyPaymentAssociation() {
    return this.loginService.hasGroup([ACCOUNTING, ACCOUNTING_RESPONSIBLE])
  }

  canEditProvisionType() {
    return this.loginService.hasGroup([ACCOUNTING, ACCOUNTING_RESPONSIBLE])
  }

  canMovePaymentToWaitingAccount() {
    return this.loginService.hasGroup([ADMINISTRATEURS, ACCOUNTING_RESPONSIBLE])
  }

  canPutInAnyAccountingAccount() {
    return this.loginService.hasGroup([ADMINISTRATEURS, ACCOUNTING, ACCOUNTING_RESPONSIBLE])
  }

  canCutPayment() {
    return this.loginService.hasGroup([ADMINISTRATEURS, ACCOUNTING_RESPONSIBLE])
  }

  canCancelBankTransfert() {
    return this.loginService.hasGroup([ACCOUNTING, ACCOUNTING_RESPONSIBLE])
  }

  canViewBankBalance() {
    return this.loginService.hasGroup([ACCOUNTING_RESPONSIBLE])
  }

  canRefundPayment() {
    return this.loginService.hasGroup([ACCOUNTING_RESPONSIBLE])
  }

  canOfferCustomerOrder() {
    return this.loginService.hasGroup([ACCOUNTING_RESPONSIBLE])
  }

  canImportInfogreffeInvoice() {
    return this.loginService.hasGroup([ADMINISTRATEURS]);
  }

  canByPassMultipleCustomerOrderOnAssociationCheck() {
    return this.loginService.hasGroup([ADMINISTRATEURS, ACCOUNTING_RESPONSIBLE, ACCOUNTING]);
  }

  canReinitInvoicing() {
    return this.loginService.hasGroup([ACCOUNTING, ACCOUNTING_RESPONSIBLE])
  }

  canByPassProvisionLockOnBilledOrder() {
    return this.loginService.hasGroup([ADMINISTRATEURS]);
  }

  canImportSageFile() {
    return this.loginService.hasGroup([ADMINISTRATEURS, ACCOUNTING_RESPONSIBLE]);
  }
}



