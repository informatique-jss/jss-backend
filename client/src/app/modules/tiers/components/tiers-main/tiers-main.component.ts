import { ChangeDetectionStrategy, Component, Input, OnInit, SimpleChanges } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';
import { CustomErrorStateMatcher } from 'src/app/app.component';
import { isTiersTypeProspect } from 'src/app/libs/CompareHelper';
import { COUNTRY_CODE_FRANCE } from 'src/app/libs/Constants';
import { validateVat } from 'src/app/libs/CustomFormsValidatorsHelper';
import { City } from 'src/app/modules/miscellaneous/model/City';
import { Country } from 'src/app/modules/miscellaneous/model/Country';
import { DeliveryService } from 'src/app/modules/miscellaneous/model/DeliveryService';
import { CityService } from 'src/app/modules/miscellaneous/services/city.service';
import { CountryService } from 'src/app/modules/miscellaneous/services/country.service';
import { DeliveryServiceService } from 'src/app/modules/miscellaneous/services/delivery.service.service';
import { Tiers } from '../../model/Tiers';

@Component({
  selector: 'tiers-main',
  templateUrl: './tiers-main.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrls: ['./tiers-main.component.css']
})

export class PrincipalComponent implements OnInit {
  matcher: CustomErrorStateMatcher = new CustomErrorStateMatcher();
  @Input() tiers: Tiers = {} as Tiers;
  @Input() editMode: boolean = false;

  COUNTRY_CODE_FRANCE = COUNTRY_CODE_FRANCE;

  deliveryServices: DeliveryService[] = [] as Array<DeliveryService>;

  countries: Country[] = [] as Array<Country>;

  constructor(private formBuilder: FormBuilder,
    private deliveryServiceService: DeliveryServiceService,
    private countryService: CountryService,
    private cityService: CityService) { }

  // TODO : reprendre les RG (notamment facturation / commande) lorsque les modules correspondants seront faits

  ngOnChanges(changes: SimpleChanges) {
    if (changes.tiers != undefined) {
      if (this.tiers.deliveryService == null || this.tiers.deliveryService == undefined)
        this.tiers.deliveryService = this.deliveryServices[0];
      if (this.tiers.country == null || this.tiers.country == undefined)
        this.tiers.country = this.countries[0];
      this.principalForm.markAllAsTouched();
    }
  }

  ngOnInit() {
    // Referential loading
    this.deliveryServiceService.getDeliveryServices().subscribe(response => {
      this.deliveryServices = response;
    })
    this.countryService.getCountries().subscribe(response => {
      this.countries = response;
    })

    // Trigger it to show mandatory fields
    this.principalForm.markAllAsTouched();
  }

  principalForm = this.formBuilder.group({
    tiersType: [''],
    tiersCategory: [''],
    deliveryService: [''],
    postalCode: ['',],
    city: ['',],
    responsibleSuscribersNumber: [{ value: '', disabled: true }, []],
    webAccountNumber: [{ value: '', disabled: true }, []],
  });

  checkVAT(fieldName: string): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const root = control.root as FormGroup;

      const fieldValue = root.get(fieldName)?.value;
      if (!this.tiers.isIndividual && !isTiersTypeProspect(this.tiers) && (fieldValue == undefined || fieldValue == null || fieldValue.length == 0 || !validateVat(fieldValue)))
        return {
          notFilled: true
        };
      return null;
    };
  }

  limitTextareaSize(numberOfLine: number) {
    if (this.tiers.mailRecipient != null) {
      var l = this.tiers.mailRecipient.replace(/\r\n/g, "\n").replace(/\r/g, "").split(/\n/g);//split lines
      var outValue = "";
      if (l.length > numberOfLine) {
        outValue = l.slice(0, numberOfLine).join("\n");
        this.tiers.mailRecipient = outValue;
      }
    }
  }

  fillPostalCode(city: City) {
    if (this.tiers.country == null || this.tiers.country == undefined)
      this.tiers.country = city.country;

    if (this.tiers.country.code == COUNTRY_CODE_FRANCE && city.postalCode != null)
      this.tiers.postalCode = city.postalCode;
  }

  fillCity(postalCode: string) {
    this.cityService.getCitiesFilteredByPostalCode(postalCode).subscribe(response => {
      if (response != null && response != undefined && response.length == 1) {
        let city = response[0];
        if (this.tiers.country == null || this.tiers.country == undefined)
          this.tiers.country = city.country;

        this.tiers.city = city;
      }
    })

  }

  getFormStatus(): boolean {
    this.principalForm.markAllAsTouched();

    return this.principalForm.valid;
  }
}
