import { Component, Input, OnInit, SimpleChanges } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { UploadAttachmentService } from 'src/app/modules/miscellaneous/services/upload.attachment.service';
import { ActeDeposit } from '../../model/ActeDeposit';
import { Affaire } from '../../model/Affaire';
import { Provision } from '../../model/Provision';

@Component({
  selector: 'acte-deposit',
  templateUrl: './acte-deposit.component.html',
  styleUrls: ['./acte-deposit.component.css']
})
export class ActeDepositComponent implements OnInit {
  @Input() provision: Provision | undefined;
  @Input() affaire: Affaire | undefined;
  @Input() editMode: boolean = false;

  constructor(private formBuilder: FormBuilder,
    private uploadAttachmentService: UploadAttachmentService,
    private constantService: ConstantService,
  ) { }

  acteDepositForm = this.formBuilder.group({});

  ngOnInit() {
    if (this.provision && this.provision.formalite && !this.provision.formalite.acteDeposit)
      this.provision.formalite.acteDeposit = {} as ActeDeposit;

    if (this.provision && this.provision.formalite) {
      if (!this.provision.formalite.acteDeposit.isReductionCapitalSocial) this.provision.formalite.acteDeposit.isReductionCapitalSocial = false;
      if (!this.provision.formalite.acteDeposit.isTransformation) this.provision.formalite.acteDeposit.isTransformation = false;
      if (!this.provision.formalite.acteDeposit.isFusionScission) this.provision.formalite.acteDeposit.isFusionScission = false;
      if (!this.provision.formalite.acteDeposit.isFusionTransfrontaliere) this.provision.formalite.acteDeposit.isFusionTransfrontaliere = false;
      if (!this.provision.formalite.acteDeposit.isApportPartiel) this.provision.formalite.acteDeposit.isApportPartiel = false;
      if (!this.provision.formalite.acteDeposit.isCessation) this.provision.formalite.acteDeposit.isCessation = false;
      if (!this.provision.formalite.acteDeposit.isSocialPartsCession) this.provision.formalite.acteDeposit.isSocialPartsCession = false;
      if (!this.provision.formalite.acteDeposit.isSocialPartsDonation) this.provision.formalite.acteDeposit.isSocialPartsDonation = false;
      if (!this.provision.formalite.acteDeposit.isMandateExtension) this.provision.formalite.acteDeposit.isMandateExtension = false;
      if (!this.provision.formalite.acteDeposit.isFusion) this.provision.formalite.acteDeposit.isFusion = false;
      if (!this.provision.formalite.acteDeposit.isScission) this.provision.formalite.acteDeposit.isScission = false;
      if (!this.provision.formalite.acteDeposit.isAnnualAccountRefusal) this.provision.formalite.acteDeposit.isAnnualAccountRefusal = false;
      if (!this.provision.formalite.acteDeposit.isBylawsAmendments) this.provision.formalite.acteDeposit.isBylawsAmendments = false;
      if (!this.provision.formalite.acteDeposit.isOrder) this.provision.formalite.acteDeposit.isOrder = false;
      if (!this.provision.formalite.acteDeposit.isLiquidatorReport) this.provision.formalite.acteDeposit.isLiquidatorReport = false;
      if (!this.provision.formalite.acteDeposit.isContratAppui) this.provision.formalite.acteDeposit.isContratAppui = false;
      if (!this.provision.formalite.acteDeposit.isInformationConjoint) this.provision.formalite.acteDeposit.isInformationConjoint = false;
      if (!this.provision.formalite.acteDeposit.isAffectationPatrimoine) this.provision.formalite.acteDeposit.isAffectationPatrimoine = false;
      if (!this.provision.formalite.acteDeposit.isDelegationPower) this.provision.formalite.acteDeposit.isDelegationPower = false;
    }
  }

  ngOnChanges(changes: SimpleChanges) {
  }

}
