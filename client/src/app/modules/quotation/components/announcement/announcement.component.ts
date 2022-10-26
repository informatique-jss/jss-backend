declare var require: any
import { Component, ElementRef, EventEmitter, Input, OnInit, Output, SimpleChanges, ViewChild } from '@angular/core';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import { MatDialog } from '@angular/material/dialog';
import { MatAccordion } from '@angular/material/expansion';
import { Observable } from 'rxjs';
import { map, startWith } from 'rxjs/operators';
import { CustomErrorStateMatcher } from 'src/app/app.component';
import { CONFRERE_BALO_ID, JOURNAL_TYPE_JSS_DENOMINATION, JOURNAL_TYPE_SPEL_CODE, SEPARATOR_KEY_CODES } from 'src/app/libs/Constants';
import { getDocument } from 'src/app/libs/DocumentHelper';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { ANNOUNCEMENT_ENTITY_TYPE } from 'src/app/routing/search/search.component';
import { ConfrereDialogComponent } from '../../../miscellaneous/components/confreres-dialog/confreres-dialog.component';
import { Document } from "../../../miscellaneous/model/Document";
import { Affaire } from '../../model/Affaire';
import { Announcement } from '../../model/Announcement';
import { AnnouncementNoticeTemplate } from '../../model/AnnouncementNoticeTemplate';
import { CharacterPrice } from '../../model/CharacterPrice';
import { Confrere } from '../../model/Confrere';
import { JournalType } from '../../model/JournalType';
import { NoticeType } from '../../model/NoticeType';
import { Provision } from '../../model/Provision';
import { AnnouncementNoticeTemplateService } from '../../services/announcement.notice.template.service';
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

  matcher: CustomErrorStateMatcher = new CustomErrorStateMatcher();
  @Input() announcement: Announcement = {} as Announcement;
  @Input() affaire: Affaire = {} as Affaire;
  @Input() provision: Provision = {} as Provision;
  @Input() editMode: boolean = false;
  @Input() instanceOfCustomerOrder: boolean = false;
  @Input() isStatusOpen: boolean = true;
  @Output() noticeChange: EventEmitter<void> = new EventEmitter<void>();

  @ViewChild('tabs', { static: false }) tabs: any;
  @ViewChild('noticeTypesInput') noticeTypesInput: ElementRef<HTMLInputElement> | undefined;
  @ViewChild('noticeTemplateInput') noticeTemplateInput: ElementRef<HTMLInputElement> | undefined;
  @ViewChild(MatAccordion) accordion: MatAccordion | undefined;

  SEPARATOR_KEY_CODES = SEPARATOR_KEY_CODES;
  ANNOUNCEMENT_ENTITY_TYPE = ANNOUNCEMENT_ENTITY_TYPE;
  CONFRERE_BALO_ID = CONFRERE_BALO_ID;
  JOURNAL_TYPE_SPEL_CODE = JOURNAL_TYPE_SPEL_CODE;

  journalTypes: JournalType[] = [] as Array<JournalType>;

  confreres: Confrere[] = [] as Array<Confrere>;
  filteredConfreres: Observable<Confrere[]> | undefined;

  characterPrice: CharacterPrice = {} as CharacterPrice;

  publicationDocument: Document = {} as Document;
  proofReadingDocument: Document = {} as Document;
  publicationCertificateDocument: Document = {} as Document;

  noticeTypes: NoticeType[] = [] as Array<NoticeType>;
  filteredNoticeTypes: Observable<NoticeType[]> | undefined;

  noticeTemplates: AnnouncementNoticeTemplate[] = [] as Array<AnnouncementNoticeTemplate>;
  filteredNoticeTemplates: Observable<AnnouncementNoticeTemplate[]> | undefined;
  selectedNoticeTemplates: AnnouncementNoticeTemplate[] = [] as Array<AnnouncementNoticeTemplate>;

  constructor(private formBuilder: UntypedFormBuilder,
    private confrereService: ConfrereService,
    private characterPriceService: CharacterPriceService,
    private constantService: ConstantService,
    private noticeTypeService: NoticeTypeService,
    public confrereDialog: MatDialog,
    private journalTypeService: JournalTypeService,
    private announcementNoticeTemplateService: AnnouncementNoticeTemplateService,
  ) { }

  ngOnInit() {
    this.confrereService.getConfreres().subscribe(response => {
      this.confreres = response;
      if (this.announcement!.confrere == null || this.announcement!.confrere == undefined || this.announcement!.confrere.id == undefined)
        this.announcement!.confrere = this.getJssConfrere();
    })

    this.journalTypeService.getJournalTypes().subscribe(response => {
      this.journalTypes = response;
    })

    this.noticeTypeService.getNoticeTypes().subscribe(response => {
      this.noticeTypes = response;
    })

    this.announcementNoticeTemplateService.getAnnouncementNoticeTemplates().subscribe(response => {
      this.noticeTemplates = response;
    })

    this.filteredConfreres = this.announcementForm.get("confrere")?.valueChanges.pipe(
      startWith(''),
      map(value => this._filterConfrere(value)),
    );

    this.filteredNoticeTypes = this.announcementForm.get("noticeTypes")?.valueChanges.pipe(
      startWith(''),
      map(value => this._filterNoticeType(value)),
    );

    this.filteredNoticeTemplates = this.announcementForm.get("noticeTemplates")?.valueChanges.pipe(
      startWith(''),
      map(value => this._filterNoticeTemplates(value)),
    );
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.announcement) {
      if (!this.announcement!)
        this.announcement! = {} as Announcement;
      if (!this.announcement!.confrere)
        this.announcement!.confrere = this.getJssConfrere();
      if (!this.announcement!.isRedactedByJss)
        this.announcement!.isRedactedByJss = false;
      if (!this.announcement!.isHeader)
        this.announcement!.isHeader = false;
      if (!this.announcement!.isHeaderFree)
        this.announcement!.isHeaderFree = false;
      if (!this.announcement!.isPictureBaloPackage)
        this.announcement!.isPictureBaloPackage = false;
      if (!this.announcement!.isLegalDisplay)
        this.announcement!.isLegalDisplay = false;
      if (!this.announcement!.isProofReadingDocument)
        this.announcement!.isProofReadingDocument = false;
      if (!this.announcement!.isPublicationCertificateDocument)
        this.announcement!.isPublicationCertificateDocument = false;
      if (this.announcement!.publicationDate)
        this.announcement.publicationDate = new Date(this.announcement.publicationDate);

      this.publicationDocument = getDocument(this.constantService.getDocumentTypePublication(), this.announcement!);
      this.proofReadingDocument = getDocument(this.constantService.getDocumentTypeProofReading(), this.announcement!);
      this.publicationCertificateDocument = getDocument(this.constantService.getDocumentTypePublicationCertificate(), this.announcement!);

      this.announcementForm.get('notice')?.setValue(this.announcement.notice);
      this.announcementForm.get('noticeHeader')?.setValue(this.announcement.noticeHeader);

      this.announcementForm.markAllAsTouched();
      this.toggleTabs();
      this.updateCharacterPrice();
    }
  }

  getJssConfrere(): Confrere {
    if (this.confreres != undefined)
      for (let i = 0; i < this.confreres.length; i++) {
        const confrere = this.confreres[i];
        if (confrere.label == JOURNAL_TYPE_JSS_DENOMINATION)
          return confrere;
      }
    return {} as Confrere;
  }

  announcementForm = this.formBuilder.group({
    noticeTypes: [''],
    noticeTemplates: [''],
    notice: ['', Validators.required],
    noticeHeader: [''],
    confrere: [''],
    journalType: [''],
  });

  getFormStatus(): boolean {
    this.announcementForm.markAllAsTouched();
    if (this.announcement && this.announcement.notice)
      this.announcement.notice = this.announcement.notice.replace(/ +(?= )/g, '').replace(/(\r\n|\r|\n){2,}/g, '$1\n');

    return this.announcementForm.valid && this.announcement.noticeTypes && this.announcement.noticeTypes.length > 0;
  }

  getCurrentDate(): Date {
    return new Date();
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
      this.announcementForm.get('notice')?.setValue(this.announcement.notice);
    }
  }

  setNoticeModel(event: any) {
    if (this.announcement)
      this.announcement.notice = event.html;
    this.noticeChangeFunction();
  }

  setNoticeHeaderModel(event: any) {
    if (this.announcement)
      this.announcement.noticeHeader = event.html;
    this.noticeChangeFunction();
  }

  noticeChangeFunction() {
    this.noticeChange.emit();
  }


  countCharacterNumber() {
    let noticeValue = this.announcementForm.get('notice')?.value != undefined ? this.announcementForm.get('notice')?.value : "";
    // Ignore HTML tags
    noticeValue = new DOMParser().parseFromString(noticeValue, "text/html").documentElement.textContent;

    let headerValue = this.announcementForm.get('noticeHeader')?.value != undefined ? this.announcementForm.get('noticeHeader')?.value : "";
    // Ignore HTML tags
    headerValue = new DOMParser().parseFromString(headerValue, "text/html").documentElement.textContent;

    let nbr = noticeValue.replace(/ +(?= )/g, '').replace(/(\r\n|\r|\n){2,}/g, ' ').trim().length;
    if (!this.announcement?.isHeaderFree)
      nbr += headerValue.replace(/ +(?= )/g, '').replace(/(\r\n|\r|\n){2,}/g, ' ').trim().length;

    return nbr;
  }

  toggleTabs() {
    if (this.tabs != undefined)
      this.tabs.realignInkBar();
  }

  updateHeaderFree() {
    if (this.announcement && this.announcement.confrere?.journalType && this.announcement.confrere.journalType.code == JOURNAL_TYPE_SPEL_CODE)
      this.announcement.isHeaderFree = true;
  }

  openConfrereDialog() {
    let dialogConfrere = this.confrereDialog.open(ConfrereDialogComponent, {
      width: '100%'
    });
    dialogConfrere.afterClosed().subscribe(response => {
      if (response && response != null)
        this.announcement!.confrere = response;
      this.updateHeaderFree();
    });
  }


  public displayConfrere(object: Confrere): string {
    return object ? object.label : '';
  }

  public displayLabel(object: any): string {
    return object ? object.label : '';
  }

  private _filterConfrere(value: string): Confrere[] {
    const filterValue = (value != undefined && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return this.confreres.filter(confrere => confrere.label != undefined && confrere.label.toLowerCase().includes(filterValue));
  }

  private _filterNoticeType(value: string): NoticeType[] {
    const filterValue = (value != undefined && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return this.noticeTypes.filter(noticeType => noticeType.label != undefined && noticeType.label.toLowerCase().includes(filterValue) && noticeType.noticeTypeFamily.id == this.announcement!.noticeTypeFamily.id);
  }

  private _filterNoticeTemplates(value: string): AnnouncementNoticeTemplate[] {
    const filterValue = (value != undefined && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return this.noticeTemplates.filter(noticeTemplate => noticeTemplate.label != undefined && noticeTemplate.label.toLowerCase().includes(filterValue) && (!noticeTemplate.provisionFamilyTypes || noticeTemplate.provisionFamilyTypes.map(type => type.code).indexOf(this.provision.provisionFamilyType.code) >= 0));
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

  getHistoryActions(): SortTableAction[] {
    let historyActions = [] as Array<SortTableAction>;

    return historyActions;
  }

}
