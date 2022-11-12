import { Component, Input, OnInit } from '@angular/core';
import { AbstractControl, UntypedFormBuilder, UntypedFormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';
import { Observable } from 'rxjs';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { City } from '../../../model/City';
import { CityService } from '../../../services/city.service';
import { GenericAutocompleteComponent } from '../generic-autocomplete/generic-autocomplete.component';

@Component({
  selector: 'autocomplete-postal-code',
  templateUrl: '../generic-autocomplete/generic-autocomplete.component.html',
  styleUrls: ['../generic-autocomplete/generic-autocomplete.component.css']
})
export class AutocompletePostalCodeComponent extends GenericAutocompleteComponent<string, City> implements OnInit {

  constructor(private formBuild: UntypedFormBuilder, private cityService: CityService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  @Input() byPassAutocompletValidator: boolean = true;

  searchEntities(value: string): Observable<City[]> {
    return this.cityService.getCitiesFilteredByPostalCode(value);
  }

  mapResponse(response: City[]): string[] {
    return [...new Set(response.map(city => city.postalCode))]
  }
  displayLabel(object: any): string {
    return object;
  }

  checkAutocompleteField(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const root = control.root as UntypedFormGroup;
      const fieldValue = root.get(this.propertyName)?.value;
      if (this.form && this.form!.get(this.propertyName)) {
        if (this.conditionnalRequired != undefined) {
          if (this.conditionnalRequired && (!fieldValue || this.byPassAutocompletValidator == false && fieldValue.id == null)) {
            this.form!.get(this.propertyName)!.setErrors({ notFilled: this.propertyName });
            return {
              notFilled: this.propertyName
            };
          }
        } else if (this.isMandatory && (!fieldValue || this.byPassAutocompletValidator == false && fieldValue.id == null)) {
          this.form!.get(this.propertyName)!.setErrors({ notFilled: this.propertyName });
          return {
            notFilled: this.propertyName
          };
        }
        this.form!.get(this.propertyName)!.setErrors(null);
      }
      return null;
    };
  }
}
