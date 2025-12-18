import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { AppRestService } from '../../main/services/appRest.service';
import { QuotationDto } from '../model/QuotationDto';
import { QuotationSearch } from '../model/QuotationSearch';

@Injectable({
  providedIn: 'root'
})
export class QuotationService extends AppRestService<QuotationDto> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  selectedQuotation: QuotationDto[] = [];
  selectedQuotationUnique: QuotationDto | undefined;
  selectedQuotationUniqueChange: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);

  getCurrentSelectedQuotation() {
    if (this.selectedQuotation.length == 0) {
      let toParse = localStorage.getItem("selected-quotation");
      if (toParse)
        this.selectedQuotation = JSON.parse(toParse);
    }
    return this.selectedQuotation;
  }

  setCurrentSelectedQuotation(quotation: QuotationDto[]) {
    this.selectedQuotation = quotation;
    localStorage.setItem("selected-quotation", JSON.stringify(quotation));
  }

  getSelectedQuotationUnique() {
    return this.selectedQuotationUnique;
  }

  getSelectedQuotationUniqueChangeEvent() {
    return this.selectedQuotationUniqueChange.asObservable();
  }

  setSelectedQuotationUnique(quotationDto: QuotationDto) {
    this.selectedQuotationUnique = quotationDto;
    this.selectedQuotationUniqueChange.next(true);
  }

  searchQuotation(quotationSearch: QuotationSearch) {
    return this.postList(new HttpParams(), "quotation/search/v2", quotationSearch);
  }
}
