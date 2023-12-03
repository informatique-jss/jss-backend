import { AfterContentChecked, ChangeDetectorRef, Component, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import { AbstractControl, FormBuilder, UntypedFormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';
import { validateVat } from 'src/app/libs/CustomFormsValidatorsHelper';
import { City } from 'src/app/modules/miscellaneous/model/City';
import { CityService } from 'src/app/modules/miscellaneous/services/city.service';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { Affaire } from 'src/app/modules/quotation/model/Affaire';

@Component({
  selector: 'add-affaire',
  templateUrl: './add-affaire.component.html',
  styleUrls: ['./add-affaire.component.css']
})
export class AddAffaireComponent implements OnInit, AfterContentChecked {

  @Input() affaire: Affaire = {} as Affaire;
  @Output() affaireChange: EventEmitter<Affaire> = new EventEmitter<Affaire>();
  @Input() editMode: boolean = false;
  @Input() isLabelAffaire: boolean = false;

  legalFormUnregistered = this.constantService.getLegalFormUnregistered();

  constructor(private formBuilder: FormBuilder,
    private cityService: CityService,
    private constantService: ConstantService,
    private changeDetectorRef: ChangeDetectorRef
  ) { }

  ngOnInit() {
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes && changes.affaire && !this.affaire.isUnregistered) {
      this.affaire.isUnregistered = false;
    }
  }

  ngAfterContentChecked(): void {
    this.changeDetectorRef.detectChanges();
  }

  affaireForm = this.formBuilder.group({});

  fillPostalCode(city: City) {
    if (this.affaire.country == null || this.affaire.country == undefined)
      this.affaire.country = city.country;

    if (this.affaire.country.id == this.constantService.getCountryFrance().id && city.postalCode != null && !this.affaire.postalCode)
      this.affaire.postalCode = city.postalCode;
  }

  fillCity(postalCode: string) {
    this.cityService.getCitiesFilteredByPostalCode(postalCode).subscribe(response => {
      if (response != null && response != undefined && response.length == 1) {
        let city = response[0];
        if (this.affaire.country == null || this.affaire.country == undefined)
          this.affaire.country = city.country;

        this.affaire.city = city;
      }
    })
  }

  fillAffaire(affaire: Affaire) {
    this.affaire = affaire;
  }

  getFormStatus(): boolean {
    this.affaireForm.markAllAsTouched();
    return this.affaireForm.valid;
  }

  checkVAT(fieldName: string): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const root = control.root as UntypedFormGroup;
      const fieldValue = root.get(fieldName)?.value;
      if (!this.affaire.isIndividual && (fieldValue == undefined || fieldValue == null || fieldValue.length == 0 || !validateVat(fieldValue)))
        return {
          notFilled: true
        };
      return null;
    };
  }
}
