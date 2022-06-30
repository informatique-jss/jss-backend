declare var require: any
import { Component, ElementRef, Input, OnInit, SimpleChanges, ViewChild } from '@angular/core';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import { MatDialog } from '@angular/material/dialog';
import { MatAccordion } from '@angular/material/expansion';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { Observable } from 'rxjs';
import { map, startWith } from 'rxjs/operators';
import { CustomErrorStateMatcher } from 'src/app/app.component';
import { CONFRERE_BALO_ID, JOURNAL_TYPE_JSS_DENOMINATION, JOURNAL_TYPE_SPEL_CODE, LOGO_ATTACHMENT_TYPE_CODE, PROOF_READING_DOCUMENT_TYPE_CODE, PUBLICATION_CERTIFICATE_DOCUMENT_TYPE_CODE, PUBLICATION_TIERS_DOCUMENT_TYPE_CODE, SEPARATOR_KEY_CODES } from 'src/app/libs/Constants';
import { getDocument } from 'src/app/libs/DocumentHelper';
import { downloadHtmlAsRtf } from 'src/app/libs/DownloadHelper';
import { formatDate } from 'src/app/libs/FormatHelper';
import { Attachment } from 'src/app/modules/miscellaneous/model/Attachment';
import { Audit } from 'src/app/modules/miscellaneous/model/Audit';
import { HistoryAction } from 'src/app/modules/miscellaneous/model/HistoryAction';
import { DocumentTypeService } from 'src/app/modules/miscellaneous/services/document.type.service';
import { UploadAttachmentService } from 'src/app/modules/miscellaneous/services/upload.attachment.service';
import { SHAL_ENTITY_TYPE } from 'src/app/routing/search/search.component';
import { ConfrereDialogComponent } from '../../../miscellaneous/components/confreres-dialog/confreres-dialog.component';
import { Document } from "../../../miscellaneous/model/Document";
import { DocumentType } from "../../../miscellaneous/model/DocumentType";
import { Affaire } from '../../model/Affaire';
import { CharacterPrice } from '../../model/CharacterPrice';
import { Confrere } from '../../model/Confrere';
import { JournalType } from '../../model/JournalType';
import { NoticeType } from '../../model/NoticeType';
import { Shal } from '../../model/Shal';
import { ShalNoticeTemplate } from '../../model/ShalNoticeTemplate';
import { CharacterPriceService } from '../../services/character.price.service';
import { ConfrereService } from '../../services/confrere.service';
import { JournalTypeService } from '../../services/journal.type.service';
import { NoticeTypeService } from '../../services/notice.type.service';
import { ShalNoticeTemplateService } from '../../services/shal.notice.template.service';

@Component({
  selector: 'shal',
  templateUrl: './shal.component.html',
  styleUrls: ['./shal.component.css']
})
export class ShalComponent implements OnInit {

  matcher: CustomErrorStateMatcher = new CustomErrorStateMatcher();
  @Input() shal: Shal = {} as Shal;
  @Input() affaire: Affaire = {} as Affaire;
  @Input() editMode: boolean = false;
  @Input() instanceOfCustomerOrder: boolean = false;
  @Input() isStatusOpen: boolean = true;

  @ViewChild('tabs', { static: false }) tabs: any;
  @ViewChild('noticeTypesInput') noticeTypesInput: ElementRef<HTMLInputElement> | undefined;
  @ViewChild('noticeTemplateInput') noticeTemplateInput: ElementRef<HTMLInputElement> | undefined;
  @ViewChild(MatAccordion) accordion: MatAccordion | undefined;

  SEPARATOR_KEY_CODES = SEPARATOR_KEY_CODES;
  SHAL_ENTITY_TYPE = SHAL_ENTITY_TYPE;
  CONFRERE_BALO_ID = CONFRERE_BALO_ID;
  LOGO_ATTACHMENT_TYPE_CODE = LOGO_ATTACHMENT_TYPE_CODE;
  JOURNAL_TYPE_SPEL_CODE = JOURNAL_TYPE_SPEL_CODE;

