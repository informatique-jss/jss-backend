import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { Language } from '../../../model/Language';
import { ConstantService } from '../../../services/constant.service';
import { LanguageService } from '../../../services/language.service';
import { GenericRadioGroupComponent } from '../generic-radio-group/generic-radio-group.component';

@Component({
  selector: 'radio-group-language',
  templateUrl: '../generic-radio-group/generic-radio-group.component.html',
  styleUrls: ['../generic-radio-group/generic-radio-group.component.css']
})
export class RadioGroupLanguageComponent extends GenericRadioGroupComponent<Language> implements OnInit {
  types: Language[] = [] as Array<Language>;
  @Input() isAutoSelectLanguageFrench = true;

  constructor(
    private formBuild: UntypedFormBuilder,
    private contantService: ConstantService,
    private languageService: LanguageService, private userNoteService2: UserNoteService) {
    super(formBuild, userNoteService2);
  }

  initTypes(): void {
    this.languageService.getLanguages().subscribe(response => {
      this.types = response;
      if (this.types && this.isAutoSelectLanguageFrench)
        for (let type of this.types)
          if (this.contantService.getLanguageFrench().id == type.id) {
            this.model = type;
            this.modelChange.emit();
            this.form!.get(this.propertyName)?.setValue(this.model);
          }
    })
  }
}
