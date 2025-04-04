import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { CKEditorModule } from '@ckeditor/ckeditor5-angular';
import { MiscellaneousModule } from '../../../miscellaneous/components/miscellaneous/miscellaneous.module';
import { IdentificationComponent } from '../identification/identification.component';
import { RequiredInformationComponent } from '../required-information/required-information.component';
import { ServicesSelectionComponent } from '../services-selection/services-selection.component';
import { QuotationComponent } from './quotation.component';

const routes: Routes = [
  {
    path: 'quotation', component: QuotationComponent,
    children: [
      { path: 'identification', component: IdentificationComponent },
      { path: 'services-selection', component: ServicesSelectionComponent },
      { path: 'required-information', component: RequiredInformationComponent },
    ]
  },
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload' }),
    CommonModule,
    MiscellaneousModule,
    FormsModule,
    ReactiveFormsModule,
    CKEditorModule
  ],
  declarations: [QuotationComponent,
    IdentificationComponent,
    ServicesSelectionComponent,
    RequiredInformationComponent,
  ]
})
export class QuotationModule { }

