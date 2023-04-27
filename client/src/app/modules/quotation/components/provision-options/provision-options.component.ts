import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { getDocument } from 'src/app/libs/DocumentHelper';
import { Attachment } from 'src/app/modules/miscellaneous/model/Attachment';
import { BillingType } from 'src/app/modules/miscellaneous/model/BillingType';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { UploadAttachmentService } from 'src/app/modules/miscellaneous/services/upload.attachment.service';
import { PROVISION_ENTITY_TYPE } from 'src/app/routing/search/search.component';
import { ITiers } from '../../../tiers/model/ITiers';
import { Provision } from '../../model/Provision';
import { ConfrereService } from '../../services/confrere.service';

@Component({
  selector: 'provision-options',
  templateUrl: './provision-options.component.html',
  styleUrls: ['./provision-options.component.css']
})
export class ProvisionOptionsComponent implements OnInit {

  @Input() provision: Provision | undefined;
  @Input() customerOrder: ITiers | undefined;
  @Input() editMode: boolean = false;
  @Output() provisionChange: EventEmitter<Provision> = new EventEmitter<Provision>();

  PROVISION_ENTITY_TYPE = PROVISION_ENTITY_TYPE;

  journalTypeSpel = this.constantService.getJournalTypeSpel();
  journalTypePaper = this.constantService.getJournalTypePaper();

  billingTypeLogo = this.constantService.getBillingTypeLogo();
  billingTypeRedactedByJss = this.constantService.getBillingTypeRedactedByJss();
  billingTypeBaloPackage = this.constantService.getBillingTypeBaloPackage();
  billingTypeBaloNormalization = this.constantService.getBillingTypeBaloNormalization();
  billingTypeBaloPublicationFlag = this.constantService.getBillingTypeBaloPublicationFlag();
  billingTypePublicationPaper = this.constantService.getBillingTypePublicationPaper();
  billingTypePublicationReceipt = this.constantService.getBillingTypePublicationReceipt();
  billingTypePublicationFlag = this.constantService.getBillingTypePublicationFlag();
  billingTypeBodaccFollowup = this.constantService.getBillingTypeBodaccFollowup();
  billingTypeBodaccFollowupAndRedaction = this.constantService.getBillingTypeBodaccFollowupAndRedaction();
  billingTypeNantissementDeposit = this.constantService.getBillingTypeNantissementDeposit();
  billingTypeSocialShareNantissementRedaction = this.constantService.getBillingTypeSocialShareNantissementRedaction();
  billingTypeBusinnessNantissementRedaction = this.constantService.getBillingTypeBusinnessNantissementRedaction();
  billingTypeSellerPrivilegeRedaction = this.constantService.getBillingTypeSellerPrivilegeRedaction();
  billingTypeTreatmentMultipleModiciation = this.constantService.getBillingTypeTreatmentMultipleModiciation();
  billingTypeVacationMultipleModification = this.constantService.getBillingTypeVacationMultipleModification();
  billingTypeRegisterPurchase = this.constantService.getBillingTypeRegisterPurchase();
  billingTypeRegisterInitials = this.constantService.getBillingTypeRegisterInitials();
  billingTypeRegisterShippingCosts = this.constantService.getBillingTypeRegisterShippingCosts();
  billingTypeDisbursement = this.constantService.getBillingTypeDisbursement();
  billingTypeFeasibilityStudy = this.constantService.getBillingTypeFeasibilityStudy();
  billingTypeChronopostFees = this.constantService.getBillingTypeChronopostFees();
  billingTypeApplicationFees = this.constantService.getBillingTypeApplicationFees();
  billingTypeBankCheque = this.constantService.getBillingTypeBankCheque();
  billingTypeComplexeFile = this.constantService.getBillingTypeComplexeFile();
  billingTypeBilan = this.constantService.getBillingTypeBilan();
  billingTypeDocumentScanning = this.constantService.getBillingTypeDocumentScanning();
  billingTypeEmergency = this.constantService.getBillingTypeEmergency();
  billingTypeVacationDepositBeneficialOwners = this.constantService.getBillingTypeVacationDepositBeneficialOwners();
  billingTypeVacationUpdateBeneficialOwners = this.constantService.getBillingTypeVacationUpdateBeneficialOwners();
  billingTypeFormalityAdditionalDeclaration = this.constantService.getBillingTypeFormalityAdditionalDeclaration();
  billingTypeCorrespondenceFees = this.constantService.getBillingTypeCorrespondenceFees();


