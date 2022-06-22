import { Component, Input, OnInit, SimpleChanges, ViewChild } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MatAccordion } from '@angular/material/expansion';
import { CustomErrorStateMatcher } from 'src/app/app.component';
import { COMPETENT_AUTHORITY_TYPE_RCS_CODE } from 'src/app/libs/Constants';
import { BodaccSplit } from '../../model/BodaccSplit';
import { Siren } from '../../model/Siren';

@Component({
  selector: 'bodacc-split',
  templateUrl: './bodacc-split.component.html',
  styleUrls: ['./bodacc-split.component.css']
})
export class BodaccSplitComponent implements OnInit {

  matcher: CustomErrorStateMatcher = new CustomErrorStateMatcher();
  @Input() bodaccSplit: BodaccSplit = {} as BodaccSplit;
  @Input() editMode: boolean = false;
  @ViewChild(MatAccordion) accordion: MatAccordion | undefined;

  COMPETENT_AUTHORITY_TYPE_RCS_CODE = COMPETENT_AUTHORITY_TYPE_RCS_CODE;

  constructor(private formBuilder: UntypedFormBuilder,
  ) { }

  ngOnInit() {

  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.bodaccSplit != undefined) {
      if (this.bodaccSplit.beneficiaryCompanyRcsDeclarationDate)
        this.bodaccSplit.beneficiaryCompanyRcsDeclarationDate = new Date(this.bodaccSplit.beneficiaryCompanyRcsDeclarationDate);
      if (this.bodaccSplit.splitProjectDate)
        this.bodaccSplit.splitProjectDate = new Date(this.bodaccSplit.splitProjectDate);
    }
    this.bodaccSplitForm.markAllAsTouched();
  }

  bodaccSplitForm = this.formBuilder.group({
    beneficiaryCompanySiren: ['', []],
    beneficiaryCompanyRcsCompetentAuthority: ['', []],
    splitCompanySiren: ['', []],
    splitCompanyRcsCompetentAuthority: ['', []],
  });

  getFormStatus(): boolean {
    this.bodaccSplitForm.markAllAsTouched();
    return this.bodaccSplitForm.valid;
  }

  fillSirenBeneficiaryCompany(siren: Siren) {
    if (siren != undefined && siren != null) {
      this.bodaccSplit.beneficiaryCompanySiren = siren!.uniteLegale.siren;
      if (siren!.uniteLegale.siren != undefined && siren!.uniteLegale.siren != null) {
        if (siren.uniteLegale.periodesUniteLegale != null && siren.uniteLegale.periodesUniteLegale != undefined && siren.uniteLegale.periodesUniteLegale.length > 0) {
          siren.uniteLegale.periodesUniteLegale.forEach(periode => {
            if (periode.dateFin == null)
              this.bodaccSplit.beneficiaryCompanyDenomination = periode.denominationUniteLegale;
            this.bodaccSplitForm.markAllAsTouched();
          });
        }
      }
    }
  }

  fillSirenSplitCompany(siren: Siren) {
    if (siren != undefined && siren != null) {
      this.bodaccSplit.splitCompanySiren = siren!.uniteLegale.siren;
      if (siren!.uniteLegale.siren != undefined && siren!.uniteLegale.siren != null) {
        if (siren.uniteLegale.periodesUniteLegale != null && siren.uniteLegale.periodesUniteLegale != undefined && siren.uniteLegale.periodesUniteLegale.length > 0) {
          siren.uniteLegale.periodesUniteLegale.forEach(periode => {
            if (periode.dateFin == null)
              this.bodaccSplit.splitCompanyDenomination = periode.denominationUniteLegale;
            this.bodaccSplitForm.markAllAsTouched();
          });
        }
      }
    }
  }

}
