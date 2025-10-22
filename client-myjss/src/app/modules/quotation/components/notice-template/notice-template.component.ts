import { Component, Input, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { SERVICE_FIELD_TYPE_DATE, SERVICE_FIELD_TYPE_INTEGER, SERVICE_FIELD_TYPE_SELECT, SERVICE_FIELD_TYPE_TEXT, SERVICE_FIELD_TYPE_TEXTAREA } from '../../../../libs/Constants';
import { formatDateFrance } from '../../../../libs/FormatHelper';
import { copyObject } from '../../../../libs/GenericHelper';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { TrustHtmlPipe } from '../../../../libs/TrustHtmlPipe';
import { GenericDatePickerComponent } from '../../../miscellaneous/components/forms/generic-date-picker/generic-datetime-picker.component';
import { GenericInputComponent } from '../../../miscellaneous/components/forms/generic-input/generic-input.component';
import { GenericTextareaComponent } from '../../../miscellaneous/components/forms/generic-textarea/generic-textarea.component';
import { SelectAnnouncementNoticeTemplateFragmentComponent } from "../../../miscellaneous/components/forms/select-announcement-notice-template-fragment/select-announcement-notice-template-fragment";
import { SelectValueServiceFieldTypeComponent } from '../../../miscellaneous/components/forms/select-value-service-field-type/select-value-service-field-type.component';
import { Service } from '../../../my-account/model/Service';
import { ServiceFieldType } from '../../../my-account/model/ServiceFieldType';
import { AnnouncementNoticeTemplate } from '../../model/AnnouncementNoticeTemplate';
import { AnnouncementNoticeTemplateFragment } from '../../model/AnnouncementNoticeTemplateFragment';
import { NoticeTemplateService } from '../../services/notice.template.service';
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
    SelectAnnouncementNoticeTemplateFragmentComponent]
})
export class NoticeTemplateComponent implements OnInit {

  @Input() service: Service | undefined;

  @ViewChild('previewModal') previewModalView!: TemplateRef<any>;
  previewModalInstance: any | undefined;

  templates: AnnouncementNoticeTemplate[] = [];
  fragmentsFound: AnnouncementNoticeTemplateFragment[] = [];
  selectedFragments: (AnnouncementNoticeTemplateFragment | undefined)[] = [];
  fragmentInstancesMap = new Map<string, AnnouncementNoticeTemplateFragment[]>();
  selectedFragmentCode: string = '';

  fragmentSelection: AnnouncementNoticeTemplateFragment[][] = [];
  placeholdersMap = new Map<string, ServiceFieldType[]>();

