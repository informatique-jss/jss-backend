import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { AppRestService } from '../../main/services/appRest.service';
import { PagedContent } from '../../main/services/PagedContent';
import { ResponsableDto } from '../../tiers/model/ResponsableDto';
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

  getQuotation(id: number) {
    return this.getById("responsable-dto", id) as any as Observable<ResponsableDto>;
  }

  getQuotations(value: string): Observable<PagedContent<QuotationDto>> {
    return this.getPagedList(new HttpParams().set("searchedValue", value), "quotation/search");
  }

  searchQuotation(quotationSearch: QuotationSearch) {
    return this.postList(new HttpParams(), "quotation/search/v2", quotationSearch);
  }
}
