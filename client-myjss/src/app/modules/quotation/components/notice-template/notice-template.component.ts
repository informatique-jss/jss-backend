import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { GenericInputComponent } from '../../../miscellaneous/components/forms/generic-input/generic-input.component';
import { AssoServiceFieldType } from '../../../my-account/model/AssoServiceFieldType';
import { Service } from '../../../my-account/model/Service';

@Component({
  selector: 'notice-template',
  templateUrl: './notice-template.component.html',
  styleUrls: ['./notice-template.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS,
    GenericInputComponent]
})
export class NoticeTemplateComponent implements OnChanges {

  @Input() template: string = '';
  @Input() service: Service | undefined;

  placeholders: AssoServiceFieldType[] = [];
  displayText: string = '';
  form!: FormGroup;


  constructor(private fb: FormBuilder) { }


  ngOnChanges(changes: SimpleChanges): void {
    if (changes['template']) {
      this.extractPlaceholders();
      this.initForm();
      this.updateDisplay();
    }
  }

  private extractPlaceholders(): void {
    const regex = /\[([^\[*]+) \*\]/g;
    const matches: AssoServiceFieldType[] = [];
    let match;
    let index = 0;
    while ((match = regex.exec(this.template)) !== null) {
      const label = match[1].trim();
      matches.push({ id: index++, serviceFieldType: { label: label } } as AssoServiceFieldType);
    }
    for (let match of matches) {
      if (this.service && this.service.assoServiceFieldTypes && this.service.assoServiceFieldTypes.map((asso) => asso.serviceFieldType.label).includes(match.label)) {
        let assoServiceTypeFound = this.service.assoServiceFieldTypes.find((asso) => asso.serviceFieldType.label == match.label);
        if (assoServiceTypeFound)
          matches.splice(matches.indexOf(match), 1, assoServiceTypeFound);
      }
    }
    this.placeholders = matches;
  }

  private initForm(): void {
    const group: any = {};
    this.placeholders.forEach(ph => {
      group[ph.id] = [''];
    });
    this.form = this.fb.group(group);

    this.form.valueChanges.subscribe(() => {
      this.updateDisplay();
    });
  }

  private updateDisplay(): void {
    let updated = this.template;
    this.placeholders.forEach(ph => {
      const val = this.form.get(ph.serviceFieldTypeid!.value || `[${ph.label} *]`;
      const regex = new RegExp(`\\[${ph.label} \\*\\]`, '');
      updated = updated.replace(regex, val);
    });
    this.displayText = updated.replace(/\n/g, '<br>');
  }
}
