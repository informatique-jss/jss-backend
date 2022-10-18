import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Language } from '../../../model/Language';
import { LanguageService } from '../../../services/language.service';
import { GenericRadioGroupComponent } from '../generic-radio-group/generic-radio-group.component';

@Component({
  selector: 'radio-group-language',
  templateUrl: './radio-group-language.component.html',
  styleUrls: ['./radio-group-language.component.css']
})
export class RadioGroupLanguageComponent extends GenericRadioGroupComponent<Language> implements OnInit {
  types: Language[] = [] as Array<Language>;

  constructor(
    private formBuild: UntypedFormBuilder, private languageService: LanguageService) {
    super(formBuild);
  }

  initTypes(): void {
    this.languageService.getLanguages().subscribe(response => {
      this.types = response;
    })
  }
}
