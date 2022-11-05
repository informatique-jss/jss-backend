import { ChangeDetectionStrategy, Component, ElementRef, Input, OnInit, ViewChild } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import { debounceTime, distinctUntilChanged, filter, switchMap, tap } from 'rxjs/operators';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { City } from '../../../model/City';
import { Country } from '../../../model/Country';
import { CityService } from '../../../services/city.service';
import { GenericChipsComponent } from '../generic-chips/generic-chips.component';

@Component({
  selector: 'chips-city',
  templateUrl: './chips-city.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrls: ['./chips-city.component.css']
})
export class ChipsCityComponent extends GenericChipsComponent<City> implements OnInit {

  citys: City[] = [] as Array<City>;
  filteredTypes: City[] | undefined;
  @ViewChild('cityInput') cityInput: ElementRef<HTMLInputElement> | undefined;

  /**
   * The model of country property.
   * If undefined, cities are searched worldwide
   */
  @Input() modelCountry: Country | undefined;

  constructor(private formBuild: UntypedFormBuilder, private cityService: CityService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  callOnNgInit(): void {
    if (this.form)
      this.form.get(this.propertyName)?.valueChanges.pipe(
        filter(res => {
          return res != undefined && res !== null && res.length >= 2
        }),
        distinctUntilChanged(),
        debounceTime(300),
        tap((value) => {
          this.filteredTypes = [];
          this.modelChange.emit(this.model);
        }),
        switchMap(value => this.cityService.getCitiesFilteredByCountryAndName(value, this.modelCountry)
        )
      ).subscribe(response => {
        this.filteredTypes = response;
      });
  }


  validateInput(value: string): boolean {
    return true;
  }

  setValueToObject(value: string, object: City): City {
    return object;
  }

  getValueFromObject(object: City): string {
    return object.label;
  }

  private _filterByName(inputList: any, value: string): any {
    const filterValue = (value != undefined && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return inputList.filter((input: any) => input.label != undefined && input.label.toLowerCase().includes(filterValue));
  }

  addCity(event: MatAutocompleteSelectedEvent): void {
    if (this.form != undefined) {
      if (!this.model)
        this.model = [] as Array<City>;
      // Do not add twice
      if (this.model.map(city => city.id).indexOf(event.option.value.id) >= 0)
        return;
      if (event.option && event.option.value && event.option.value.id)
        this.model.push(event.option.value);
      this.modelChange.emit(this.model);
      this.form.get(this.propertyName)?.setValue(null);
      this.cityInput!.nativeElement.value = '';
    }
  }
}
