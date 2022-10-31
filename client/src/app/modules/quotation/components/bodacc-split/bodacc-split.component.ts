import { Component, Input, OnInit, SimpleChanges, ViewChild } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MatAccordion } from '@angular/material/expansion';
import { CustomErrorStateMatcher } from 'src/app/app.component';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { BodaccSplit } from '../../model/BodaccSplit';
import { BodaccSplitBeneficiary } from '../../model/BodaccSplitBeneficiary';
import { BodaccSplitCompany } from '../../model/BodaccSplitCompany';
import { Siren } from '../../model/Siren';

@Component({
  selector: 'bodacc-split',
  templateUrl: './bodacc-split.component.html',
  styleUrls: ['./bodacc-split.component.css']
})
export class BodaccSplitComponent implements OnInit {

  matcher: CustomErrorStateMatcher = new CustomErrorStateMatcher();
  @Input() bodaccSplit: BodaccSplit = {} as BodaccSplit;
  @Input() instanceOfCustomerOrder: boolean = false;
  @Input() isStatusOpen: boolean = true;
  @Input() editMode: boolean = false;
  @ViewChild(MatAccordion) accordion: MatAccordion | undefined;

  competentAuthorityTypeRcs = this.constantService.getCompetentAuthorityTypeRcs();

  constructor(private formBuilder: UntypedFormBuilder,
    private constantService: ConstantService,
  ) { }

  ngOnInit() {

  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.bodaccSplit != undefined) {
      if (!this.bodaccSplit.bodaccSplitBeneficiaries)
        this.createBeneficiary();
      if (!this.bodaccSplit.bodaccSplitCompanies)
        this.createSplitCompany();
      for (let beneficiary of this.bodaccSplit.bodaccSplitBeneficiaries) {
        if (beneficiary.beneficiaryCompanyRcsDeclarationDate)
          beneficiary.beneficiaryCompanyRcsDeclarationDate = new Date(beneficiary.beneficiaryCompanyRcsDeclarationDate);
      }
      if (this.bodaccSplit.splitProjectDate)
        this.bodaccSplit.splitProjectDate = new Date(this.bodaccSplit.splitProjectDate);
    }
    this.bodaccSplitForm.markAllAsTouched();
  }

  bodaccSplitForm = this.formBuilder.group({
  });



  getFormStatus(): boolean {
    this.bodaccSplitForm.markAllAsTouched();
    return this.bodaccSplitForm.valid && this.bodaccSplit.bodaccSplitBeneficiaries.length > 0 && this.bodaccSplit.bodaccSplitCompanies.length > 0;
  }

  createBeneficiary() {
    if (!this.bodaccSplit.bodaccSplitBeneficiaries)
      this.bodaccSplit.bodaccSplitBeneficiaries = [] as Array<BodaccSplitBeneficiary>;
    this.bodaccSplit.bodaccSplitBeneficiaries.push({} as BodaccSplitBeneficiary);
  }

  deleteBeneficiary(beneficiaryCompany: BodaccSplitBeneficiary) {
    for (let i = 0; i < this.bodaccSplit.bodaccSplitBeneficiaries.length; i++) {
      const beneficiary = this.bodaccSplit.bodaccSplitBeneficiaries[i];
      if (JSON.stringify(beneficiary).toLowerCase() == JSON.stringify(beneficiaryCompany).toLowerCase())
        this.bodaccSplit.bodaccSplitBeneficiaries.splice(i, 1);
    }
  }

  createSplitCompany() {
    if (!this.bodaccSplit.bodaccSplitCompanies)
      this.bodaccSplit.bodaccSplitCompanies = [] as Array<BodaccSplitCompany>;
    this.bodaccSplit.bodaccSplitCompanies.push({} as BodaccSplitCompany);
  }

  deleteSplitCompany(bodaccSplitCompany: BodaccSplitCompany) {
    for (let i = 0; i < this.bodaccSplit.bodaccSplitCompanies.length; i++) {
      const splitCompany = this.bodaccSplit.bodaccSplitCompanies[i];
      if (JSON.stringify(splitCompany).toLowerCase() == JSON.stringify(bodaccSplitCompany).toLowerCase())
        this.bodaccSplit.bodaccSplitCompanies.splice(i, 1);
    }
  }

  fillSirenBeneficiaryCompany(siren: Siren, beneficiaryCompagny: BodaccSplitBeneficiary) {
    if (siren != undefined && siren != null) {
      beneficiaryCompagny.beneficiaryCompanySiren = siren!.uniteLegale.siren;
      if (siren!.uniteLegale.siren != undefined && siren!.uniteLegale.siren != null) {
        if (siren.uniteLegale.periodesUniteLegale != null && siren.uniteLegale.periodesUniteLegale != undefined && siren.uniteLegale.periodesUniteLegale.length > 0) {
          siren.uniteLegale.periodesUniteLegale.forEach(periode => {
            if (periode.dateFin == null)
              beneficiaryCompagny.beneficiaryCompanyDenomination = periode.denominationUniteLegale;
            this.bodaccSplitForm.markAllAsTouched();
          });
        }
      }
    }
  }

  fillSirenSplitCompany(siren: Siren, bodaccSplitCompany: BodaccSplitCompany) {
    if (siren != undefined && siren != null) {
      bodaccSplitCompany.splitCompanySiren = siren!.uniteLegale.siren;
      if (siren!.uniteLegale.siren != undefined && siren!.uniteLegale.siren != null) {
        if (siren.uniteLegale.periodesUniteLegale != null && siren.uniteLegale.periodesUniteLegale != undefined && siren.uniteLegale.periodesUniteLegale.length > 0) {
          siren.uniteLegale.periodesUniteLegale.forEach(periode => {
            if (periode.dateFin == null)
              bodaccSplitCompany.splitCompanyDenomination = periode.denominationUniteLegale;
            this.bodaccSplitForm.markAllAsTouched();
          });
        }
      }
    }
  }

}
