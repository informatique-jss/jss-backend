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
import { Document } from "../../../miscellaneous/model/Document";
import { DocumentType } from "../../../miscellaneous/model/DocumentType";
import { CharacterPrice } from '../../model/CharacterPrice';
import { Confrere } from '../../model/Confrere';
import { JournalType } from '../../model/JournalType';
import { NoticeType } from '../../model/NoticeType';
import { Provision } from '../../model/Provision';
import { Shal } from '../../model/Shal';
import { CharacterPriceService } from '../../services/character.price.service';
import { ConfrereService } from '../../services/confrere.service';
import { JournalTypeService } from '../../services/journal.type.service';
import { NoticeTypeService } from '../../services/notive.type.service';
import { ConfrereDialogComponent } from '../confreres-dialog/confreres-dialog.component';

@Component({
  selector: 'shal',
  templateUrl: './shal.component.html',
  styleUrls: ['./shal.component.css']
})
export class ShalComponent implements OnInit {

  matcher: CustomErrorStateMatcher = new CustomErrorStateMatcher();
  @Input() provision: Provision = {} as Provision;
  @Input() editMode: boolean = false;

  @ViewChild('tabs', { static: false }) tabs: any;
  @ViewChild('noticeTypesInput') noticeTypesInput: ElementRef<HTMLInputElement> | undefined;
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

  logoUrl: SafeUrl | undefined;

  constructor(private formBuilder: UntypedFormBuilder,
    private confrereService: ConfrereService,
    private characterPriceService: CharacterPriceService,
    private noticeTypeService: NoticeTypeService,
    public confrereDialog: MatDialog,
    private documentTypeService: DocumentTypeService,
    private journalTypeService: JournalTypeService,
    private uploadAttachmentService: UploadAttachmentService,
    private sanitizer: DomSanitizer
  ) { }

