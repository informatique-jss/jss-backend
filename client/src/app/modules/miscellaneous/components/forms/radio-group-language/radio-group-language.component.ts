import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { Language } from '../../../model/Language';
import { LanguageService } from '../../../services/language.service';
import { GenericRadioGroupComponent } from '../generic-radio-group/generic-radio-group.component';

@Component({
  selector: 'radio-group-language',
  templateUrl: '../generic-radio-group/generic-radio-group-code.component.html',
  styleUrls: ['../generic-radio-group/generic-radio-group.component.css']
})
export class RadioGroupLanguageComponent extends GenericRadioGroupComponent<Language> implements OnInit {
  types: Language[] = [] as Array<Language>;

  constructor(
    private formBuild: UntypedFormBuilder, private languageService: LanguageService, private userNoteService2: UserNoteService) {
    super(formBuild, userNoteService2);
  }

  initTypes(): void {
    this.languageService.getLanguages().subscribe(response => {
      this.types = response;
    })
  }
}
