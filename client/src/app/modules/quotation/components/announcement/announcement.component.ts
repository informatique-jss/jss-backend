declare var require: any
import { Component, ElementRef, EventEmitter, Input, OnInit, Output, SimpleChanges, ViewChild } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import { MatDialog } from '@angular/material/dialog';
import { MatAccordion } from '@angular/material/expansion';
import { MatTabChangeEvent } from '@angular/material/tabs';
import { ChangeEvent } from '@ckeditor/ckeditor5-angular';
import { Alignment, Bold, ClassicEditor, Clipboard, Essentials, Font, GeneralHtmlSupport, Indent, IndentBlock, Italic, Link, List, Mention, Paragraph, PasteFromOffice, RemoveFormat, Underline, Undo } from 'ckeditor5';
import { Observable } from 'rxjs';
import { map, startWith } from 'rxjs/operators';
import { SEPARATOR_KEY_CODES } from 'src/app/libs/Constants';
import { instanceOfCustomerOrder } from 'src/app/libs/TypeHelper';
import { Attachment } from 'src/app/modules/miscellaneous/model/Attachment';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { ANNOUNCEMENT_ENTITY_TYPE } from 'src/app/routing/search/search.component';
import { PROVISION_ENTITY_TYPE } from '../../../../routing/search/search.component';
import { AppService } from '../../../../services/app.service';
import { HabilitationsService } from '../../../../services/habilitations.service';
import { UserPreferenceService } from '../../../../services/user.preference.service';
import { AttachmentType } from '../../../miscellaneous/model/AttachmentType';
import { Document } from "../../../miscellaneous/model/Document";
import { Announcement } from '../../model/Announcement';
import { AnnouncementNoticeTemplate } from '../../model/AnnouncementNoticeTemplate';
import { AnnouncementStatus } from '../../model/AnnouncementStatus';
import { CharacterPrice } from '../../model/CharacterPrice';
import { Confrere } from '../../model/Confrere';
import { IQuotation } from '../../model/IQuotation';
import { JournalType } from '../../model/JournalType';
import { NoticeType } from '../../model/NoticeType';
import { Provision } from '../../model/Provision';
import { AnnouncementNoticeTemplateService } from '../../services/announcement.notice.template.service';
import { AnnouncementStatusService } from '../../services/announcement.status.service';
import { CharacterNumberService } from '../../services/character.number.service';
import { CharacterPriceService } from '../../services/character.price.service';
import { ConfrereService } from '../../services/confrere.service';
import { JournalTypeService } from '../../services/journal.type.service';
import { NoticeTypeService } from '../../services/notice.type.service';


@Component({
  selector: 'announcement',
  templateUrl: './announcement.component.html',
  styleUrls: ['./announcement.component.css']
})
export class AnnouncementComponent implements OnInit {
  @Input() announcement: Announcement = {} as Announcement;
  @Input() provision: Provision | undefined;
  @Input() editMode: boolean = false;
  @Input() instanceOfCustomerOrder: boolean = false;
  @Input() isStatusOpen: boolean = true;
  @Input() quotation: IQuotation | undefined;
  @Output() provisionChange: EventEmitter<Provision> = new EventEmitter<Provision>();

  @ViewChild('noticeTypesInput') noticeTypesInput: ElementRef<HTMLInputElement> | undefined;
  @ViewChild('noticeTemplateInput') noticeTemplateInput: ElementRef<HTMLInputElement> | undefined;
  @ViewChild(MatAccordion) accordion: MatAccordion | undefined;

  SEPARATOR_KEY_CODES = SEPARATOR_KEY_CODES;
  ANNOUNCEMENT_ENTITY_TYPE = ANNOUNCEMENT_ENTITY_TYPE;
  PROVISION_ENTITY_TYPE = PROVISION_ENTITY_TYPE;
  instanceOfCustomerOrderFn = instanceOfCustomerOrder;

  journalTypes: JournalType[] = [] as Array<JournalType>;
  journalTypeSpel: JournalType = this.constantService.getJournalTypeSpel();
  journalTypePaper: JournalType = this.constantService.getJournalTypePaper();
  confrereJssSpel: Confrere = this.constantService.getConfrereJssSpel();
  attachmentTypePublicationReceipt: AttachmentType = this.constantService.getAttachmentTypePublicationReceipt();
  attachmentTypePublicationFlag: AttachmentType = this.constantService.getAttachmentTypePublicationFlag();
  attachmentTypeComplexAnnouncement: AttachmentType = this.constantService.getAttachmentTypeComplexAnnouncement();

  characterPrice: CharacterPrice = {} as CharacterPrice;

