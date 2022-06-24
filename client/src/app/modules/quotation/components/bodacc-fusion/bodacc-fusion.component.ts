import { Component, Input, OnInit, SimpleChanges, ViewChild } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MatAccordion } from '@angular/material/expansion';
import { CustomErrorStateMatcher } from 'src/app/app.component';
import { COMPETENT_AUTHORITY_TYPE_RCS_CODE } from 'src/app/libs/Constants';
import { BodaccFusion } from '../../model/BodaccFusion';
import { BodaccFusionAbsorbedCompany } from '../../model/BodaccFusionAbsorbedCompany';
import { BodaccFusionMergingCompany } from '../../model/BodaccFusionMergingCompany';
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
      if (!this.bodaccFusion.bodaccFusionAbsorbedCompanies)
        this.createAbsorbedCompany();
      if (!this.bodaccFusion.bodaccFusionMergingCompanies)
        this.createMergedCompany();
      for (let mergedCompany of this.bodaccFusion.bodaccFusionMergingCompanies) {
        if (mergedCompany.mergingCompanyRcsDeclarationDate)
          mergedCompany.mergingCompanyRcsDeclarationDate = new Date(mergedCompany.mergingCompanyRcsDeclarationDate);
      }
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

  createAbsorbedCompany() {
    if (!this.bodaccFusion.bodaccFusionAbsorbedCompanies)
      this.bodaccFusion.bodaccFusionAbsorbedCompanies = [] as Array<BodaccFusionAbsorbedCompany>;
    this.bodaccFusion.bodaccFusionAbsorbedCompanies.push({} as BodaccFusionAbsorbedCompany);
  }

  createMergedCompany() {
    if (!this.bodaccFusion.bodaccFusionMergingCompanies)
      this.bodaccFusion.bodaccFusionMergingCompanies = [] as Array<BodaccFusionMergingCompany>;
    this.bodaccFusion.bodaccFusionMergingCompanies.push({} as BodaccFusionMergingCompany);
  }

  deleteAbsorbedCompany(bodaccAbsorbedCompany: BodaccFusionAbsorbedCompany) {
    for (let i = 0; i < this.bodaccFusion.bodaccFusionAbsorbedCompanies.length; i++) {
      const absorbedCompany = this.bodaccFusion.bodaccFusionAbsorbedCompanies[i];
      if (JSON.stringify(absorbedCompany).toLowerCase() == JSON.stringify(bodaccAbsorbedCompany).toLowerCase())
        this.bodaccFusion.bodaccFusionAbsorbedCompanies.splice(i, 1);
    }
  }

  deleteMergedCompany(bodaccFusionMergingCompany: BodaccFusionMergingCompany) {
    for (let i = 0; i < this.bodaccFusion.bodaccFusionMergingCompanies.length; i++) {
      const mergedCompany = this.bodaccFusion.bodaccFusionMergingCompanies[i];
      if (JSON.stringify(mergedCompany).toLowerCase() == JSON.stringify(bodaccFusionMergingCompany).toLowerCase())
        this.bodaccFusion.bodaccFusionMergingCompanies.splice(i, 1);
    }
  }

  fillSirenMergingCompany(siren: Siren, bodaccFusionMergingCompany: BodaccFusionMergingCompany) {
    if (siren != undefined && siren != null) {
      bodaccFusionMergingCompany.mergingCompanySiren = siren!.uniteLegale.siren;
      if (siren!.uniteLegale.siren != undefined && siren!.uniteLegale.siren != null) {
        if (siren.uniteLegale.periodesUniteLegale != null && siren.uniteLegale.periodesUniteLegale != undefined && siren.uniteLegale.periodesUniteLegale.length > 0) {
          siren.uniteLegale.periodesUniteLegale.forEach(periode => {
            if (periode.dateFin == null)
              bodaccFusionMergingCompany.mergingCompanyDenomination = periode.denominationUniteLegale;
            this.bodaccFusionForm.markAllAsTouched();
          });
        }
      }
    }
  }

  fillSirenAbsorbedCompany(siren: Siren, bodaccAbsorbedCompany: BodaccFusionAbsorbedCompany) {
    if (siren != undefined && siren != null) {
      bodaccAbsorbedCompany.absorbedCompanySiren = siren!.uniteLegale.siren;
      if (siren!.uniteLegale.siren != undefined && siren!.uniteLegale.siren != null) {
        if (siren.uniteLegale.periodesUniteLegale != null && siren.uniteLegale.periodesUniteLegale != undefined && siren.uniteLegale.periodesUniteLegale.length > 0) {
          siren.uniteLegale.periodesUniteLegale.forEach(periode => {
            if (periode.dateFin == null)
              bodaccAbsorbedCompany.absorbedCompanyDenomination = periode.denominationUniteLegale;
            this.bodaccFusionForm.markAllAsTouched();
          });
        }
      }
    }
  }

}
