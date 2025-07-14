import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { SHARED_IMPORTS } from '../../../../../libs/SharedImports';
import { ConstantService } from '../../../../main/services/constant.service';
import { Language } from '../../../../quotation/model/Language';
import { LanguageService } from '../../../../quotation/services/language.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-language',
  templateUrl: '../generic-select/generic-select.component.html',
  styleUrls: ['../generic-select/generic-select.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS]
})
export class SelectLanguageComponent extends GenericSelectComponent<Language> implements OnInit {

  @Input() types: Language[] = [] as Array<Language>;
  @Input() isAutoSelectLanguageFrench = true;

  constructor(private formBuild: UntypedFormBuilder,
    private languageService: LanguageService,
    private constantService: ConstantService) {
    super(formBuild)
  }

  initTypes(): void {
    this.languageService.getLanguages().subscribe(response => {
      this.types = response;
      if (this.types && this.isAutoSelectLanguageFrench)
        for (let type of this.types)
          if (this.constantService.getLanguageFrench().id == type.id) {
            this.model = type;
            this.modelChange.emit();
            this.form!.get(this.propertyName)?.setValue(this.model);
          }
    })
  }
}
