import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { Routes } from '@angular/router';
import { TrustHtmlPipe } from '../../../../libs/TrustHtmlPipe';
import { AvatarComponent } from '../avatar/avatar.component';
import { DoubleButtonsComponent } from '../double-buttons/double-buttons.component';
import { AutocompleteCityComponent } from '../forms/autocomplete-city/autocomplete-city.component';
import { GenericDatetimePickerComponent } from '../forms/generic-datetime-picker/generic-datetime-picker.component';
import { GenericInputComponent } from '../forms/generic-input/generic-input.component';
import { GenericTextareaComponent } from '../forms/generic-textarea/generic-textarea.component';
import { GenericToggleComponent } from '../forms/generic-toggle/generic-toggle.component';
import { RadioGroupAffaireTypeComponent } from '../forms/radio-group-affaire-type/radio-group-affaire-type.component';
import { RadioGroupQuotationTypeComponent } from '../forms/radio-group-quotation-type/radio-group-quotation-type.component';
import { SelectCountryComponent } from '../forms/select-country/select-country.component';
import { SingleUploadComponent } from '../forms/single-upload/single-upload.component';
import { GenericSwiperComponent } from '../generic-swiper/generic-swiper.component';
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
  ],
  declarations: [MiscellaneousComponent,
    AvatarComponent,
    GenericInputComponent,
    SingleUploadComponent,
    TrustHtmlPipe,
    GenericTextareaComponent,
    DoubleButtonsComponent,
    GenericToggleComponent,
    GenericDatetimePickerComponent,
    RadioGroupQuotationTypeComponent,
    RadioGroupAffaireTypeComponent,
    AutocompleteCityComponent,
    SelectCountryComponent,
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
    GenericSwiperComponent,
    RadioGroupQuotationTypeComponent,
    RadioGroupAffaireTypeComponent,
    AutocompleteCityComponent,
    SelectCountryComponent,
    OurClientsComponent,
  ]
})
export class MiscellaneousModule { }
