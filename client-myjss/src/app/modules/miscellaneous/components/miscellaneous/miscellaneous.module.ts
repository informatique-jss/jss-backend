import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { Routes } from '@angular/router';
import { AutocompleteLibModule } from 'angular-ng-autocomplete';
import { TrustHtmlPipe } from '../../../../libs/TrustHtmlPipe';
import { AvatarComponent } from '../avatar/avatar.component';
import { DoubleButtonsComponent } from '../double-buttons/double-buttons.component';
import { AutocompleteCityComponent } from '../forms/autocomplete-city/autocomplete-city.component';
import { GenericDatePickerComponent } from '../forms/generic-date-picker/generic-datetime-picker.component';
import { GenericDatetimePickerComponent } from '../forms/generic-datetime-picker/generic-datetime-picker.component';
import { GenericInputComponent } from '../forms/generic-input/generic-input.component';
import { GenericTextareaComponent } from '../forms/generic-textarea/generic-textarea.component';
import { GenericToggleComponent } from '../forms/generic-toggle/generic-toggle.component';
import { RadioGroupAffaireTypeComponent } from '../forms/radio-group-affaire-type/radio-group-affaire-type.component';
import { RadioGroupQuotationTypeComponent } from '../forms/radio-group-quotation-type/radio-group-quotation-type.component';
import { SelectBillingLabelTypeComponent } from '../forms/select-billing-label-type/select-billing-label-type.component';
import { SelectCivilityComponent } from '../forms/select-civility/select-civility.component';
import { SelectCountryComponent } from '../forms/select-country/select-country.component';
import { SelectDepartmentComponent } from '../forms/select-department/select-department.component';
import { SelectMultipleNoticeTypeComponent } from '../forms/select-multiple-notice-type/select-multiple-notice-type.component';
import { SelectMyJssCategoryComponent } from '../forms/select-myjss-category/select-myjss-category.component';
import { SelectNoticeTypeFamilyComponent } from '../forms/select-notice-type-family/select-notice-type-family.component';
import { SelectStringComponent } from '../forms/select-string/select-string.component';
import { SelectValueServiceFieldTypeComponent } from '../forms/select-value-service-field-type/select-value-service-field-type.component';
import { SingleUploadComponent } from '../forms/single-upload/single-upload.component';
import { GenericSwiperComponent } from '../generic-swiper/generic-swiper.component';
import { GenericTestimonialComponent } from '../generic-testimonial/generic-testimonial.component';
import { OurClientsComponent } from '../our-clients/our-clients.component';
import { MiscellaneousComponent } from './miscellaneous.component';

const routes: Routes = [{}];

@NgModule({
  imports: [
    CommonModule,
    BrowserModule,
    FormsModule,
    ReactiveFormsModule,
    GenericSwiperComponent,
    OurClientsComponent,
    AutocompleteLibModule,
    GenericTestimonialComponent
  ],
  declarations: [MiscellaneousComponent,
    AvatarComponent,
    SingleUploadComponent,
    TrustHtmlPipe,
    GenericTextareaComponent,
    DoubleButtonsComponent,
    GenericToggleComponent,
    GenericDatetimePickerComponent,
    GenericDatePickerComponent,
    RadioGroupQuotationTypeComponent,
    RadioGroupAffaireTypeComponent,
    SelectCountryComponent,
    SelectValueServiceFieldTypeComponent,
    SelectBillingLabelTypeComponent,
    GenericInputComponent,
    SelectMyJssCategoryComponent,
    SelectCivilityComponent,
    AutocompleteCityComponent,
    SelectDepartmentComponent,
    SelectNoticeTypeFamilyComponent,
    SelectMultipleNoticeTypeComponent,
    SelectStringComponent
  ],
  exports: [
    AvatarComponent,
    GenericInputComponent,
    SingleUploadComponent,
    TrustHtmlPipe,
    GenericTextareaComponent,
    DoubleButtonsComponent,
    GenericToggleComponent,
    GenericDatetimePickerComponent,
    GenericDatePickerComponent,
    GenericSwiperComponent,
    AvatarComponent,
    SingleUploadComponent,
    GenericInputComponent,
    RadioGroupQuotationTypeComponent,
    RadioGroupAffaireTypeComponent,
    SelectCountryComponent,
    SelectMyJssCategoryComponent,
    SelectValueServiceFieldTypeComponent,
    SelectBillingLabelTypeComponent,
    OurClientsComponent,
    SelectCivilityComponent,
    AutocompleteCityComponent,
    SelectDepartmentComponent,
    SelectNoticeTypeFamilyComponent,
    SelectMultipleNoticeTypeComponent,
    SelectStringComponent,
    GenericTestimonialComponent
  ]
})
export class MiscellaneousModule { }
