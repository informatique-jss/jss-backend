import { Component, Input, OnInit, SimpleChanges, ViewChild } from '@angular/core';
import { AbstractControl, UntypedFormBuilder, UntypedFormGroup, ValidationErrors } from '@angular/forms';
import { MatAccordion } from '@angular/material/expansion';
import { CustomErrorStateMatcher } from 'src/app/app.component';
import { BODACC_SALE_ACT_TYPE_AUTHENTIC_CODE, BODACC_SALE_TRANSFERT_FUND_TYPE_BAIL, BODACC_SALE_TRANSFERT_FUND_TYPE_MORAL, BODACC_SALE_TRANSFERT_FUND_TYPE_PHYSIQUE, COMPETENT_AUTHORITY_TYPE_CFP_CODE } from 'src/app/libs/Constants';
import { validateSiren } from 'src/app/libs/CustomFormsValidatorsHelper';
import { CompetentAuthority } from 'src/app/modules/miscellaneous/model/CompetentAuthority';
import { CompetentAuthorityService } from 'src/app/modules/miscellaneous/services/competent-authority.service';
import { Affaire } from '../../model/Affaire';
import { BodaccSale } from '../../model/BodaccSale';
import { Siren } from '../../model/Siren';
import { TransfertFundsType } from '../../model/TransfertFundsType';

@Component({
  selector: 'bodacc-sale',
  templateUrl: './bodacc-sale.component.html',
  styleUrls: ['./bodacc-sale.component.css']
})
export class BodaccSaleComponent implements OnInit {

  matcher: CustomErrorStateMatcher = new CustomErrorStateMatcher();
  @Input() bodaccSale: BodaccSale = {} as BodaccSale;
  @Input() editMode: boolean = false;
  @Input() transfertFundsType: TransfertFundsType = {} as TransfertFundsType;
  @ViewChild(MatAccordion) accordion: MatAccordion | undefined;
  @Input() affaire: Affaire | undefined;

  BODACC_SALE_TRANSFERT_FUND_TYPE_PHYSIQUE = BODACC_SALE_TRANSFERT_FUND_TYPE_PHYSIQUE;
  BODACC_SALE_TRANSFERT_FUND_TYPE_MORAL = BODACC_SALE_TRANSFERT_FUND_TYPE_MORAL;
  BODACC_SALE_TRANSFERT_FUND_TYPE_BAIL = BODACC_SALE_TRANSFERT_FUND_TYPE_BAIL;
  BODACC_SALE_ACT_TYPE_AUTHENTIC_CODE = BODACC_SALE_ACT_TYPE_AUTHENTIC_CODE;
  COMPETENT_AUTHORITY_TYPE_CFP_CODE = COMPETENT_AUTHORITY_TYPE_CFP_CODE;

  constructor(private formBuilder: UntypedFormBuilder,
    private competentAuthoritiesService: CompetentAuthorityService,
  ) { }

