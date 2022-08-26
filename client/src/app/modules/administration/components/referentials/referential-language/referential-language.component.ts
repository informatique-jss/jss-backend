import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { Language } from 'src/app/modules/miscellaneous/model/Language';
import { LanguageService } from 'src/app/modules/miscellaneous/services/language.service';
import { AppService } from 'src/app/services/app.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-language',
  templateUrl: './../generic-referential/generic-referential.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialLanguageComponent extends GenericReferentialComponent<Language> implements OnInit {
  constructor(private languageService: LanguageService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<Language> {
    return this.languageService.addOrUpdateLanguage(this.selectedEntity!);
  }
  getGetObservable(): Observable<Language[]> {
    return this.languageService.getLanguages();
  }
}
