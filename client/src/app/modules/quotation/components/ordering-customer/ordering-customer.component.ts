import { Component, Input, OnInit, SimpleChanges } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { CustomErrorStateMatcher } from 'src/app/app.component';
import { instanceOfQuotation } from 'src/app/libs/TypeHelper';
import { Responsable } from 'src/app/modules/tiers/model/Responsable';
import { Tiers } from 'src/app/modules/tiers/model/Tiers';
import { TiersService } from 'src/app/modules/tiers/services/tiers.service';
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

  overrideSpecialOffer: boolean = false;

  constructor(private formBuilder: UntypedFormBuilder,
    private tiersService: TiersService,
    public specialOfferDialog: MatDialog) { }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.quotation != undefined) {
      this.orderingCustomerForm.markAllAsTouched();
    }
  }

  ngOnInit() {
    // Trigger it to show mandatory fields
    this.orderingCustomerForm.markAllAsTouched();
  }

  orderingCustomerForm = this.formBuilder.group({
    responsable: ['', []],
    tiers: ['', []],
  });

  fillTiers(tiers: Tiers) {
    this.quotation.tiers = tiers;
    this.quotation.responsable = null;
    this.quotation.observations = this.quotation.tiers.observations;
    this.quotation.mails = this.quotation.tiers.mails;
    this.quotation.phones = this.quotation.tiers.phones;
  }

  fillResponsable(responsable: Responsable) {
    this.quotation.responsable = responsable;
    this.tiersService.getTiersByResponsable(responsable.id).subscribe(response => {
      if (this.quotation.responsable != null) {
        this.quotation.responsable.tiers = response;
        this.quotation.observations = this.quotation.responsable.tiers.observations;
      }
    })
    this.quotation.tiers = null;
    this.quotation.mails = this.quotation.responsable.mails;
    this.quotation.phones = this.quotation.responsable.phones;
  }

  getFormStatus(): boolean {
    this.orderingCustomerForm.markAllAsTouched();
    return this.orderingCustomerForm.valid;
  }

  instanceOfQuotation = instanceOfQuotation;

}