  ngOnInit() {
    this.confrereService.getConfreres().subscribe(response => {
      this.confreres = response;
      if (this.provision.shal!.confrere == null || this.provision.shal!.confrere == undefined || this.provision.shal!.confrere.id == undefined)
        this.provision.shal!.confrere = this.getJssConfrere();
    })

    this.journalTypeService.getJournalTypes().subscribe(response => {
      this.journalTypes = response;
    })

    this.noticeTypeService.getNoticeTypes().subscribe(response => {
      this.noticeTypes = response;
    })


    this.filteredConfreres = this.shalForm.get("confrere")?.valueChanges.pipe(
      startWith(''),
      map(value => this._filterConfrere(value)),
    );

    this.filteredNoticeTypes = this.shalForm.get("noticeTypes")?.valueChanges.pipe(
      startWith(''),
      map(value => this._filterNoticeType(value)),
    );
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.provision) {
      if (!this.provision.shal!)
        this.provision.shal! = {} as Shal;
      if (!this.provision.shal!.confrere)
        this.provision.shal!.confrere = this.getJssConfrere();
      if (!this.provision.shal!.isRedactedByJss)
        this.provision.shal!.isRedactedByJss = false;
      if (!this.provision.shal!.journalType)
        this.provision.shal!.journalType = this.journalTypes[0];
      if (!this.provision.shal!.isHeader)
        this.provision.shal!.isHeader = false;
      if (!this.provision.shal!.isHeaderFree)
        this.provision.shal!.isHeaderFree = false;
      if (!this.provision.shal!.isLogo)
        this.provision.shal!.isLogo = false;
      if (!this.provision.shal!.isPictureBaloPackage)
        this.provision.shal!.isPictureBaloPackage = false;
      if (!this.provision.shal!.isLegalDisplay)
        this.provision.shal!.isLegalDisplay = false;
      if (!this.provision.shal!.isProofReadingDocument)
        this.provision.shal!.isProofReadingDocument = false;
      if (!this.provision.shal!.isPublicationCertificateDocument)
        this.provision.shal!.isPublicationCertificateDocument = false;
      if (this.provision.shal!.publicationDate)
        this.provision.shal.publicationDate = new Date(this.provision.shal.publicationDate);
      this.documentTypeService.getDocumentTypes().subscribe(response => {
        this.documentTypes = response;
        this.publicationDocument = getDocument(PUBLICATION_TIERS_DOCUMENT_TYPE_CODE, this.provision.shal!, this.documentTypes);
        this.proofReadingDocument = getDocument(PROOF_READING_DOCUMENT_TYPE_CODE, this.provision.shal!, this.documentTypes);
        this.publicationCertificateDocument = getDocument(PUBLICATION_CERTIFICATE_DOCUMENT_TYPE_CODE, this.provision.shal!, this.documentTypes);
        this.setLogoUrl();
      })

      this.shalForm.get('notice')?.setValue(this.provision.shal.notice);

      this.shalForm.markAllAsTouched();
      this.toggleTabs();
    }
  }

  getJssConfrere(): Confrere {
    if (this.confreres != undefined)
      for (let i = 0; i < this.confreres.length; i++) {
        const confrere = this.confreres[i];
        if (confrere.denomination == JOURNAL_TYPE_JSS_DENOMINATION)
          return confrere;
      }
    return {} as Confrere;
  }

  shalForm = this.formBuilder.group({
    noticeTypes: [''],
    notice: ['', Validators.required],
    noticeHeader: [''],
    confrere: [''],
    noticeTypeFamily: ['', Validators.required],
    isProofReadingDocument: [''],
    isPublicationCertificateDocument: [''],
    journalType: ['', Validators.required],
  });

  getFormStatus(): boolean {
    this.shalForm.markAllAsTouched();
    if (this.provision.shal)
      this.provision.shal.notice = this.provision.shal.notice.replace(/ +(?= )/g, '').replace(/(\r\n|\r|\n){2,}/g, '$1\n');

    return this.shalForm.valid && this.provision.shal!.noticeTypes && this.provision.shal!.noticeTypes.length > 0;
  }

  getCurrentDate(): Date {
    return new Date();
  }

  updateCharacterPrice() {
    if (this.provision.shal!.department != undefined && this.provision.shal!.publicationDate != undefined)
      this.characterPriceService.getCharacterPrice(this.provision.shal!.department, this.provision.shal!.publicationDate).subscribe(response => {
        if (response != null)
          this.characterPrice = response;
      })
  }

  setNoticeModel(event: any) {
    if (this.provision.shal)
      this.provision.shal.notice = event.html;
  }

  setNoticeHeaderModel(event: any) {
    if (this.provision.shal)
      this.provision.shal.noticeHeader = event.html;
  }

  countCharacterNumber() {
    let noticeValue = this.shalForm.get('notice')?.value != undefined ? this.shalForm.get('notice')?.value : "";
    // Ignore HTML tags
    noticeValue = new DOMParser().parseFromString(noticeValue, "text/html").documentElement.textContent;

    let headerValue = this.shalForm.get('noticeHeader')?.value != undefined ? this.shalForm.get('noticeHeader')?.value : "";
    // Ignore HTML tags
    headerValue = new DOMParser().parseFromString(headerValue, "text/html").documentElement.textContent;

    let nbr = noticeValue.replace(/ +(?= )/g, '').replace(/(\r\n|\r|\n){2,}/g, ' ').trim().length;
    if (!this.provision.shal?.isHeaderFree)
      nbr += headerValue.replace(/ +(?= )/g, '').replace(/(\r\n|\r|\n){2,}/g, ' ').trim().length;

    return nbr;
  }

  toggleTabs() {
    if (this.tabs != undefined)
      this.tabs.realignInkBar();
  }

  updateAttachments(attachments: Attachment[]) {
    if (attachments && this.provision.shal) {
      this.provision.shal.attachments = attachments;
      this.setLogoUrl();
    }
  }

  setLogoUrl() {
    if (this.provision.shal && this.provision.shal.attachments != null && this.provision.shal.attachments) {
      this.provision.shal.attachments.forEach(attachment => {
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
    if (this.provision.shal && this.provision.shal.journalType && this.provision.shal.journalType.code == JOURNAL_TYPE_SPEL_CODE)
      this.provision.shal.isHeaderFree = true;
  }

  openConfrereDialog() {
    let dialogSpecialOffer = this.confrereDialog.open(ConfrereDialogComponent, {
      width: '90%'
    });
    dialogSpecialOffer.afterClosed().subscribe(response => {
      if (response && response != null)
        this.provision.shal!.confrere = response;
    });
  }


  public displayConfrere(object: Confrere): string {
    return object ? object.denomination : '';
  }

  public displayLabel(object: any): string {
    return object ? object.label : '';
  }

  private _filterConfrere(value: string): Confrere[] {
    const filterValue = (value != undefined && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return this.confreres.filter(confrere => confrere.denomination != undefined && confrere.denomination.toLowerCase().includes(filterValue));
  }

  private _filterNoticeType(value: string): NoticeType[] {
    const filterValue = (value != undefined && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return this.noticeTypes.filter(noticeType => noticeType.label != undefined && noticeType.label.toLowerCase().includes(filterValue) && noticeType.noticeTypeFamily.id == this.provision.shal!.noticeTypeFamily.id);
  }

  addNoticeType(event: MatAutocompleteSelectedEvent): void {
    if (!this.provision.shal!.noticeTypes)
      this.provision.shal!.noticeTypes = [] as Array<NoticeType>;
    // Do not add twice
    if (this.provision.shal!.noticeTypes.map(noticeType => noticeType.id).indexOf(event.option.value.id) >= 0)
      return;
    if (event.option && event.option.value && event.option.value.id)
      this.provision.shal!.noticeTypes.push(event.option.value);
    this.shalForm.get("noticeTypes")?.setValue(null);
    this.noticeTypesInput!.nativeElement.value = '';
  }

  removeNoticeType(inputNoticeType: NoticeType): void {
    if (this.provision.shal!.noticeTypes != undefined && this.provision.shal!.noticeTypes != null && this.editMode)
      for (let i = 0; i < this.provision.shal!.noticeTypes.length; i++) {
        const noticeType = this.provision.shal!.noticeTypes[i];
        if (noticeType.id == inputNoticeType.id) {
          this.provision.shal!.noticeTypes.splice(i, 1);
          return;
        }
      }
  }

  getHistoryActions(): HistoryAction[] {
    let historyActions = [] as Array<HistoryAction>;

    let exportRtfAction = {} as HistoryAction;
    exportRtfAction.actionClick = (element2: Audit): void => {
      downloadHtmlAsRtf("Affaire " + this.provision.id + " - " + formatDate(new Date(element2.datetime)), element2.newValue);

      // TODO : marche pas ... voir solution backend
      const HtmlToRtfBrowser = require('html-to-rtf-browser');
      var htmlToRtf = new HtmlToRtfBrowser();
      let html = element2.newValue;
      const rtf = htmlToRtf.convertHtmlToRtf(html)

      var element = document.createElement('a');
      element.setAttribute('href', 'application/rtf;charset=utf-8,' + encodeURIComponent(rtf));
      element.setAttribute('download', new Date(element2.datetime) + ".rtf");

      element.style.display = 'none';
      document.body.appendChild(element);

      element.click();

      document.body.removeChild(element);
    }
    exportRtfAction.actionTooltip = "Télécharger en .rtf";
    exportRtfAction.actionIcon = "description";
    historyActions.push(exportRtfAction);
    return historyActions;
  }

}
