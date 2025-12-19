import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { AppRestService } from '../../main/services/appRest.service';
import { ProvisionDto } from '../model/ProvisionDto';
import { ProvisionSearch } from '../model/ProvisionSearch';

@Injectable({
  providedIn: 'root'
})
export class ProvisionService extends AppRestService<ProvisionDto> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  selectedProvision: ProvisionDto[] = [];
  selectedProvisionUnique: ProvisionDto | undefined;
  selectedProvisionUniqueChange: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);

  getCurrentSelectedProvision() {
    if (this.selectedProvision.length == 0) {
      let toParse = localStorage.getItem("provision-order");
      if (toParse)
        this.selectedProvision = JSON.parse(toParse);
    }
    return this.selectedProvision;
  }

  setCurrentSelectedProvision(provisions: ProvisionDto[]) {
    this.selectedProvision = provisions;
    localStorage.setItem("provision-order", JSON.stringify(provisions));
  }

  getSelectedProvisionUnique() {
    return this.selectedProvisionUnique;
  }

  getSelectedProvisionUniqueChangeEvent() {
    return this.selectedProvisionUniqueChange.asObservable();
  }

  setSelectedProvisionUnique(ProvisionDto: ProvisionDto) {
    this.selectedProvisionUnique = ProvisionDto;
    this.selectedProvisionUniqueChange.next(true);
  }

  searchProvisions(provisionSearch: ProvisionSearch) {
    return this.postList(new HttpParams(), "provision/search/v2", provisionSearch);
  }
}
