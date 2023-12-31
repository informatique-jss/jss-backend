import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges, ViewChild } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MatTabChangeEvent } from '@angular/material/tabs';
import { instanceOfCustomerOrder } from 'src/app/libs/TypeHelper';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { BODACC_ENTITY_TYPE, PROVISION_ENTITY_TYPE } from 'src/app/routing/search/search.component';
import { UserPreferenceService } from '../../../../services/user.preference.service';
import { ActType } from '../../model/ActType';
import { Affaire } from '../../model/Affaire';
import { Bodacc } from '../../model/Bodacc';
import { BodaccFusion } from '../../model/BodaccFusion';
import { BodaccPublicationType } from '../../model/BodaccPublicationType';
import { BodaccSale } from '../../model/BodaccSale';
import { BodaccSplit } from '../../model/BodaccSplit';
import { IQuotation } from '../../model/IQuotation';
import { Provision } from '../../model/Provision';
import { ActTypeService } from '../../services/act-type.service';
import { BodaccFusionComponent } from '../bodacc-fusion/bodacc-fusion.component';
import { BodaccSaleComponent } from '../bodacc-sale/bodacc-sale.component';
import { BodaccSplitComponent } from '../bodacc-split/bodacc-split.component';

@Component({
  selector: 'bodacc-main',
  templateUrl: './bodacc-main.component.html',
  styleUrls: ['./bodacc-main.component.css']
})
export class BodaccMainComponent implements OnInit {

  @Input() bodacc: Bodacc = {} as Bodacc;
  @Input() provision: Provision | undefined;;
  @Input() affaire: Affaire = {} as Affaire;
  @Input() editMode: boolean = false;
  @Input() instanceOfCustomerOrder: boolean = false;
  @Input() isStatusOpen: boolean = true;
  @Input() quotation: IQuotation | undefined;
  @Output() provisionChange: EventEmitter<Provision> = new EventEmitter<Provision>();
  @ViewChild(BodaccSaleComponent) bodaccSaleComponent: BodaccSaleComponent | undefined;
  @ViewChild(BodaccFusionComponent) bodaccFusionComponent: BodaccFusionComponent | undefined;
  @ViewChild(BodaccSplitComponent) bodaccSplitComponent: BodaccSplitComponent | undefined;

  BODACC_ENTITY_TYPE = BODACC_ENTITY_TYPE;
  PROVISION_ENTITY_TYPE = PROVISION_ENTITY_TYPE;
  instanceOfCustomerOrderFn = instanceOfCustomerOrder;

  bodaccPublicationTypeMerging: BodaccPublicationType = this.constantService.getBodaccPublicationTypeMerging();
  bodaccPublicationTypeSplit: BodaccPublicationType = this.constantService.getBodaccPublicationTypeSplit();
  bodaccPublicationTypePartialSplit: BodaccPublicationType = this.constantService.getBodaccPublicationTypePartialSplit();
  bodaccPublicationTypePossessionDispatch: BodaccPublicationType = this.constantService.getBodaccPublicationTypePossessionDispatch();
  bodaccPublicationTypeEstateRepresentativeDesignation: BodaccPublicationType = this.constantService.getBodaccPublicationTypeEstateRepresentativeDesignation();
  bodaccPublicationTypeSaleOfBusiness: BodaccPublicationType = this.constantService.getBodaccPublicationTypeSaleOfBusiness();

  actTypes: ActType[] = [] as Array<ActType>;

  constructor(private formBuilder: UntypedFormBuilder,
    private constantService: ConstantService,
    private actTypeService: ActTypeService,
    private userPreferenceService: UserPreferenceService
  ) { }

  ngOnInit() {
    this.actTypeService.getActTypes().subscribe(response => {
      this.actTypes = response;
    })
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.bodacc != undefined) {
      if (this.bodacc! == undefined || this.bodacc! == null) {
        this.bodacc! = {} as Bodacc;
        this.bodacc.bodaccSale.actType = this.actTypes[0];
      }
      if (this.bodacc.dateOfPublication)
        this.bodacc.dateOfPublication = new Date(this.bodacc.dateOfPublication);

      this.bodaccForm.markAllAsTouched();
    }
  }

  changePublicationType() {
    if (this.bodacc?.bodaccPublicationType &&
      this.bodacc?.bodaccPublicationType.id == this.bodaccPublicationTypeSaleOfBusiness.id
      && !this.bodacc.bodaccSale) {
      this.bodacc.bodaccSale = {} as BodaccSale;
      this.bodacc.bodaccSale.actType = this.actTypes[0];
    }

    if (this.bodacc?.bodaccPublicationType &&
      this.bodacc?.bodaccPublicationType.id == this.bodaccPublicationTypeMerging.id
      && !this.bodacc.bodaccFusion)
      this.bodacc.bodaccFusion = {} as BodaccFusion;

    if (this.bodacc?.bodaccPublicationType &&
      this.bodacc?.bodaccPublicationType.id == this.bodaccPublicationTypeSplit.id
      && !this.bodacc.bodaccSplit)
      this.bodacc.bodaccSplit = {} as BodaccSplit;
  }

  bodaccForm = this.formBuilder.group({
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

  provisionChangeFunction() {
    this.provisionChange.emit(this.provision);
  }

  //Tabs management
  index: number = 0;
  onTabChange(event: MatTabChangeEvent) {
    this.userPreferenceService.setUserTabsSelectionIndex('bodacc', event.index);
  }

  restoreTab() {
    this.index = this.userPreferenceService.getUserTabsSelectionIndex('bodacc');
  }
}