  noticeTypes: NoticeType[] = [] as Array<NoticeType>;
  filteredNoticeTypes: Observable<NoticeType[]> | undefined;

  noticeTemplates: AnnouncementNoticeTemplate[] = [] as Array<AnnouncementNoticeTemplate>;
  filteredNoticeTemplates: Observable<AnnouncementNoticeTemplate[]> | undefined;
  selectedNoticeTemplates: AnnouncementNoticeTemplate[] = [] as Array<AnnouncementNoticeTemplate>;
  paperDocument: Document = {} as Document;

  characterNumber: number = 0;

  announcementStatus: AnnouncementStatus[] | undefined;

  initialNoticeValue: string = "";
  initialHeaderValue: string = "";

  constructor(private formBuilder: UntypedFormBuilder,
    private characterPriceService: CharacterPriceService,
    private constantService: ConstantService,
    private noticeTypeService: NoticeTypeService,
    public importHtmlAnnouncementDialog: MatDialog,
    private appService: AppService,
    private journalTypeService: JournalTypeService,
    private announcementNoticeTemplateService: AnnouncementNoticeTemplateService,
    private characterNumberService: CharacterNumberService,
    private habilitationsService: HabilitationsService,
    private announcementStatusService: AnnouncementStatusService,
    private userPreferenceService: UserPreferenceService,
    private confrereService: ConfrereService
  ) { }

  canAddNewInvoice() {
    return this.habilitationsService.canAddNewInvoice();
  }