  journalTypes: JournalType[] = [] as Array<JournalType>;

  confreres: Confrere[] = [] as Array<Confrere>;
  filteredConfreres: Observable<Confrere[]> | undefined;

  characterPrice: CharacterPrice = {} as CharacterPrice;

  publicationDocument: Document = {} as Document;
  proofReadingDocument: Document = {} as Document;
  publicationCertificateDocument: Document = {} as Document;

  documentTypes: DocumentType[] = [] as Array<DocumentType>;

  noticeTypes: NoticeType[] = [] as Array<NoticeType>;
  filteredNoticeTypes: Observable<NoticeType[]> | undefined;

  noticeTemplates: ShalNoticeTemplate[] = [] as Array<ShalNoticeTemplate>;
  filteredNoticeTemplates: Observable<ShalNoticeTemplate[]> | undefined;
  selectedNoticeTemplates: ShalNoticeTemplate[] = [] as Array<ShalNoticeTemplate>;

  logoUrl: SafeUrl | undefined;

  constructor(private formBuilder: UntypedFormBuilder,
    private confrereService: ConfrereService,
    private characterPriceService: CharacterPriceService,
    private noticeTypeService: NoticeTypeService,
    public confrereDialog: MatDialog,
    private documentTypeService: DocumentTypeService,
    private journalTypeService: JournalTypeService,
    private uploadAttachmentService: UploadAttachmentService,
    private shalNoticeTemplateService: ShalNoticeTemplateService,
    private sanitizer: DomSanitizer
  ) { }

