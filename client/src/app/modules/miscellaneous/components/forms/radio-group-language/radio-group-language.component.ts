import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { UserNoteService } from 'src/app/services/user.notes.service';
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
    private formBuild: UntypedFormBuilder, private languageService: LanguageService, private userNoteService2: UserNoteService) {
    super(formBuild, userNoteService2);
  }

  initTypes(): void {
    this.languageService.getLanguages().subscribe(response => {
      this.types = response;
      if (this.model == null || this.model.id == null) {
        this.model = this.types[0];
        this.modelChange.emit(this.model);
      }
    })
  }
}
