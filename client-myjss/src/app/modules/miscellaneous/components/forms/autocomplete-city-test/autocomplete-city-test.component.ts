import { Component, Input, OnInit } from '@angular/core';
import { ConstantService } from '../../../../../libs/constant.service';
import { City } from '../../../../profile/model/City';
import { Country } from '../../../../profile/model/Country';
import { CityService } from '../../../../quotation/services/city.service';

@Component({
  selector: 'autocomplete-city-test',
  templateUrl: './autocomplete-city-test.component.html',
  standalone: false
})
export class AutocompleteCityTestComponent implements OnInit {

  @Input() modelCountry: Country | undefined;

  @Input() preFilterPostalCode: string = "";

  keyword = 'label';
  data: City[] | undefined;

  page: number = 0;
  pageSize: number = 10;

  constructor(
    private cityService: CityService,
    private constantService: ConstantService
  ) { }

  ngOnInit() {
    if (this.modelCountry)
      this.cityService.getCitiesFilteredByNameAndCountryAndPostalCode("", this.modelCountry, this.preFilterPostalCode, this.page, this.pageSize).subscribe(res => {
        this.data = res;
      });
  }


  selectEvent(item: any) {
    // do something with selected item
  }

  onChangeSearch(val: string) {
    // fetch remote data from here
    // And reassign the 'data' which is binded to 'data' property.
  }

  onFocused(e: any) {
    // do something when input is focused
  }

  fetchNextPage() {
    this.page++;
    if (this.modelCountry && this.data && this.data.length == 0) {
      this.cityService.getCitiesFilteredByNameAndCountryAndPostalCode("", this.modelCountry, this.preFilterPostalCode, this.page, this.pageSize).subscribe(res => {
        this.data = this.data!.concat(res);
      });
    }
  }


}
