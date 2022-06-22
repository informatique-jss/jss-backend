import { Component, Input, OnInit, SimpleChanges, ViewChild } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { CustomErrorStateMatcher } from 'src/app/app.component';
import { BODACC_PUBLICATION_TYPE_ESTATE_REPRESENTATIVE_DESIGNATION, BODACC_PUBLICATION_TYPE_MERGING, BODACC_PUBLICATION_TYPE_POSSESSION_DISPATCH, BODACC_PUBLICATION_TYPE_SALE_OF_BUSINESS, BODACC_PUBLICATION_TYPE_SPLIT, COMPETENT_AUTHORITY_TYPE_RCS_CODE, PAYMENT_TYPE_VIREMENT } from 'src/app/libs/Constants';
import { BODACC_ENTITY_TYPE } from 'src/app/routing/search/search.component';
import { Bodacc } from '../../model/Bodacc';
import { BodaccFusion } from '../../model/BodaccFusion';
import { BodaccSale } from '../../model/BodaccSale';
import { BodaccSplit } from '../../model/BodaccSplit';
import { Provision } from '../../model/Provision';
import { BodaccFusionComponent } from '../bodacc-fusion/bodacc-fusion.component';
import { BodaccSaleComponent } from '../bodacc-sale/bodacc-sale.component';
import { BodaccSplitComponent } from '../bodacc-split/bodacc-split.component';

@Component({
  selector: 'bodacc-main',
  templateUrl: './bodacc-main.component.html',
  styleUrls: ['./bodacc-main.component.css']
})
export class BodaccMainComponent implements OnInit {

  matcher: CustomErrorStateMatcher = new CustomErrorStateMatcher();
  @Input() provision: Provision = {} as Provision;
  @Input() editMode: boolean = false;
  @ViewChild(BodaccSaleComponent) bodaccSaleComponent: BodaccSaleComponent | undefined;
  @ViewChild(BodaccFusionComponent) bodaccFusionComponent: BodaccFusionComponent | undefined;
  @ViewChild(BodaccSplitComponent) bodaccSplitComponent: BodaccSplitComponent | undefined;

  @ViewChild('tabs', { static: false }) tabs: any;

  BODACC_ENTITY_TYPE = BODACC_ENTITY_TYPE;
  BODACC_PUBLICATION_TYPE_SALE_OF_BUSINESS = BODACC_PUBLICATION_TYPE_SALE_OF_BUSINESS;
  BODACC_PUBLICATION_TYPE_SPLIT = BODACC_PUBLICATION_TYPE_SPLIT;
  BODACC_PUBLICATION_TYPE_MERGING = BODACC_PUBLICATION_TYPE_MERGING;
  BODACC_PUBLICATION_TYPE_POSSESSION_DISPATCH = BODACC_PUBLICATION_TYPE_POSSESSION_DISPATCH;
  BODACC_PUBLICATION_TYPE_ESTATE_REPRESENTATIVE_DESIGNATION = BODACC_PUBLICATION_TYPE_ESTATE_REPRESENTATIVE_DESIGNATION;
  PAYMENT_TYPE_VIREMENT = PAYMENT_TYPE_VIREMENT;
  COMPETENT_AUTHORITY_TYPE_RCS_CODE = COMPETENT_AUTHORITY_TYPE_RCS_CODE;

  constructor(private formBuilder: UntypedFormBuilder,
  ) { }

  ngOnInit() {

  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.provision != undefined) {
      if (this.provision.bodacc! == undefined || this.provision.bodacc! == null)
        this.provision.bodacc! = {} as Bodacc;

      if (!this.provision.bodacc.isDoublePublication)
        this.provision.bodacc.isDoublePublication = false;

      this.bodaccForm.markAllAsTouched();
      this.toggleTabs();
    }
  }

  changePublicationType() {
    if (this.provision.bodacc?.bodaccPublicationType &&
      this.provision.bodacc?.bodaccPublicationType.code == BODACC_PUBLICATION_TYPE_SALE_OF_BUSINESS
      && !this.provision.bodacc.bodaccSale)
      this.provision.bodacc.bodaccSale = {} as BodaccSale;

    if (this.provision.bodacc?.bodaccPublicationType &&
      this.provision.bodacc?.bodaccPublicationType.code == BODACC_PUBLICATION_TYPE_MERGING
      && !this.provision.bodacc.bodaccFusion)
      this.provision.bodacc.bodaccFusion = {} as BodaccFusion;

    if (this.provision.bodacc?.bodaccPublicationType &&
      this.provision.bodacc?.bodaccPublicationType.code == BODACC_PUBLICATION_TYPE_SPLIT
      && !this.provision.bodacc.bodaccSplit)
      this.provision.bodacc.bodaccSplit = {} as BodaccSplit;
  }

  bodaccForm = this.formBuilder.group({
    bodaccPublicationType: ['', []],
    competentAuthority: ['', []],
    paymentType: ['', []],
  });

  getFormStatus(): boolean {
    let status = true;

    if (this.bodaccSaleComponent)
      status = status && this.bodaccSaleComponent.getFormStatus();

    if (this.bodaccFusionComponent)
      status = status && this.bodaccFusionComponent.getFormStatus();

    if (this.bodaccSplitComponent)
      status = status && this.bodaccSplitComponent.getFormStatus();

    this.bodaccForm.markAllAsTouched();
    return status && this.bodaccForm.valid;
  }

  toggleTabs() {
    if (this.tabs != undefined)
      this.tabs.realignInkBar();
  }

}
