import { Component, Input, OnInit, SimpleChanges, ViewChild } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { CustomErrorStateMatcher } from 'src/app/app.component';
import { BODACC_PUBLICATION_TYPE_ESTATE_REPRESENTATIVE_DESIGNATION, BODACC_PUBLICATION_TYPE_MERGING, BODACC_PUBLICATION_TYPE_POSSESSION_DISPATCH, BODACC_PUBLICATION_TYPE_SALE_OF_BUSINESS, BODACC_PUBLICATION_TYPE_SPLIT, PAYMENT_TYPE_VIREMENT } from 'src/app/libs/Constants';
import { BODACC_ENTITY_TYPE } from 'src/app/routing/search/search.component';
import { Bodacc } from '../../model/Bodacc';
import { Provision } from '../../model/Provision';

@Component({
  selector: 'bodacc-main',
  templateUrl: './bodacc-main.component.html',
  styleUrls: ['./bodacc-main.component.css']
})
export class BodaccMainComponent implements OnInit {

  matcher: CustomErrorStateMatcher = new CustomErrorStateMatcher();
  @Input() provision: Provision = {} as Provision;
  @Input() editMode: boolean = false;

  @ViewChild('tabs', { static: false }) tabs: any;

  BODACC_ENTITY_TYPE = BODACC_ENTITY_TYPE;
  BODACC_PUBLICATION_TYPE_SALE_OF_BUSINESS = BODACC_PUBLICATION_TYPE_SALE_OF_BUSINESS;
  BODACC_PUBLICATION_TYPE_SPLIT = BODACC_PUBLICATION_TYPE_SPLIT;
  BODACC_PUBLICATION_TYPE_MERGING = BODACC_PUBLICATION_TYPE_MERGING;
  BODACC_PUBLICATION_TYPE_POSSESSION_DISPATCH = BODACC_PUBLICATION_TYPE_POSSESSION_DISPATCH;
  BODACC_PUBLICATION_TYPE_ESTATE_REPRESENTATIVE_DESIGNATION = BODACC_PUBLICATION_TYPE_ESTATE_REPRESENTATIVE_DESIGNATION;
  PAYMENT_TYPE_VIREMENT = PAYMENT_TYPE_VIREMENT;

  constructor(private formBuilder: UntypedFormBuilder,
  ) { }

  ngOnInit() {

  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.provision != undefined) {
      if (this.provision.bodacc! == undefined || this.provision.bodacc! == null)
        this.provision.bodacc! = {} as Bodacc;

      this.bodaccForm.markAllAsTouched();
      this.toggleTabs();
    }
  }

  bodaccForm = this.formBuilder.group({
    bodaccPublicationType: ['', []],
    competentAuthority: ['', []],
    paymentType: ['', []],
  });

  getFormStatus(): boolean {
    this.bodaccForm.markAllAsTouched();
    return this.bodaccForm.valid;
  }

  toggleTabs() {
    if (this.tabs != undefined)
      this.tabs.realignInkBar();
  }

}