  attachmentTypeLogo = this.constantService.getAttachmentTypeLogo();

  logoUrl: SafeUrl | undefined;

  constructor(private formBuilder: FormBuilder,
    private uploadAttachmentService: UploadAttachmentService,
    private constantService: ConstantService,
    private sanitizer: DomSanitizer,
    private confrereService: ConfrereService,
  ) { }

  optionForm = this.formBuilder.group({});

  ngOnInit() {
  }

  ngOnChanges(changes: SimpleChanges) {
    if (this.provision) {
      if (!this.provision.isLogo) {
        this.provision.isLogo = false;
      } else {
        this.setLogoUrl();
      }
      if (!this.provision.isRedactedByJss) this.provision.isRedactedByJss = this.displayOption(this.billingTypeRedactedByJss);
      if (!this.provision.isBaloPackage) this.provision.isBaloPackage = false;
      if (!this.provision.isBaloPublicationFlag) this.provision.isBaloPublicationFlag = false;
      if (!this.provision.isPublicationReceipt) this.provision.isPublicationReceipt = false;
      if (this.provision.isPublicationFlag == undefined || this.provision.isPublicationFlag == null) this.provision.isPublicationFlag = this.displayOption(this.billingTypePublicationFlag) && this.provision.announcement != undefined && this.provision.announcement.confrere != undefined && this.provision.announcement.confrere.journalType.id == this.journalTypeSpel.id;
      if (!this.provision.isPublicationPaper) this.provision.isPublicationPaper = false;
      if (!this.provision.isBodaccFollowup) this.provision.isBodaccFollowup = false;
      if (!this.provision.isBodaccFollowupAndRedaction) this.provision.isBodaccFollowupAndRedaction = false;
      if (!this.provision.isNantissementDeposit) this.provision.isNantissementDeposit = false;
      if (!this.provision.isSocialShareNantissementRedaction) this.provision.isSocialShareNantissementRedaction = false;
      if (!this.provision.isBusinnessNantissementRedaction) this.provision.isBusinnessNantissementRedaction = false;
      if (!this.provision.isSellerPrivilegeRedaction) this.provision.isSellerPrivilegeRedaction = false;
      if (!this.provision.isTreatmentMultipleModiciation) this.provision.isTreatmentMultipleModiciation = false;
      if (!this.provision.isVacationMultipleModification) this.provision.isVacationMultipleModification = false;
      if (!this.provision.isRegisterPurchase) this.provision.isRegisterPurchase = false;
      if (!this.provision.isRegisterInitials) this.provision.isRegisterInitials = false;
      if (!this.provision.isRegisterShippingCosts) this.provision.isRegisterShippingCosts = false;
      if (!this.provision.isDisbursement) this.provision.isDisbursement = false;
      if (!this.provision.isFeasibilityStudy) this.provision.isFeasibilityStudy = false;
      if (!this.provision.isChronopostFees) this.provision.isChronopostFees = false;
      if (!this.provision.isApplicationFees) this.provision.isApplicationFees = false;
      if (!this.provision.isBankCheque) this.provision.isBankCheque = false;
      if (!this.provision.isComplexeFile) this.provision.isComplexeFile = false;
      if (!this.provision.isBilan) this.provision.isBilan = false;
      if (!this.provision.isEmergency) this.provision.isEmergency = false;
      if (!this.provision.isComplexeFile) this.provision.isComplexeFile = false;
      if (!this.provision.isDocumentScanning) this.provision.isDocumentScanning = false;
      if (!this.provision.isEmergency) this.provision.isEmergency = false;
      if (!this.provision.isVacationDepositBeneficialOwners) this.provision.isVacationDepositBeneficialOwners = false;
      if (!this.provision.isVacationUpdateBeneficialOwners) this.provision.isVacationUpdateBeneficialOwners = false;
      if (!this.provision.isFormalityAdditionalDeclaration) this.provision.isFormalityAdditionalDeclaration = false;
      if (!this.provision.isCorrespondenceFees) this.provision.isCorrespondenceFees = false;

      this.fillPublicationPaperNumber();
    }
  }

