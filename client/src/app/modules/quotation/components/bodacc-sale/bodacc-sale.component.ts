import { Component, Input, OnInit, SimpleChanges, ViewChild } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MatAccordion } from '@angular/material/expansion';
import { CompetentAuthority } from 'src/app/modules/miscellaneous/model/CompetentAuthority';
import { CompetentAuthorityService } from 'src/app/modules/miscellaneous/services/competent.authority.service';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { ActType } from '../../model/ActType';
import { Affaire } from '../../model/Affaire';
import { BodaccSale } from '../../model/BodaccSale';
import { TransfertFundsType } from '../../model/TransfertFundsType';

@Component({
  selector: 'bodacc-sale',
  templateUrl: './bodacc-sale.component.html',
  styleUrls: ['./bodacc-sale.component.css']
})
export class BodaccSaleComponent implements OnInit {


  @Input() bodaccSale: BodaccSale = {} as BodaccSale;
  @Input() instanceOfCustomerOrder: boolean = false;
  @Input() isStatusOpen: boolean = true;
  @Input() editMode: boolean = false;
  @Input() transfertFundsType: TransfertFundsType = {} as TransfertFundsType;
  @ViewChild(MatAccordion) accordion: MatAccordion | undefined;
  @Input() affaire: Affaire | undefined;

  competentAuthorityTypeCfp = this.constantService.getCompetentAuthorityTypeCfp();

  transfertFundsTypePhysique: TransfertFundsType = this.constantService.getTransfertFundsTypePhysique();
  transfertFundsTypeMoral: TransfertFundsType = this.constantService.getTransfertFundsTypeMoral();
  transfertFundsTypeBail: TransfertFundsType = this.constantService.getTransfertFundsTypeBail();

  actTypeSeing: ActType = this.constantService.getActTypeSeing();
  actTypeAuthentic: ActType = this.constantService.getActTypeAuthentic();

  constructor(private formBuilder: UntypedFormBuilder,
    private competentAuthoritiesService: CompetentAuthorityService,
    private constantService: ConstantService
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
              if (authority.competentAuthorityType.id == this.competentAuthorityTypeCfp.id)
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
  });

  getFormStatus(): boolean {
    this.bodaccSaleForm.markAllAsTouched();
    return this.bodaccSaleForm.valid;
  }
}