  ngOnInit() {
    this.announcementStatusService.getAnnouncementStatus().subscribe(response => { this.announcementStatus = response });

    this.journalTypeService.getJournalTypes().subscribe(response => {
      this.journalTypes = response;
    })

    this.noticeTypeService.getNoticeTypes().subscribe(response => {
      this.noticeTypes = response;
    })

    this.announcementNoticeTemplateService.getAnnouncementNoticeTemplates().subscribe(response => {
      this.noticeTemplates = response;
    });

    this.filteredNoticeTypes = this.announcementForm.get("noticeTypes")?.valueChanges.pipe(
      startWith(''),
      map(value => this._filterNoticeType(value)),
    );

    this.filteredNoticeTemplates = this.announcementForm.get("noticeTemplates")?.valueChanges.pipe(
      startWith(''),
      map(value => this._filterNoticeTemplates(value)),
    );

    this.restoreTab();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.announcement && this.announcement) {
      if (!this.announcement)
        this.announcement = {} as Announcement;
      if (!this.announcement.isHeader)
        this.announcement.isHeader = false;
      if (!this.announcement.isHeaderFree)
        this.announcement.isHeaderFree = false;
      if (!this.announcement.isComplexAnnouncement)
        this.announcement.isComplexAnnouncement = false;
      if (!this.announcement.isProofReadingDocument)
        this.announcement.isProofReadingDocument = false;
      if (this.announcement.publicationDate)
        this.announcement.publicationDate = new Date(this.announcement.publicationDate);
      if (this.announcement.confrere && this.provision) {
        if (this.announcement.confrere.journalType.id == this.constantService.getJournalTypePaper().id)
          this.provision.isPublicationFlag = false;
        if (this.announcement.confrere.journalType.id == this.constantService.getJournalTypeSpel().id)
          this.provision.isPublicationPaper = false;
      }

      this.announcementForm.markAllAsTouched();
      this.updateCharacterPrice();
      this.initialNoticeValue = this.announcement.notice;
      this.initialHeaderValue = this.announcement.noticeHeader;
    }
  }

  announcementForm = this.formBuilder.group({
    noticeTypes: [''],
    noticeTemplates: [''],
    confrere: [''],
    journalType: [''],
  });


  ckEditorNotice = ClassicEditor;
  ckEditorHeader = ClassicEditor;
  config = {
    toolbar: ['undo', 'redo', '|', 'fontFamily', 'fontSize', 'bold', 'italic', 'underline', 'fontColor', 'fontBackgroundColor', '|',
      'alignment:left', 'alignment:right', 'alignment:center', 'alignment:justify', '|', 'link', 'bulletedList', 'numberedList', 'outdent', 'indent', 'removeformat'
    ],
    plugins: [
      Bold, Essentials, Italic, Mention, Paragraph, Undo, Font, Alignment, Link, List, Indent, IndentBlock, RemoveFormat, Clipboard, GeneralHtmlSupport, Underline, PasteFromOffice
    ],
    htmlSupport: {
      allow: [
        {
          name: /.*/,
          attributes: true,
          classes: true,
          styles: true
        }
      ]
    }
  } as any;

  getFormStatus(): boolean {
    this.announcementForm.markAllAsTouched();
    if (this.announcement && !this.announcement.isComplexAnnouncement && (this.announcement.notice == null || this.announcement.notice == undefined || this.announcement.notice.length == 0) && !this.isStatusOpen && this.instanceOfCustomerOrder) {
      this.appService.displaySnackBar("Le texte de l'annonce est obligatoire", true, 15);
      return false;
    }
    if (this.announcement && (!this.announcement.noticeTypes || this.announcement.noticeTypes.length == 0) && !this.isStatusOpen && this.instanceOfCustomerOrder) {
      this.appService.displaySnackBar("La rubrique de l'annonce est obligatoire", true, 15);
      return false;
    }
    if (this.announcement && this.announcement.notice)
      this.announcement.notice = this.announcement.notice.replace(/ +(?= )/g, '').replace(/(\r\n|\r|\n){2,}/g, '$1\n');
    if (this.announcement && this.announcement.publicationDate)
      this.announcement.publicationDate = new Date(this.announcement.publicationDate.setHours(12));
    return this.announcementForm.valid && (this.isStatusOpen || !this.instanceOfCustomerOrder || this.announcement.noticeTypes && this.announcement.noticeTypes.length > 0);
  }

  getPublicationDays(confrere: Confrere): string {
    if (confrere && confrere.weekDays)
      return 'Parution le : ' + confrere.weekDays.map(item => item.label).join(', ');
    return '';
  }

  getCurrentDate(): Date {
    return new Date();
  }

  updateConfrereAndCharacterPrice() {
    if (!this.announcement.department)
      this.announcement.confrere = undefined!;
    if (this.announcement.department) {
      this.confrereService.getConfrereFilteredByDepartmentAndName(this.announcement.department, '').subscribe(confreres => {
        (confreres && confreres.length > 0) ? this.announcement.confrere = confreres[0] : '';
        if (this.announcement.confrere && this.provision) {
          if (this.announcement.confrere.journalType.id == this.constantService.getJournalTypePaper().id)
            this.provision.isPublicationFlag = false;
          this.provision.isPublicationPaper = true;
          if (this.announcement.confrere.journalType.id == this.constantService.getJournalTypeSpel().id)
            this.provision.isPublicationPaper = false;
          this.provision.isPublicationFlag = true;
        }
      });
    }
    this.updateCharacterPrice();
  }

  updateCharacterPrice() {
    if (this.announcement!.department != undefined && this.announcement!.publicationDate != undefined)
      this.characterPriceService.getCharacterPrice(this.announcement!.department, this.announcement!.publicationDate).subscribe(response => {
        if (response != null)
          this.characterPrice = response;
      })
  }

  applyNoticeTemplate() {
    if (this.selectedNoticeTemplates) {
      this.announcement.notice = "";
      for (let template of this.selectedNoticeTemplates) {
        this.announcement.notice += template.text + "<br>";
      }
      this.initialNoticeValue = this.announcement.notice;
      this.noticeChangeFunction();
    }
  }

  onNoticeChange(event: ChangeEvent) {
    this.announcement.notice = event.editor.getData();
    this.noticeChangeFunction();
  }

  onNoticeHeaderChange(event: ChangeEvent) {
    this.announcement.noticeHeader = event.editor.getData();
    this.noticeChangeFunction();
  }

  noticeChangeFunction() {
    this.cleanNotice();
    setTimeout(() => {
      if (this.provision)
        this.characterNumberService.getCharacterNumber(this.provision).subscribe(response => {
          this.characterNumber = response;
        })
    }, 0);
    this.provisionChange.emit(this.provision);
  }

  cleanNotice() {
    // remove img tag
    if (this.announcement.notice) {
      this.announcement.notice = this.announcement.notice.replace(/<img[^>]*>/g, "");
      this.announcement.notice = this.announcement.notice.replace(/&quot;/g, "'");
      this.announcement.notice = this.announcement.notice.replace(/<wbr>/g, "");
    }
    if (this.announcement.noticeHeader) {
      this.announcement.noticeHeader = this.announcement.noticeHeader.replace(/<img[^>]*>/g, "");
      this.announcement.notice = this.announcement.notice.replace(/&quot;/g, "'");
      this.announcement.notice = this.announcement.notice.replace(/<wbr>/g, "");
    }
  }

  updateHeaderFree() {
    if (this.announcement && this.announcement.confrere?.journalType && this.announcement.confrere.journalType.id == this.journalTypeSpel.id)
      this.announcement.isHeaderFree = true;
  }

  public displayConfrere(object: Confrere): string {
    return object ? object.label : '';
  }

  public displayLabel(object: any): string {
    return object ? object.label : '';
  }

  private _filterNoticeType(value: string): NoticeType[] {
    const filterValue = (value != undefined && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return this.noticeTypes.filter(noticeType => noticeType.label != undefined && noticeType.label.toLowerCase().includes(filterValue) && noticeType.noticeTypeFamily.id == this.announcement!.noticeTypeFamily.id);
  }

  private _filterNoticeTemplates(value: string): AnnouncementNoticeTemplate[] {
    const filterValue = (value != undefined && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return this.noticeTemplates.filter(noticeTemplate => noticeTemplate.label != undefined && noticeTemplate.label.toLowerCase().includes(filterValue) && (!noticeTemplate.provisionFamilyTypes || this.provision && noticeTemplate.provisionFamilyTypes.map(type => type.code).indexOf(this.provision.provisionFamilyType.code) >= 0)).sort((a: AnnouncementNoticeTemplate, b: AnnouncementNoticeTemplate) => a.label.localeCompare(b.label));
  }

  addNoticeType(event: MatAutocompleteSelectedEvent): void {
    if (!this.announcement!.noticeTypes)
      this.announcement!.noticeTypes = [] as Array<NoticeType>;
    // Do not add twice
    if (this.announcement!.noticeTypes.map(noticeType => noticeType.id).indexOf(event.option.value.id) >= 0)
      return;
    if (event.option && event.option.value && event.option.value.id)
      this.announcement!.noticeTypes.push(event.option.value);
    this.announcementForm.get("noticeTypes")?.setValue(null);
    this.noticeTypesInput!.nativeElement.value = '';
  }

  addNoticeTemplate(event: MatAutocompleteSelectedEvent): void {
    if (!this.selectedNoticeTemplates)
      this.selectedNoticeTemplates = [] as Array<AnnouncementNoticeTemplate>;
    // Do not add twice
    if (this.selectedNoticeTemplates.map(noticeTemplate => noticeTemplate.id).indexOf(event.option.value.id) >= 0)
      return;
    if (event.option && event.option.value && event.option.value.id)
      this.selectedNoticeTemplates.push(event.option.value);
    this.applyNoticeTemplate();
    this.announcementForm.get("noticeTemplates")?.setValue(null);
    this.noticeTemplateInput!.nativeElement.value = '';
  }

  removeNoticeTemplate(inputNoticeTemplate: AnnouncementNoticeTemplate): void {
    if (this.selectedNoticeTemplates && this.editMode)
      for (let i = 0; i < this.selectedNoticeTemplates.length; i++) {
        const noticeTemplate = this.selectedNoticeTemplates[i];
        if (noticeTemplate.id == inputNoticeTemplate.id) {
          this.selectedNoticeTemplates.splice(i, 1);
        }
      }
    this.applyNoticeTemplate();
  }

  removeNoticeType(inputNoticeType: NoticeType): void {
    if (this.announcement!.noticeTypes != undefined && this.announcement!.noticeTypes != null && this.editMode)
      for (let i = 0; i < this.announcement!.noticeTypes.length; i++) {
        const noticeType = this.announcement!.noticeTypes[i];
        if (noticeType.id == inputNoticeType.id) {
          this.announcement!.noticeTypes.splice(i, 1);
          return;
        }
      }
  }

  clearNoticeTypeField() {
    if (this.announcement && this.announcement.noticeTypes && this.editMode && this.announcement.noticeTypes.length > 0 && this.announcement.noticeTypeFamily && this.announcement.noticeTypes[0].noticeTypeFamily.id != this.announcement.noticeTypeFamily.id) {
      this.announcement!.noticeTypes = [] as Array<NoticeType>;
      this.filteredNoticeTypes = this.announcementForm.get("noticeTypes")?.valueChanges.pipe(
        startWith(''),
        map(value => this._filterNoticeType(value)),
      );
    }
  }

  updateAttachments(attachments: Attachment[]) {
    this.appService.displaySnackBar("N'oubliez pas de mettre à jours la date de publication de l'annonce !", false, 20);
    if (attachments && this.provision) {
      this.provision.attachments = attachments;
    }
  }

  canEditJournal() {
    return this.announcement.publicationDate.getTime() < (new Date()).getTime();
  }

  //Tabs management
  index: number = 0;
  onTabChange(event: MatTabChangeEvent) {
    this.userPreferenceService.setUserTabsSelectionIndex('announcement', event.index);
  }

  restoreTab() {
    this.index = this.userPreferenceService.getUserTabsSelectionIndex('announcement');
  }
}
