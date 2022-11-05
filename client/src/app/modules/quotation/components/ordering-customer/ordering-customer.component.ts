import { ChangeDetectorRef, Component, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { CustomErrorStateMatcher } from 'src/app/app.component';
import { instanceOfCustomerOrder, instanceOfQuotation } from 'src/app/libs/TypeHelper';
import { SpecialOffer } from 'src/app/modules/miscellaneous/model/SpecialOffer';
import { DocumentTypeService } from 'src/app/modules/miscellaneous/services/document.type.service';
import { Responsable } from 'src/app/modules/tiers/model/Responsable';
import { Tiers } from 'src/app/modules/tiers/model/Tiers';
import { TiersService } from 'src/app/modules/tiers/services/tiers.service';
import { Document } from '../../../miscellaneous/model/Document';
import { DocumentType } from '../../../miscellaneous/model/DocumentType';
import { Confrere } from '../../model/Confrere';
import { IQuotation } from '../../model/IQuotation';

@Component({
  selector: 'ordering-customer',
  templateUrl: './ordering-customer.component.html',
  styleUrls: ['./ordering-customer.component.css']
})
export class OrderingCustomerComponent implements OnInit {

  matcher: CustomErrorStateMatcher = new CustomErrorStateMatcher();
  @Input() quotation: IQuotation = {} as IQuotation;
  @Input() editMode: boolean = false;
  @Output() updateDocuments: EventEmitter<void> = new EventEmitter<void>();

  documentTypes: DocumentType[] = [] as Array<DocumentType>;
  devisDocument: Document = {} as Document;
  billingDocument: Document = {} as Document;

  constructor(private formBuilder: UntypedFormBuilder,
    private tiersService: TiersService,
    protected cd: ChangeDetectorRef,
    protected documentTypeService: DocumentTypeService,
    public specialOfferDialog: MatDialog) { }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.quotation != undefined) {
      if (!this.quotation.overrideSpecialOffer) {
        this.quotation.overrideSpecialOffer = false;
        this.initSpecialOffers();
      }
      this.orderingCustomerForm.markAllAsTouched();
    }
  }

  ngOnInit() {
    this.orderingCustomerForm.markAllAsTouched();
  }

  orderingCustomerForm = this.formBuilder.group({
  });

  displayOverrideSpecialOffers() {
    this.quotation.overrideSpecialOffer = true;
  }

  initSpecialOffers() {
    this.quotation.specialOffers = [] as Array<SpecialOffer>;
    if (this.quotation.tiers && this.quotation.tiers.specialOffers)
      this.quotation.specialOffers.push(...this.quotation.tiers.specialOffers);

    if (this.quotation.confrere && this.quotation.confrere.specialOffers)
      this.quotation.specialOffers.push(...this.quotation.confrere.specialOffers);

    if (this.quotation.responsable && this.quotation.responsable.tiers && this.quotation.responsable.tiers.specialOffers)
      this.quotation.specialOffers.push(...this.quotation.responsable.tiers.specialOffers);
  }


  fillTiers(tiers: Tiers) {
    this.quotation.tiers = tiers;
    this.quotation.responsable = undefined;
    this.quotation.responsable = undefined;
    if (this.quotation.tiers) {
      this.quotation.observations = this.quotation.tiers.observations;
      this.quotation.mails = this.quotation.tiers.mails;
      this.quotation.phones = this.quotation.tiers.phones;
    }
    this.setDocument();
  }

  fillConfrere(confrere: Confrere) {
    this.quotation.confrere = confrere;
    this.quotation.tiers = undefined;
    this.quotation.responsable = undefined;
    if (this.quotation.confrere) {
      this.quotation.observations = this.quotation.confrere.observations;
      this.quotation.mails = this.quotation.confrere.mails;
      this.quotation.phones = this.quotation.confrere.phones;
    }
    this.setDocument();
  }

  fillResponsable(responsable: Responsable) {
    this.quotation.responsable = responsable;
    this.tiersService.getTiersByResponsable(responsable.id).subscribe(response => {
      if (this.quotation.responsable != null) {
        this.quotation.responsable.tiers = response;
        this.quotation.observations = this.quotation.responsable.tiers.observations;
      }
    })
    this.quotation.tiers = undefined;
    this.quotation.mails = this.quotation.responsable.mails;
    this.quotation.phones = this.quotation.responsable.phones;
    this.setDocument();
  }

  getFormStatus(): boolean {
    console.log(this.orderingCustomerForm);
    this.orderingCustomerForm.markAllAsTouched();
    return this.orderingCustomerForm.valid && (this.quotation.responsable != undefined || this.quotation.tiers != undefined || this.quotation.confrere != undefined);
  }

  instanceOfQuotation = instanceOfQuotation;
  instanceOfCustomerOrder = instanceOfCustomerOrder;

  setDocument() {
    this.updateDocuments.emit();
  }

}