  ngOnInit() {

  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.bodaccSale != undefined) {
      if (this.bodaccSale.deedDate)
        this.bodaccSale.deedDate = new Date(this.bodaccSale.deedDate);

      if (this.bodaccSale.registrationDate)
        this.bodaccSale.registrationDate = new Date(this.bodaccSale.registrationDate);

      if (this.bodaccSale.purchaserActivityStartDate)
        this.bodaccSale.purchaserActivityStartDate = new Date(this.bodaccSale.purchaserActivityStartDate);

      if (this.bodaccSale.leaseResilisationDate)
        this.bodaccSale.leaseResilisationDate = new Date(this.bodaccSale.leaseResilisationDate);

      if (!this.bodaccSale.isOwnerIndividual)
        this.bodaccSale.isOwnerIndividual = false;

      if (!this.bodaccSale.isTenantIndividual)
        this.bodaccSale.isTenantIndividual = false;

      if (this.affaire && this.affaire.city) {
        this.competentAuthoritiesService.getCompetentAuthorityByCity(this.affaire.city).subscribe(response => {
          if (response) {
            let outAuhority = [] as Array<CompetentAuthority>;
            for (let authority of response) {
              if (authority.competentAuthorityType.code == COMPETENT_AUTHORITY_TYPE_CFP_CODE)
                outAuhority.push(authority)
            }
            if (outAuhority.length == 1)
              this.bodaccSale.registrationAuthority = outAuhority[0];
          }
        })
      }


      this.bodaccSaleForm.markAllAsTouched();
    }
  }

  bodaccSaleForm = this.formBuilder.group({
    fundType: ['', []],
    ownerSiren: ['', []],
    tenantSiren: ['', []],
    purchaserSiren: ['', []],
    registrationAuthority: ['', []],
  });

  getFormStatus(): boolean {
    this.bodaccSaleForm.markAllAsTouched();
    return this.bodaccSaleForm.valid;
  }

  checkSiren(fieldName: string): ValidationErrors | null {
    return (control: AbstractControl): ValidationErrors | null => {
      const root = control.root as UntypedFormGroup;

      const fieldValue = root.get(fieldName)?.value;
      if (this.bodaccSale != undefined && (fieldValue == undefined || fieldValue == null || fieldValue.length == 0 || !validateSiren(fieldValue)))
        return {
          notFilled: true
        };
      return null;
    };
  }

  fillSirenOwner(siren: Siren) {
    if (siren != undefined && siren != null) {
      this.bodaccSale.ownerSiren = siren!.uniteLegale.siren;
      if (siren!.uniteLegale.siren != undefined && siren!.uniteLegale.siren != null) {
        if (siren.uniteLegale.periodesUniteLegale != null && siren.uniteLegale.periodesUniteLegale != undefined && siren.uniteLegale.periodesUniteLegale.length > 0) {
          siren.uniteLegale.periodesUniteLegale.forEach(periode => {
            if (periode.dateFin == null) {
              this.bodaccSale.ownerFirstname = periode.denominationUniteLegale;
              this.bodaccSale.ownerDenomination = periode.denominationUniteLegale;
            }
            this.bodaccSaleForm.markAllAsTouched();
          });
        }
      }
    }
  }

  fillSirenTenant(siren: Siren) {
    if (siren != undefined && siren != null) {
      this.bodaccSale.tenantSiren = siren!.uniteLegale.siren;
      if (siren!.uniteLegale.siren != undefined && siren!.uniteLegale.siren != null) {
        if (siren.uniteLegale.periodesUniteLegale != null && siren.uniteLegale.periodesUniteLegale != undefined && siren.uniteLegale.periodesUniteLegale.length > 0) {
          siren.uniteLegale.periodesUniteLegale.forEach(periode => {
            if (periode.dateFin == null) {
              this.bodaccSale.tenantDenomination = periode.denominationUniteLegale;
              this.bodaccSale.tenantFirstname = periode.denominationUniteLegale;
            }
            this.bodaccSaleForm.markAllAsTouched();
          });
        }
      }
    }
  }

  fillSirenPurchaser(siren: Siren) {
    if (siren != undefined && siren != null) {
      this.bodaccSale.purchaserSiren = siren!.uniteLegale.siren;
      if (siren!.uniteLegale.siren != undefined && siren!.uniteLegale.siren != null) {
        if (siren.uniteLegale.periodesUniteLegale != null && siren.uniteLegale.periodesUniteLegale != undefined && siren.uniteLegale.periodesUniteLegale.length > 0) {
          siren.uniteLegale.periodesUniteLegale.forEach(periode => {
            if (periode.dateFin == null) {
              this.bodaccSale.purchaserFirstname = periode.denominationUniteLegale;
              this.bodaccSale.purchaserDenomination = periode.denominationUniteLegale;
            }
            this.bodaccSaleForm.markAllAsTouched();
          });
        }
      }
    }
  }
}
