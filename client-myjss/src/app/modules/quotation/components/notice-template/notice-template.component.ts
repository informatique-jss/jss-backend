import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { SERVICE_FIELD_TYPE_DATE, SERVICE_FIELD_TYPE_INTEGER, SERVICE_FIELD_TYPE_SELECT, SERVICE_FIELD_TYPE_TEXT, SERVICE_FIELD_TYPE_TEXTAREA } from '../../../../libs/Constants';
import { formatDateFrance } from '../../../../libs/FormatHelper';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { TrustHtmlPipe } from '../../../../libs/TrustHtmlPipe';
import { GenericDatePickerComponent } from '../../../miscellaneous/components/forms/generic-date-picker/generic-datetime-picker.component';
import { GenericInputComponent } from '../../../miscellaneous/components/forms/generic-input/generic-input.component';
import { GenericTextareaComponent } from '../../../miscellaneous/components/forms/generic-textarea/generic-textarea.component';
import { GenericToggleComponent } from '../../../miscellaneous/components/forms/generic-toggle/generic-toggle.component';
import { SelectAnnouncementNoticeTemplateFragmentComponent } from "../../../miscellaneous/components/forms/select-announcement-notice-template-fragment/select-announcement-notice-template-fragment";
import { SelectValueServiceFieldTypeComponent } from '../../../miscellaneous/components/forms/select-value-service-field-type/select-value-service-field-type.component';
import { Service } from '../../../my-account/model/Service';
import { ServiceFieldType } from '../../../my-account/model/ServiceFieldType';
import { AnnouncementNoticeTemplate } from '../../model/AnnouncementNoticeTemplate';
import { AnnouncementNoticeTemplateFragment } from '../../model/AnnouncementNoticeTemplateFragment';
import { ServiceFieldTypeService } from '../../services/service.field.type.service';

@Component({
  selector: 'notice-template',
  templateUrl: './notice-template.component.html',
  styleUrls: ['./notice-template.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS,
    GenericInputComponent,
    TrustHtmlPipe,
    GenericTextareaComponent,
    GenericDatePickerComponent,
    SelectValueServiceFieldTypeComponent,
    GenericToggleComponent,
    SelectAnnouncementNoticeTemplateFragmentComponent]
})
export class NoticeTemplateComponent implements OnInit {

  @Input() service: Service | undefined;
  templates: AnnouncementNoticeTemplate[] = [];
  fragmentsFound: AnnouncementNoticeTemplateFragment[] = [];
  selectedFragments: (AnnouncementNoticeTemplateFragment | undefined)[] = [];

  fragmentSelection: AnnouncementNoticeTemplateFragment[][] = [];
  placeholdersMap = new Map<string, ServiceFieldType[]>();

  displayText: string = '';
  displayTextOriginal: string = '';

  serviceFieldTypes: ServiceFieldType[] = [];
  form!: FormGroup;

  SERVICE_FIELD_TYPE_INTEGER = SERVICE_FIELD_TYPE_INTEGER;
  SERVICE_FIELD_TYPE_TEXT = SERVICE_FIELD_TYPE_TEXT;
  SERVICE_FIELD_TYPE_TEXTAREA = SERVICE_FIELD_TYPE_TEXTAREA;
  SERVICE_FIELD_TYPE_DATE = SERVICE_FIELD_TYPE_DATE;
  SERVICE_FIELD_TYPE_SELECT = SERVICE_FIELD_TYPE_SELECT;

  constructor(
    private fb: FormBuilder,
    private serviceFieldTypesService: ServiceFieldTypeService,
  ) { }

  ngOnInit(): void {
    this.form = this.fb.group({});

    if (this.service) {
      this.templates = this.service?.serviceTypes.map(st => st.announcementNoticeTemplate) ?? [];
      this.fragmentsFound = this.templates.flatMap(template => template.announcementNoticeTemplateFragments);
      this.displayText = this.templates.map(t => t.text).join('');
      this.displayTextOriginal = this.displayText;
    }

    this.serviceFieldTypesService.getServiceFieldTypes().subscribe(serviceFieldTypes => {
      this.serviceFieldTypes = serviceFieldTypes;
      this.createPlaceholdersMap();
      this.extractFragmentSelection(this.displayText);
      this.prepareInitialDisplayText();
    });

    this.form.valueChanges.subscribe(() => {
      console.log(this.form.controls);
      this.updateDisplayText()
    });
  }

  // Map placeholders to their corresponding ServiceFieldType instances
  private createPlaceholdersMap(): void {
    this.placeholdersMap.set("#", this.findPlaceholders(this.displayText));

    for (const fragment of this.fragmentsFound) {
      this.placeholdersMap.set(fragment.code, this.findPlaceholders(fragment.text));
    }
  }

  // Extract usual placeholder tokens from a text and resolve them to known service fields
  private findPlaceholders(text: string): ServiceFieldType[] {
    const regex = /\{([^}]+)\}/g;
    const result: ServiceFieldType[] = [];
    let match;
    let generatedId = 1000;

