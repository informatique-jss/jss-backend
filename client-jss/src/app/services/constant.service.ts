import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Constant } from '../libs/Constant';
import { AppRestService } from './appRest.service';

@Injectable({
  providedIn: 'root'
})
export class ConstantService extends AppRestService<Constant> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  constant: Constant = {} as Constant;

  getConstants() {
    return this.get(new HttpParams(), "constants");
  }

  initConstant() {
    if (localStorage.getItem('constants') != null) {
      let a = localStorage.getItem('constants');
      this.constant = JSON.parse(a!) as Constant;
    }

    this.getConstants().subscribe(response => {
      this.constant = response;
      localStorage.setItem('constants', JSON.stringify(this.constant));
    });
  }

  getJssCategoryHomepageFirstHighlighted() {
    return this.constant.jssCategoryHomepageFirstHighlighted;
  }

  getJssCategoryHomepageSecondHighlighted() {
    return this.constant.jssCategoryHomepageSecondHighlighted;
  }

  getJssCategoryHomepageThirdHighlighted() {
    return this.constant.jssCategoryHomepageThirdHighlighted;
  }

  getPublishingDepartmentIdf() {
    return this.constant.publishingDepartmentIdf;
  }

  getCategoryInterview() {
    return this.constant.categoryInterview;
  }

  getCategoryPodcast() {
    return this.constant.categoryPodcast;
  }

  getCategoryArticle() {
    return this.constant.categoryArticle;
  }

  getCategorySerie() {
    return this.constant.categorySerie;
  }

  getCategoryExclusivity() {
    return this.constant.categoryExclusivity;
  }

}
