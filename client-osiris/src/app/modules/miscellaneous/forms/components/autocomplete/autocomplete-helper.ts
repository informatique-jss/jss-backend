import { Injectable } from "@angular/core";
import { Observable, of } from "rxjs";
import { ConstantService } from "../../../../main/services/constant.service";
import { City } from "../../../../profile/model/City";
import { CityService } from "../../../../profile/services/city.service";
import { AutocompleteType } from "./autocomplete.component";


@Injectable({
  providedIn: 'root'
})
export class AutocompleteHelper {

  constructor(
    private cityService: CityService,
    private constantService: ConstantService,
  ) { }

  selectValues: { [key: string]: any[] } = {};

  getValues(autocompleteType: AutocompleteType, seatchText: string): Observable<any> {
    switch (autocompleteType) {
      case 'city':
        if (this.selectValues[autocompleteType])
          return of(this.selectValues[autocompleteType]);
        return new Observable<City[]>(observer => {
          this.cityService.getCitiesFilteredByNameAndCountryAndPostalCode(seatchText, this.constantService.getCountryFrance(), "", 0, 25).subscribe(response => {
            let foundCities = [];
            if (response)
              for (let city of response.content) {
                foundCities.push(city);
              }
            let values = foundCities.sort((a, b) => a.label.localeCompare(b.label));
            this.selectValues[autocompleteType] = values;
            observer.next(values);
            observer.complete;
          })
        })
      default:
        throw new Error('select type not implemented');
    }
  }
}