    while ((match = regex.exec(text)) !== null) {
      const code = match[1].trim();
      const existing = this.serviceFieldTypes.find(serviceFieldType => serviceFieldType.code === code);
      result.push(existing ?? { id: generatedId++, code } as ServiceFieldType);
    }
    return result;
  }

  // Find everything between [] and populare fragmentSelection array
  extractFragmentSelection(text: string) {
    const regex = /\[([^\]]+)\]/g;
    let match;

    while ((match = regex.exec(text)) !== null) {
      const content = match[1];
      const parts = content.split('||').map(part => part.trim());
      let selectionFragmentsFounds: AnnouncementNoticeTemplateFragment[] = [];
      for (let fragmentPart of parts) {
        let fragmentFound = this.fragmentsFound.find(fragment => fragment.code == fragmentPart);
        if (fragmentFound) {
          selectionFragmentsFounds.push(fragmentFound);
        }
      }
      this.fragmentSelection.push(selectionFragmentsFounds);
    }
  }

  // Format and initialize the display text with placeholders converted
  private prepareInitialDisplayText(): void {
    let text = this.displayTextOriginal;

    // Replace main notice placeholders
    text = this.findAndReplacePlaceholders(undefined, text, this.placeholdersMap.get("#") ?? []);

    // Hide fragments in text
    text =

      this.displayTextOriginal = text;
    this.displayText = text;
  }

  // Replaces the placeholders in the display text with the current form values
  private updateDisplayText(): void {
    let text = this.displayTextOriginal;

    // When fragment is selected, write only the selectedFragment so placeholders can then be wrote
    for (let selectedFragment of this.selectedFragments) {
      if (selectedFragment)
        text = text.replace(new RegExp(`\\[[^\\[\\]]*${selectedFragment.code}[^\\[\\]]*\\]`, 'g'), "[" + selectedFragment.code + "]");
    }

    // When fragment is selected, shows the text in the displayText
    for (let selectedFragment of this.selectedFragments) {
      if (selectedFragment) {
        let fragmentFound = this.fragmentsFound.find(fragment => fragment.code == selectedFragment.code);
        if (fragmentFound) {
          text = text.replace(new RegExp(`\\[\\s*${fragmentFound.code}\\s*\\]`), "</br>" + fragmentFound.text + "</br>");
          text = this.findAndReplacePlaceholders(fragmentFound, text, this.placeholdersMap.get(fragmentFound.code) ?? []);
        }
      }
    }

    // Finds placeholders and replace them in text with input value
    for (let [fragmentCode, placeholders] of this.placeholdersMap.entries()) {
      for (let placeholder of placeholders) {
        let controlName = this.buildFormControlName(fragmentCode === '#' ? null : fragmentCode, placeholder);
        let inputValue = this.sanitizeValue(this.form.get(controlName)?.value);
        if (inputValue == "") {
          inputValue = placeholder.label ? placeholder.label : placeholder.code;
          if (fragmentCode != "#") {
            let fragmentFound = this.fragmentsFound.find(fragment => fragment.code == fragmentCode);
            if (fragmentFound)
              inputValue = inputValue + " pour " + fragmentFound.label
          }
        }
        text = text.replace(new RegExp(`\\{\\s*${controlName}\\s*\\}`), inputValue);
      }
    }
    this.displayText = text;
  }

  // Find placeholders formatted as {placeholder} and change them to <b>{*IF PRESENT : fragment.code_*placeholder_id}</b>
  private findAndReplacePlaceholders(fragment: AnnouncementNoticeTemplateFragment | undefined, text: string, fragmentPlaceholders: ServiceFieldType[]) {
    for (const placeholder of fragmentPlaceholders) {
      text = text.replace(new RegExp(`\\{\\s*${placeholder.code}\\s*\\}`), `<b>{${this.buildFormControlName(fragment ? fragment.code : null, placeholder)}}</b>`);
    }
    return text;
  }

  // Convert dates and structured values into display-friendly format
  private sanitizeValue(value: any): string {
    const dateRegex = /^\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\d|3[01])$/;
    if (typeof value === 'string' && dateRegex.test(value)) {
      return formatDateFrance(new Date(value));
    }
    return value?.value ?? value ?? '';
  }

  // Build a unique name for each form control
  private buildFormControlName(fragmentCode: string | null, placeholder: ServiceFieldType): string {
    return fragmentCode ? `${fragmentCode}_${placeholder.code}_${placeholder.id}` : `${placeholder.code}_${placeholder.id}`;
  }

  changeToggleValue(event: any, index: number) {
    if (event) {
      this.selectedFragments.splice(index, 1, this.fragmentSelection[index][0]);
    } else {
      this.selectedFragments.splice(index, 1, undefined);
    }
  }

  getSectionsFragments(fragmentCodes: string[], selectedIndex: number): AnnouncementNoticeTemplateFragment[] {
    let fragmentsFound: AnnouncementNoticeTemplateFragment[] = [];
    if (!this.selectedFragments[selectedIndex]) {
      for (let fragmentCode of fragmentCodes) {
        let fragmentFound = this.fragmentsFound.find(fragment => fragment.code == fragmentCode);
        if (fragmentFound)
          fragmentsFound.push(fragmentFound);
      }
    }
    return fragmentsFound;
  }

  getArrayInString(array: any[]): string[] {
    return array.map(e => e.label);
  }

  getFragmentsCodes(fragments: AnnouncementNoticeTemplateFragment[]): string[] {
    return fragments.map(e => e.label);
  }
}