  ngOnInit() {
    this.confrereService.getConfreres().subscribe(response => {
      this.confreres = response;
      if (this.shal!.confrere == null || this.shal!.confrere == undefined || this.shal!.confrere.id == undefined)
        this.shal!.confrere = this.getJssConfrere();
    })

    this.journalTypeService.getJournalTypes().subscribe(response => {
      this.journalTypes = response;
    })

    this.noticeTypeService.getNoticeTypes().subscribe(response => {
      this.noticeTypes = response;
    })

    this.shalNoticeTemplateService.getShalNoticeTemplates().subscribe(response => {
      this.noticeTemplates = response;
    })

    this.filteredConfreres = this.shalForm.get("confrere")?.valueChanges.pipe(
      startWith(''),
      map(value => this._filterConfrere(value)),
    );

    this.filteredNoticeTypes = this.shalForm.get("noticeTypes")?.valueChanges.pipe(
      startWith(''),
      map(value => this._filterNoticeType(value)),
    );

    this.filteredNoticeTemplates = this.shalForm.get("noticeTemplates")?.valueChanges.pipe(
      startWith(''),
      map(value => this._filterByLabel(value)),
    );
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.shal) {
      if (!this.shal!)
        this.shal! = {} as Shal;
      if (!this.shal!.confrere)
        this.shal!.confrere = this.getJssConfrere();
      if (!this.shal!.isRedactedByJss)
        this.shal!.isRedactedByJss = false;
      if (!this.shal!.journalType)
        this.shal!.journalType = this.journalTypes[0];
      if (!this.shal!.isHeader)
        this.shal!.isHeader = false;
      if (!this.shal!.isHeaderFree)
        this.shal!.isHeaderFree = false;
      if (!this.shal!.isLogo)
        this.shal!.isLogo = false;
      if (!this.shal!.isPictureBaloPackage)
        this.shal!.isPictureBaloPackage = false;
      if (!this.shal!.isLegalDisplay)
        this.shal!.isLegalDisplay = false;
      if (!this.shal!.isProofReadingDocument)
        this.shal!.isProofReadingDocument = false;
      if (!this.shal!.isPublicationCertificateDocument)
        this.shal!.isPublicationCertificateDocument = false;
      if (this.shal!.publicationDate)
        this.shal.publicationDate = new Date(this.shal.publicationDate);

      this.documentTypeService.getDocumentTypes().subscribe(response => {
        this.documentTypes = response;
        this.publicationDocument = getDocument(PUBLICATION_TIERS_DOCUMENT_TYPE_CODE, this.shal!, this.documentTypes);
        this.proofReadingDocument = getDocument(PROOF_READING_DOCUMENT_TYPE_CODE, this.shal!, this.documentTypes);
        this.publicationCertificateDocument = getDocument(PUBLICATION_CERTIFICATE_DOCUMENT_TYPE_CODE, this.shal!, this.documentTypes);
        this.setLogoUrl();
      })

      this.shalForm.get('notice')?.setValue(this.shal.notice);
      this.shalForm.get('noticeHeader')?.setValue(this.shal.noticeHeader);

      this.shalForm.markAllAsTouched();
      this.toggleTabs();
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

  shalForm = this.formBuilder.group({
    noticeTypes: [''],
    noticeTemplates: [''],
    notice: ['', Validators.required],
    noticeHeader: [''],
    confrere: [''],
    journalType: [''],
  });

  getFormStatus(): boolean {
    this.shalForm.markAllAsTouched();
    if (this.shal)
      this.shal.notice = this.shal.notice.replace(/ +(?= )/g, '').replace(/(\r\n|\r|\n){2,}/g, '$1\n');

    return this.shalForm.valid && this.shal!.noticeTypes && this.shal!.noticeTypes.length > 0;
  }

  getCurrentDate(): Date {
    return new Date();
  }

  updateCharacterPrice() {
    if (this.shal!.department != undefined && this.shal!.publicationDate != undefined)
      this.characterPriceService.getCharacterPrice(this.shal!.department, this.shal!.publicationDate).subscribe(response => {
        if (response != null)
          this.characterPrice = response;
      })
  }

  applyNoticeTemplate() {
    if (this.selectedNoticeTemplates) {
      this.shal.notice = "";
      for (let template of this.selectedNoticeTemplates) {
        this.shal.notice += template.text + "<br>";
      }
      this.shalForm.get('notice')?.setValue(this.shal.notice);
    }
  }

  setNoticeModel(event: any) {
    if (this.shal)
      this.shal.notice = event.html;
  }

  setNoticeHeaderModel(event: any) {
    if (this.shal)
      this.shal.noticeHeader = event.html;
  }

  countCharacterNumber() {
    let noticeValue = this.shalForm.get('notice')?.value != undefined ? this.shalForm.get('notice')?.value : "";
    // Ignore HTML tags
    noticeValue = new DOMParser().parseFromString(noticeValue, "text/html").documentElement.textContent;

    let headerValue = this.shalForm.get('noticeHeader')?.value != undefined ? this.shalForm.get('noticeHeader')?.value : "";
    // Ignore HTML tags
    headerValue = new DOMParser().parseFromString(headerValue, "text/html").documentElement.textContent;

    let nbr = noticeValue.replace(/ +(?= )/g, '').replace(/(\r\n|\r|\n){2,}/g, ' ').trim().length;
    if (!this.shal?.isHeaderFree)
      nbr += headerValue.replace(/ +(?= )/g, '').replace(/(\r\n|\r|\n){2,}/g, ' ').trim().length;

    return nbr;
  }

  toggleTabs() {
    if (this.tabs != undefined)
      this.tabs.realignInkBar();
  }

  updateAttachments(attachments: Attachment[]) {
    if (attachments && this.shal) {
      this.shal.attachments = attachments;
      this.setLogoUrl();
    }
  }

  setLogoUrl() {
    if (this.shal && this.shal.attachments != null && this.shal.attachments) {
      this.shal.attachments.forEach(attachment => {
        if (attachment.attachmentType.code == LOGO_ATTACHMENT_TYPE_CODE)
          this.uploadAttachmentService.previewAttachmentUrl(attachment).subscribe((response: any) => {
            let binaryData = [];
            binaryData.push(response.body);
            let url = window.URL.createObjectURL(new Blob(binaryData, { type: response.headers.get("content-type") }));
            this.logoUrl = this.sanitizer.bypassSecurityTrustUrl(url);
          })
      })
    }
  }

  updateHeaderFree() {
    if (this.shal && this.shal.journalType && this.shal.journalType.code == JOURNAL_TYPE_SPEL_CODE)
      this.shal.isHeaderFree = true;
  }

  openConfrereDialog() {
    let dialogConfrere = this.confrereDialog.open(ConfrereDialogComponent, {
      width: '90%'
    });
    dialogConfrere.afterClosed().subscribe(response => {
      if (response && response != null)
        this.shal!.confrere = response;
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
    return this.noticeTypes.filter(noticeType => noticeType.label != undefined && noticeType.label.toLowerCase().includes(filterValue) && noticeType.noticeTypeFamily.id == this.shal!.noticeTypeFamily.id);
  }

  private _filterByLabel(value: string): ShalNoticeTemplate[] {
    const filterValue = (value != undefined && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return this.noticeTemplates.filter(noticeTemplate => noticeTemplate.label != undefined && noticeTemplate.label.toLowerCase().includes(filterValue));
  }

  addNoticeType(event: MatAutocompleteSelectedEvent): void {
    if (!this.shal!.noticeTypes)
      this.shal!.noticeTypes = [] as Array<NoticeType>;
    // Do not add twice
    if (this.shal!.noticeTypes.map(noticeType => noticeType.id).indexOf(event.option.value.id) >= 0)
      return;
    if (event.option && event.option.value && event.option.value.id)
      this.shal!.noticeTypes.push(event.option.value);
    this.shalForm.get("noticeTypes")?.setValue(null);
    this.noticeTypesInput!.nativeElement.value = '';
  }

  addNoticeTemplate(event: MatAutocompleteSelectedEvent): void {
    if (!this.selectedNoticeTemplates)
      this.selectedNoticeTemplates = [] as Array<ShalNoticeTemplate>;
    // Do not add twice
    if (this.selectedNoticeTemplates.map(noticeTemplate => noticeTemplate.id).indexOf(event.option.value.id) >= 0)
      return;
    if (event.option && event.option.value && event.option.value.id)
      this.selectedNoticeTemplates.push(event.option.value);
    this.applyNoticeTemplate();
    this.shalForm.get("noticeTemplates")?.setValue(null);
    this.noticeTemplateInput!.nativeElement.value = '';
  }

  removeNoticeTemplate(inputNoticeTemplate: ShalNoticeTemplate): void {
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
    if (this.shal!.noticeTypes != undefined && this.shal!.noticeTypes != null && this.editMode)
      for (let i = 0; i < this.shal!.noticeTypes.length; i++) {
        const noticeType = this.shal!.noticeTypes[i];
        if (noticeType.id == inputNoticeType.id) {
          this.shal!.noticeTypes.splice(i, 1);
          return;
        }
      }
  }

  getHistoryActions(): HistoryAction[] {
    let historyActions = [] as Array<HistoryAction>;

    let exportRtfAction = {} as HistoryAction;
    exportRtfAction.actionClick = (element2: Audit): void => {
      downloadHtmlAsRtf("Affaire " + this.affaire.id + " - " + formatDate(new Date(element2.datetime)) + ".rtf", element2.newValue);
    }
    exportRtfAction.actionTooltip = "Télécharger en .rtf";
    exportRtfAction.actionIcon = "description";
    historyActions.push(exportRtfAction);
    return historyActions;
  }

  exportAsRtf() {
    downloadHtmlAsRtf("Affaire " + this.affaire.id + " - " + formatDate(new Date()) + ".rtf", ((this.shal.noticeHeader) ? this.shal.noticeHeader + "<br>" : "") + this.shal.notice);
  }

}
