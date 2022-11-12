import { ChangeDetectionStrategy, Component, Input, OnInit, SimpleChanges } from '@angular/core';
import { AbstractControl, UntypedFormBuilder, UntypedFormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';
import { CustomErrorStateMatcher } from 'src/app/app.component';
import { validateVat } from 'src/app/libs/CustomFormsValidatorsHelper';
import { City } from 'src/app/modules/miscellaneous/model/City';
import { Country } from 'src/app/modules/miscellaneous/model/Country';
import { DeliveryService } from 'src/app/modules/miscellaneous/model/DeliveryService';
import { CityService } from 'src/app/modules/miscellaneous/services/city.service';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { DeliveryServiceService } from 'src/app/modules/miscellaneous/services/delivery.service.service';
import { Tiers } from '../../model/Tiers';
import { TiersType } from '../../model/TiersType';

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

  countryFrance: Country = this.constantService.getCountryFrance();

  deliveryServices: DeliveryService[] = [] as Array<DeliveryService>;
  prospectTiersType: TiersType = this.constantService.getTiersTypeProspect();

  constructor(private formBuilder: UntypedFormBuilder,
    private deliveryServiceService: DeliveryServiceService,
    private constantService: ConstantService,
    private cityService: CityService) { }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.tiers != undefined) {
      if (this.tiers.deliveryService == null || this.tiers.deliveryService == undefined)
        this.tiers.deliveryService = this.deliveryServices[0];
      if (!this.tiers.isProvisionalPaymentMandatory)
        this.tiers.isProvisionalPaymentMandatory = false;
      if (!this.tiers.isSepaMandateReceived)
        this.tiers.isSepaMandateReceived = false;
      this.principalForm.markAllAsTouched();
    }
  }


  ngOnInit() {
    this.prospectTiersType = this.constantService.getTiersTypeProspect();

    // Referential loading
    this.deliveryServiceService.getDeliveryServices().subscribe(response => {
      this.deliveryServices = response;
    })

    // Trigger it to show mandatory fields
    this.principalForm.markAllAsTouched();
  }

  principalForm = this.formBuilder.group({
  });

  isTiersTypeProspect(tiers: Tiers): boolean {
    return tiers && tiers.tiersType && this.constantService.getTiersTypeProspect().id == tiers.tiersType.id;
  }

  checkVAT(fieldName: string): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const root = control.root as UntypedFormGroup;
      const fieldValue = root.get(fieldName)?.value;
      if (!this.tiers.isIndividual && !this.isTiersTypeProspect(this.tiers) && (fieldValue == undefined || fieldValue == null || fieldValue.length == 0 || !validateVat(fieldValue)))
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

    if (this.tiers.country.id == this.countryFrance.id && city.postalCode != null)
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
    console.log(this.principalForm);
    this.principalForm.markAllAsTouched();
    return this.principalForm.valid;
  }
}