  displayOption(billingTypeToDisplay: BillingType): boolean {
    if (this.provision && billingTypeToDisplay && this.provision.provisionType && this.provision.provisionType.billingTypes)
      for (let billingType of this.provision.provisionType.billingTypes)
        if (billingType.id == billingTypeToDisplay.id)
          return true;
    return false;
  }


  updateAttachments(attachments: Attachment[]) {
    if (attachments && this.provision) {
      this.provision.attachments = attachments;
      this.setLogoUrl();
    }
  }

  setLogoUrl() {
    if (this.provision && this.provision.attachments != null && this.provision.attachments) {
      this.provision.attachments.forEach(attachment => {
        if (attachment.attachmentType.id == this.attachmentTypeLogo.id)
          this.uploadAttachmentService.previewAttachmentUrl(attachment).subscribe((response: any) => {
            let binaryData = [];
            binaryData.push(response.body);
            let url = window.URL.createObjectURL(new Blob(binaryData, { type: response.headers.get("content-type") }));
            this.logoUrl = this.sanitizer.bypassSecurityTrustUrl(url);
          })
      })
    }
  }

  toggleIsBodaccFollowup() {
    if (this.provision) {
      if (this.provision.isBodaccFollowup)
        this.provision.isBodaccFollowupAndRedaction = false;
    }
  }

  toggleIsBodaccRedaction() {
    if (this.provision) {
      if (this.provision.isBodaccFollowupAndRedaction)
        this.provision.isBodaccFollowup = false;
    }
  }

  changeNantissementDeposit(value: boolean) {
    if (value && this.provision)
      this.provision.isNantissementDeposit = true;
  }

  toggleIsMultipleVacation() {
    if (this.provision)
      if (this.provision.isVacationMultipleModification)
        this.provision.isTreatmentMultipleModiciation = true;
      else
        this.provision.isTreatmentMultipleModiciation = false;
  }

  toggleIsMultipleTreatment() {
    if (this.provision)
      if (this.provision.isTreatmentMultipleModiciation)
        this.provision.isVacationMultipleModification = true;
      else
        this.provision.isVacationMultipleModification = false;
  }

  canDisplayNantissementDeposit() {
    return this.canDisplayNantissement(this.constantService.getStringNantissementDepositFormeJuridiqueCode());
  }
  canDisplayNantissementSocialShare() {
    return this.canDisplayNantissement(this.constantService.getStrinSocialShareNantissementRedactionFormeJuridiqueCode());
  }
  canDisplayNantissementBusiness() {
    return this.canDisplayNantissement(this.constantService.getStringBusinnessNantissementRedactionFormeJuridiqueCode());
  }

  canDisplayNantissement(codeList: string) {
    if (this.provision && (this.provision.simpleProvision || this.provision.formalite))
      return true;

    return false;
  }
  fillPublicationPaperNumber() {
    if (this.provision && this.provision.announcement && (!this.provision.publicationPaperAffaireNumber && !this.provision.publicationPaperClientNumber)) {
      let document = getDocument(this.constantService.getDocumentTypePaper(), this.provision.announcement);
      if (document) {
        if (document.numberMailingAffaire && document.numberMailingAffaire > 0) {
          this.provision.publicationPaperAffaireNumber = document.numberMailingAffaire;
          this.provision.isPublicationPaper = true;
        }
        if (document.numberMailingClient && document.numberMailingClient > 0) {
          this.provision.publicationPaperClientNumber = document.numberMailingClient;
          this.provision.isPublicationPaper = true;
        }
      }
    }
  }
  provisionChangeFunction() {
    this.provisionChange.emit(this.provision);
  }
}
