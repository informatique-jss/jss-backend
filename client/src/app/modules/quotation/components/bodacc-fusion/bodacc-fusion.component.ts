import { Component, Input, OnInit, SimpleChanges, ViewChild } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MatAccordion } from '@angular/material/expansion';
import { CustomErrorStateMatcher } from 'src/app/app.component';
import { COMPETENT_AUTHORITY_TYPE_RCS_CODE } from 'src/app/libs/Constants';
import { BodaccFusion } from '../../model/BodaccFusion';
import { Siren } from '../../model/Siren';

@Component({
  selector: 'bodacc-fusion',
  templateUrl: './bodacc-fusion.component.html',
  styleUrls: ['./bodacc-fusion.component.css']
})
export class BodaccFusionComponent implements OnInit {

  matcher: CustomErrorStateMatcher = new CustomErrorStateMatcher();
  @Input() bodaccFusion: BodaccFusion = {} as BodaccFusion;
  @Input() editMode: boolean = false;
  @ViewChild(MatAccordion) accordion: MatAccordion | undefined;

  COMPETENT_AUTHORITY_TYPE_RCS_CODE = COMPETENT_AUTHORITY_TYPE_RCS_CODE;

  constructor(private formBuilder: UntypedFormBuilder,
  ) { }

  ngOnInit() {

  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.bodaccFusion != undefined) {
      if (this.bodaccFusion.mergingCompanyRcsDeclarationDate)
        this.bodaccFusion.mergingCompanyRcsDeclarationDate = new Date(this.bodaccFusion.mergingCompanyRcsDeclarationDate);
      if (this.bodaccFusion.mergingProjectDate)
        this.bodaccFusion.mergingProjectDate = new Date(this.bodaccFusion.mergingProjectDate);
    }
    this.bodaccFusionForm.markAllAsTouched();
  }

  bodaccFusionForm = this.formBuilder.group({
    mergingCompanySiren: ['', []],
    mergingCompanyRcsCompetentAuthority: ['', []],
    absorbedCompanySiren: ['', []],
    absorbedCompanyRcsCompetentAuthority: ['', []],
  });

  getFormStatus(): boolean {
    this.bodaccFusionForm.markAllAsTouched();
    return this.bodaccFusionForm.valid;
  }

  fillSirenMergingCompany(siren: Siren) {
    if (siren != undefined && siren != null) {
      this.bodaccFusion.mergingCompanySiren = siren!.uniteLegale.siren;
      if (siren!.uniteLegale.siren != undefined && siren!.uniteLegale.siren != null) {
        if (siren.uniteLegale.periodesUniteLegale != null && siren.uniteLegale.periodesUniteLegale != undefined && siren.uniteLegale.periodesUniteLegale.length > 0) {
          siren.uniteLegale.periodesUniteLegale.forEach(periode => {
            if (periode.dateFin == null)
              this.bodaccFusion.mergingCompanyDenomination = periode.denominationUniteLegale;
            this.bodaccFusionForm.markAllAsTouched();
          });
        }
      }
    }
  }

  fillSirenAbsorbedCompany(siren: Siren) {
    if (siren != undefined && siren != null) {
      this.bodaccFusion.absorbedCompanySiren = siren!.uniteLegale.siren;
      if (siren!.uniteLegale.siren != undefined && siren!.uniteLegale.siren != null) {
        if (siren.uniteLegale.periodesUniteLegale != null && siren.uniteLegale.periodesUniteLegale != undefined && siren.uniteLegale.periodesUniteLegale.length > 0) {
          siren.uniteLegale.periodesUniteLegale.forEach(periode => {
            if (periode.dateFin == null)
              this.bodaccFusion.absorbedCompanyDenomination = periode.denominationUniteLegale;
            this.bodaccFusionForm.markAllAsTouched();
          });
        }
      }
    }
  }

}