  displayText: string = '';
  displayTextOriginal: string = '';
  fragmentSelectionText: string = '';

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
    private noticeTemplateService: NoticeTemplateService,
    public modalService: NgbModal,
  ) { }

  ngOnInit(): void {
    this.form = this.fb.group({});

    this.templates = [];
    if (this.service) {
      if (this.service && this.service.serviceTypes)
        for (let st of this.service.serviceTypes)
          if (st.assoServiceProvisionTypes)
            for (let asso of st.assoServiceProvisionTypes)
              if (asso.announcementNoticeTemplate)
                this.templates.push(asso.announcementNoticeTemplate);

      this.fragmentsFound = this.templates.flatMap(template => template.announcementNoticeTemplateFragments);
      this.displayText = this.templates.map(t => t.text).join('');
      this.displayTextOriginal = this.displayText;
      this.fragmentSelectionText = this.displayText;
    }

    this.serviceFieldTypesService.getServiceFieldTypes().subscribe(serviceFieldTypes => {
      this.serviceFieldTypes = serviceFieldTypes;
      this.createPlaceholdersMap();
      this.initFragmentInstancesMap();
      this.extractFragmentSelection(this.fragmentSelectionText);
      this.prepareInitialDisplayText();
    });

    let noticeTemplateDescription = this.noticeTemplateService.getNoticeTemplateDescription();
    this.form.valueChanges.subscribe(() => {
      this.updateDisplayText()
      if (noticeTemplateDescription && this.displayText) {
        noticeTemplateDescription.displayText = this.sanitizeDisplayText(this.displayText);
        this.noticeTemplateService.changeNoticeTemplateDescription(noticeTemplateDescription);
      }
    });
  }

  /**
   * @returns sanitized display text used for preview and for registering the notice in the final announcement
   */
  sanitizeDisplayText(displayText: string): string {
    return displayText
      .replaceAll("<mark>", "") //deleting marks around placeholders
      .replaceAll("</mark>", "") //deleting marks around placeholders
      //deleting <div id="fragment- with class etc. while still keeping the content inside the <div></div>
      .replaceAll(/<div id="fragment-[^"]*" class="fragment-box [^"]*">([\s\S]*?)<\/div>/g, '$1');
  }

  getNoticeTemplateDescription() {
    return this.noticeTemplateService.getNoticeTemplateDescription();
  }

  // Map placeholders to their corresponding ServiceFieldType instances
  private createPlaceholdersMap(): void {
    this.placeholdersMap.set("#", this.findPlaceholders(this.displayText));

    for (const fragment of this.fragmentsFound) {
      this.placeholdersMap.set(fragment.code, this.findPlaceholders(fragment.text));
    }
  }

  // Map placeholders to their corresponding ServiceFieldType instances
  private initFragmentInstancesMap(): void {
    for (const fragment of this.fragmentsFound) {
      this.addFragmentInstance(fragment.code)
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
      let existing = this.serviceFieldTypes.find(serviceFieldType => serviceFieldType.code === code);
      if (existing)
        existing = copyObject(existing, false) as ServiceFieldType;
      result.push(existing ? existing : { id: generatedId++, code } as ServiceFieldType);
    }
    return result;
  }

  // Find everything between [] and populare fragmentSelection array
  extractFragmentSelection(text: string) {
    const regex = /\[([^\]]+)\]/g;
    let match;

    let i = 0;
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
      i++;
    }
  }

  // Format and initialize the display text with placeholders converted
  private prepareInitialDisplayText(): void {
    let text = this.displayTextOriginal;

    // Replace main notice placeholders
    text = this.findAndReplacePlaceholders(undefined, text, this.placeholdersMap.get("#") ?? [], undefined);

    this.displayTextOriginal = text;
    this.displayText = text;
  }

  // Replaces the placeholders in the display text with the current form values
  private updateDisplayText(): void {
    let text = this.displayTextOriginal;

    // When fragment is selected, replace [XXXX || YYYYY] by [XXXX] if XXXX is the selected fragment
    for (let selectedFragment of this.selectedFragments) {
      if (selectedFragment) {
        text = text.replace(/\[([^\[\]]+)\]/g, (match, group) => {
          const items = group.split(/\s*\|\|\s*/); // split by '||' with spaces management
          if (items.includes(selectedFragment.code)) {
            return `[${selectedFragment.code}]`;
          } else {
            return match; // do not change if not found
          }
        });
      }

      this.fragmentSelectionText = text;
    }

    // When fragment is selected, shows the text in the displayText
    for (let selectedFragment of this.selectedFragments) {
      if (selectedFragment) {
        let fragmentFound = this.fragmentsFound.find(fragment => fragment.code == selectedFragment!.code);
        if (fragmentFound) {
          let fragmentTextToReplace: string = "";
          let i = 0;
          for (let fragment of this.fragmentInstancesMap.get(fragmentFound.code)!) {
            fragmentTextToReplace = fragmentTextToReplace + fragment.text + "<br>";
            fragmentTextToReplace = this.findAndReplacePlaceholders(fragmentFound, fragmentTextToReplace, this.placeholdersMap.get(fragmentFound.code) ?? [], i);
            i++;
          }
          text = text.replace(new RegExp(`\\[\\s*${fragmentFound.code}\\s*\\]`), `<div id="fragment-${fragmentFound.code}" class="fragment-box ${this.getFragmentClass(fragmentFound)}">` + fragmentTextToReplace + "</div>");
          text = text.replace("/n", "<br>");
        }
      }
    }

    // Finds placeholders and replace them in text with input value
    for (let [fragmentCode, placeholders] of this.placeholdersMap.entries()) {
      for (let placeholder of placeholders) {
        let controlName: string;
        if (fragmentCode === '#') {
          controlName = this.buildFormControlName(null, placeholder, undefined);
          text = this.replaceControleNamesWithText(controlName, placeholder, fragmentCode, text);
        } else if (this.fragmentInstancesMap.get(fragmentCode)) {
          for (let i = 0; i < this.fragmentInstancesMap.get(fragmentCode)!.length; i++) {
            controlName = this.buildFormControlName(fragmentCode, placeholder, i);
            text = this.replaceControleNamesWithText(controlName, placeholder, fragmentCode, text);
          }
        }
      }
    }

    // We take out the fragments codes and replace them with their label before displaying the text
    text = text.replace(new RegExp('\\[|\\]', 'g'), "");

    for (let fragment of this.fragmentsFound) {
      if (this.selectedFragments.includes(fragment)) {
        text = text.replace(this.getRegexDoublePipeNearCode(fragment.code), "ou");
        text = text.replace(new RegExp(`(?<!["=])\\b${fragment.code}\\b(?!["])`, 'g'), fragment.label); // replacing all fragments but the one in the HTML tags (<...>)
      } else {
        // We first isolate the fragments from the ||
        text = text.replace(this.getRegexDoublePipeNearCode(fragment.code), "");
        // Then we replace the code by the label
        text = text.replace(new RegExp(`(\\b${fragment.code}\\b)`, 'g'), "");
      }
    }

    // Replace multiple consecutive <br> tags with only the first and the last
    text = text.replace(/<p>(\s|&nbsp;|<br\s*\/?>)*<\/p>/gi, '');
    text = text.replace(/(<br\s*\/?>\s*(?:&nbsp;)?\s*){3,}/gi, '<br><br>');

    this.displayText = text;
  }

  private getRegexDoublePipeNearCode(code: string): RegExp {
    // Escape special characters in the code to avoid regex errors
    const escapedCode = code.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');

    // Regex :
    // - (\\|\\|.{0,3}CODE) → || followed by up to 4 characters, then the code
    // - (CODE.{0,3}\\|\\|) → code followed by up to 4 characters, then ||
    const pattern = `(\\|\\|.{0,3}\\b${escapedCode}\\b)|(\\b${escapedCode}\\b.{0,3}\\|\\|)`;

    return new RegExp(pattern, 'g');
  }

  setSelectedFragment(fragmentCode: string) {
    if (fragmentCode != this.selectedFragmentCode) {
      this.selectedFragmentCode = fragmentCode;
      this.updateDisplayText();
      this.scrollToFragment(fragmentCode);
    }
  }

  isFragmentSelected(announcementNoticeTemplateFragment: AnnouncementNoticeTemplateFragment | undefined) {
    if (announcementNoticeTemplateFragment)
      return announcementNoticeTemplateFragment.code === this.selectedFragmentCode;
    else
      return false;
  }

  getFragmentClass(announcementNoticeTemplateFragment: AnnouncementNoticeTemplateFragment | undefined): string {
    if (announcementNoticeTemplateFragment)
      if (announcementNoticeTemplateFragment.code === this.selectedFragmentCode)
        return "selected-fragment";
    return "fragment-border";
  }

  private scrollToFragment(code: string): void {
    setTimeout(() => {
      const el = document.getElementById(`fragment-${code}`);
      if (el) {
        el.scrollIntoView({ behavior: 'smooth', block: 'start' });
      }
    }, 100); // Timeout so the DOM is well up to date
  }

  private replaceControleNamesWithText(controlName: string, placeholder: ServiceFieldType, fragmentCode: string, text: string) {
    let inputValue = this.sanitizeValue(this.form.get(controlName)?.value);
    if (inputValue == "") {
      inputValue = placeholder.label ? placeholder.label : placeholder.code;
      if (fragmentCode != "#") {
        let fragmentFound = this.fragmentsFound.find(fragment => fragment.code == fragmentCode);
        if (fragmentFound)
          inputValue = inputValue;
      }
    }
    text = text.replace(new RegExp(`\\{\\s*${controlName}\\s*\\}`), inputValue);
    return text;
  }

  // Find placeholders formatted as {placeholder} and change them to <mark>{*IF PRESENT : fragment.code_*placeholder_id}</mark>
  private findAndReplacePlaceholders(fragment: AnnouncementNoticeTemplateFragment | undefined, text: string, fragmentPlaceholders: ServiceFieldType[], fragmentIndex: number | undefined) {
    for (const placeholder of fragmentPlaceholders) {
      text = text.replace(new RegExp(`\\{\\s*${placeholder.code}\\s*\\}`), `<mark>{${this.buildFormControlName(fragment ? fragment.code : null, placeholder, fragmentIndex)}}</mark>`);
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
  buildFormControlName(fragmentCode: string | null, placeholder: ServiceFieldType, fragmentInstance: number | undefined): string {
    let baseName = fragmentCode ? `${fragmentCode}_${placeholder.code}_${placeholder.id}` : `${placeholder.code}_${placeholder.id}`;
    return fragmentInstance !== undefined ? `${baseName}_i${fragmentInstance}` : baseName;
  }

  onFragmentSelectionChange(fragment: AnnouncementNoticeTemplateFragment, index: number) {
    if (fragment) {
      this.selectedFragments[index] = fragment;
      this.setSelectedFragment(fragment.code);
    }
  }

  changeToggleValue(announcementNoticeTemplateFragment: AnnouncementNoticeTemplateFragment, index: number) {
    if (announcementNoticeTemplateFragment && this.fragmentSelection[index] && !this.isFragmentSelected(announcementNoticeTemplateFragment)) {
      this.selectedFragments.splice(index, 1, this.fragmentSelection[index][0]);
      this.setSelectedFragment(this.fragmentSelection[index][0].code);
    } else {
      this.selectedFragments.splice(index, 1, undefined);
      this.setSelectedFragment("");
    }
  }

  isSelectedFragmentsContainsFragment(announcementNoticeTemplateFragment: AnnouncementNoticeTemplateFragment): boolean {
    return this.selectedFragments.findIndex(announcement => announcement?.code == announcementNoticeTemplateFragment.code) == -1 ? false : true;

  }

  getSelectionClassForFragment(announcementNoticeTemplateFragment: AnnouncementNoticeTemplateFragment): string {
    if (this.isSelectedFragmentsContainsFragment(announcementNoticeTemplateFragment))
      if (this.isFragmentSelected(announcementNoticeTemplateFragment))
        return "btn-selected-blue";
      else
        return "btn-selected-yellow";
    else
      return "btn-disabled"
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

  addFragmentInstance(fragmentCodeToAdd: string): void {
    this.updateFragmentInstance(fragmentCodeToAdd, true);
  }

  deleteFragmentInstance(fragmentCodeToAdd: string): void {
    this.updateFragmentInstance(fragmentCodeToAdd, false);
  }

  private updateFragmentInstance(fragmentCodeToAdd: string, isToAdd: boolean): void {
    if (!this.fragmentInstancesMap.get(fragmentCodeToAdd)) {
      this.fragmentInstancesMap.set(fragmentCodeToAdd, []);
    }

    const fragmentFound = this.fragmentsFound.find(fragment => fragment.code == fragmentCodeToAdd);
    if (fragmentFound) {
      if (isToAdd) {
        this.fragmentInstancesMap.get(fragmentCodeToAdd)!.push(fragmentFound);
      } else {
        this.fragmentInstancesMap.get(fragmentCodeToAdd)!.pop();
      }
    }
    this.updateDisplayText();
  }

  getFragmentInputLabel(placeholder: ServiceFieldType, fragmentIndex: number, fragmentInstances: AnnouncementNoticeTemplateFragment[]): string {
    let baseLabel = "";
    if (placeholder.label)
      baseLabel = placeholder.label;
    else
      baseLabel = placeholder.code;

    if (fragmentInstances.length > 1) {
      let fragmentIndexCopy = fragmentIndex + 1;
      return baseLabel + " " + fragmentIndexCopy;
    } else {
      return baseLabel;
    }
  }


  showPreviewModal() {
    this.previewModal(this.previewModalView);
  }

  previewModal(content: TemplateRef<any>) {
    if (this.previewModalInstance) {
      return;
    }

    this.previewModalInstance = this.modalService.open(content, { size: 'xl', centered: true, scrollable: false });

    this.previewModalInstance.result.finally(() => {
      this.previewModalInstance = undefined;
    });
  }

}