import { Component, Input, OnInit, SimpleChanges } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, ValidationErrors } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { debounceTime, distinctUntilChanged, filter, map, startWith, switchMap, tap } from 'rxjs/operators';
import { CustomErrorStateMatcher } from 'src/app/app.component';
import { SEPARATOR_KEY_CODES } from 'src/app/libs/Constants';
import { callNumber, prepareMail } from 'src/app/libs/MailHelper';
import { SpecialOffersDialogComponent } from 'src/app/modules/miscellaneous/components/special-offers-dialog/special-offers-dialog.component';
import { SpecialOfferService } from 'src/app/modules/miscellaneous/services/special-offer.service';
import { IQuotation } from '../../model/IQuotation';
import { SpecialOffer } from 'src/app/modules/miscellaneous/model/SpecialOffer';
import { Tiers } from 'src/app/modules/tiers/model/Tiers';
import { TiersService } from 'src/app/modules/tiers/services/tiers.service';
import { ITiers } from 'src/app/modules/tiers/model/ITiers';
import { Responsable } from 'src/app/modules/tiers/model/Responsable';
import { ResponsableService } from 'src/app/modules/tiers/services/responsable.service';
import { Mail } from 'src/app/modules/miscellaneous/model/Mail';
import { Phone } from 'src/app/modules/miscellaneous/model/Phone';
import { instanceOfQuotation } from 'src/app/libs/TypeHelper';

@Component({
  selector: 'ordering-customer',
  templateUrl: './ordering-customer.component.html',
  styleUrls: ['./ordering-customer.component.css']
})
export class OrderingCustomerComponent implements OnInit {

  matcher: CustomErrorStateMatcher = new CustomErrorStateMatcher();
  @Input() quotation: IQuotation = {} as IQuotation;
  @Input() editMode: boolean = false;

  SEPARATOR_KEY_CODES = SEPARATOR_KEY_CODES;

  specialOffers: SpecialOffer[] = [] as Array<SpecialOffer>;
  filteredSpecialOffers: Observable<SpecialOffer[]> | undefined;

  filteredTiers: Tiers[] = [] as Array<Tiers>;
  filteredResponsable: Responsable[] = [] as Array<Responsable>;

  overrideSpecialOffer: boolean = false;

  constructor(private formBuilder: FormBuilder,
    private specialOfferService: SpecialOfferService,
    private tiersService: TiersService,
    private responsableService: ResponsableService,
    public specialOfferDialog: MatDialog) { }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.quotation != undefined) {
      this.orderingCustomerForm.markAllAsTouched();
    }
  }

  ngOnInit() {
    // Referential loading
    this.specialOfferService.getSpecialOffers().subscribe(response => {
      this.specialOffers = response;
    })

    // Initialize autocomplete fields
    this.filteredSpecialOffers = this.orderingCustomerForm.get("specialOffer")?.valueChanges.pipe(
      startWith(''),
      map(value => (typeof value === 'string') ? this._filterByCode(this.specialOffers, value) : [])
    );

    this.orderingCustomerForm.get("tiers")?.valueChanges.pipe(
      filter(res => {
        return res != undefined && res !== null && res.length >= 2
      }),
      distinctUntilChanged(),
      debounceTime(300),
      tap(() => {
        this.filteredTiers = [];
      }),
      switchMap(value => this.tiersService.getIndividualTiersByKeyword(value)
      )
    ).subscribe(response => {
      this.filteredTiers = response as Tiers[];
    });

    this.orderingCustomerForm.get("responsable")?.valueChanges.pipe(
      filter(res => {
        return res != undefined && res !== null && res.length >= 2
      }),
      distinctUntilChanged(),
      debounceTime(300),
      tap(() => {
        this.filteredResponsable = [];
      }),
      switchMap(value => this.responsableService.getResponsableByKeyword(value)
      )
    ).subscribe(response => {
      this.filteredResponsable = response as Responsable[];
    });


    // Trigger it to show mandatory fields
    this.orderingCustomerForm.markAllAsTouched();
  }

  orderingCustomerForm = this.formBuilder.group({
    specialOffer: ['', [this.checkAutocompleteField("specialOffer")]],
    tiers: ['', [this.checkAutocompleteField("tiers")]],
    responsable: ['', [this.checkAutocompleteField("responsable")]],
    phones: ['', []],
    mails: ['', []],
    description: ['', []],
    obervations: ['', []],
  });

  openSpecialOffersDialog() {
    let dialogSpecialOffer = this.specialOfferDialog.open(SpecialOffersDialogComponent, {
      width: '90%'
    });
    dialogSpecialOffer.afterClosed().subscribe(response => {
      if (response && response != null)
        this.quotation.specialOffer = response;
    });
  }

  checkAutocompleteField(fieldName: string): ValidationErrors | null {
    console.log("toto");
    return (control: AbstractControl): ValidationErrors | null => {
      const root = control.root as FormGroup;

      const fieldValue = root.get(fieldName)?.value;
      if (fieldValue != undefined && fieldValue != null && (fieldValue.id == undefined || fieldValue.id == null))
        return {
          notFilled: true
        };
      return null;
    };
  }

  private _filterByCode(inputList: any, value: string): any {
    const filterValue = (value != undefined && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return inputList.filter((input: any) => input.code != undefined && input.code.toLowerCase().includes(filterValue));
  }

  prepareMail = function (mail: Mail) {
    prepareMail(mail.mail, null, null);
  }

  call = function (phone: Phone) {
    callNumber(phone.phoneNumber);
  }

  displayTiers(tiers: Tiers): string {
    return tiers ? tiers.firstname + " " + tiers.lastname + " (" + tiers.id + ")" : '';
  }

  displayLabel(specialOffer: SpecialOffer): string {
    return specialOffer ? specialOffer.label : '';
  }

  displayResponsable(responsable: Responsable): string {
    return responsable ? responsable.firstname + " " + responsable.lastname + " (" + responsable.id + ")" : '';
  }

  fillTiers(tiers: Tiers) {
    this.quotation.tiers = tiers;
    this.quotation.responsable = null;
    this.quotation.observations = this.quotation.tiers.observations;
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
  }

  getFormStatus(): boolean {
    this.orderingCustomerForm.markAllAsTouched();
    console.log(this.orderingCustomerForm);
    return this.orderingCustomerForm.valid;
  }

  instanceOfQuotation = instanceOfQuotation;

}
