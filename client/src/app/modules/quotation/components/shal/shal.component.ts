import { Component, ElementRef, Input, OnInit, SimpleChanges, ViewChild } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, ValidationErrors, Validators } from '@angular/forms';
import { MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import { MatDialog } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { map, startWith } from 'rxjs/operators';
import { CustomErrorStateMatcher } from 'src/app/app.component';
import { compareWithId } from 'src/app/libs/CompareHelper';
import { CONFRERE_BALO_ID, JOURNAL_TYPE_JSS_DENOMINATION, PROOF_READING_DOCUMENT_TYPE_CODE, PUBLICATION_CERTIFICATE_DOCUMENT_TYPE_CODE, PUBLICATION_TIERS_DOCUMENT_TYPE_CODE, SEPARATOR_KEY_CODES } from 'src/app/libs/Constants';
import { getDocument } from 'src/app/libs/DocumentHelper';
import { downloadTextAsFile } from 'src/app/libs/DownloadHelper';
import { convertToRtf, formatDate } from 'src/app/libs/FormatHelper';
import { instanceOfCustomerOrder } from 'src/app/libs/TypeHelper';
import { Audit } from 'src/app/modules/miscellaneous/model/Audit';
import { Department } from 'src/app/modules/miscellaneous/model/Department';
import { HistoryAction } from 'src/app/modules/miscellaneous/model/HistoryAction';
import { DocumentTypeService } from 'src/app/modules/miscellaneous/services/document.type.service';
import { SHAL_ENTITY_TYPE } from 'src/app/routing/search/search.component';
import { Document } from "../../../miscellaneous/model/Document";
import { DocumentType } from "../../../miscellaneous/model/DocumentType";
import { CharacterPrice } from '../../model/CharacterPrice';
import { Confrere } from '../../model/Confrere';
import { JournalType } from '../../model/JournalType';
import { NoticeType } from '../../model/NoticeType';
import { NoticeTypeFamily } from '../../model/NoticeTypeFamily';
import { Provision } from '../../model/Provision';
import { Shal } from '../../model/Shal';
import { CharacterPriceService } from '../../services/character.price.service';
import { ConfrereService } from '../../services/confrere.service';
import { JournalTypeService } from '../../services/journal.type.service';
import { NoticeTypeFamilyService } from '../../services/notice.type.family.service';
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

  SEPARATOR_KEY_CODES = SEPARATOR_KEY_CODES;
  SHAL_ENTITY_TYPE = SHAL_ENTITY_TYPE;
  CONFRERE_BALO_ID = CONFRERE_BALO_ID;

  filteredDepartments: Observable<Department[]> | undefined;

  journalTypes: JournalType[] = [] as Array<JournalType>;

  confreres: Confrere[] = [] as Array<Confrere>;
  filteredConfreres: Observable<Confrere[]> | undefined;

  characterPrice: CharacterPrice = {} as CharacterPrice;

  noticeTypeFamilies: NoticeTypeFamily[] = [] as Array<NoticeTypeFamily>;

  noticeTypes: NoticeType[] = [] as Array<NoticeType>;
  filteredNoticeTypes: Observable<NoticeType[]> | undefined;

  publicationDocument: Document = {} as Document;
  proofReadingDocument: Document = {} as Document;
  publicationCertificateDocument: Document = {} as Document;

  documentTypes: DocumentType[] = [] as Array<DocumentType>;


  constructor(private formBuilder: FormBuilder,
    private confrereService: ConfrereService,
    private characterPriceService: CharacterPriceService,
    public confrereDialog: MatDialog,
    private noticeTypeService: NoticeTypeService,
    private noticeTypeFamilyService: NoticeTypeFamilyService,
    private documentTypeService: DocumentTypeService,
    private journalTypeService: JournalTypeService,
  ) { }

  ngOnInit() {
    this.confrereService.getConfreres().subscribe(response => {
      this.confreres = response;
      if (this.provision.shal!.confrere == null || this.provision.shal!.confrere == undefined || this.provision.shal!.confrere.id == undefined)
        this.provision.shal!.confrere = this.getJssConfrere();
    })

    this.noticeTypeFamilyService.getNoticeTypeFamilies().subscribe(response => {
      this.noticeTypeFamilies = response;
    })

    this.noticeTypeService.getNoticeTypes().subscribe(response => {
      this.noticeTypes = response;
    })

    this.filteredDepartments = this.shalForm.get("department")?.valueChanges.pipe(
      startWith(''),
      map(value => this._filterDepartment(value)),
    );

    this.filteredConfreres = this.shalForm.get("confrere")?.valueChanges.pipe(
      startWith(''),
      map(value => this._filterConfrere(value)),
    );

    this.filteredNoticeTypes = this.shalForm.get("noticeTypes")?.valueChanges.pipe(
      startWith(''),
      map(value => this._filterNoticeType(value)),
    );

    this.journalTypeService.getJournalTypes().subscribe(response => {
      this.journalTypes = response;
    })
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
      this.documentTypeService.getDocumentTypes().subscribe(response => {
        this.documentTypes = response;
        this.publicationDocument = getDocument(PUBLICATION_TIERS_DOCUMENT_TYPE_CODE, this.provision.shal!, this.documentTypes);
        this.proofReadingDocument = getDocument(PROOF_READING_DOCUMENT_TYPE_CODE, this.provision.shal!, this.documentTypes);
        this.publicationCertificateDocument = getDocument(PUBLICATION_CERTIFICATE_DOCUMENT_TYPE_CODE, this.provision.shal!, this.documentTypes);

      })

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
    journalType: ['', [Validators.required]],
    department: ['', [Validators.required, this.checkAutocompleteField("department")]],
    confrere: ['', [this.checkAutocompleteField("confrere")]],
    publicationDate: ['', [Validators.required]],
    isRedactedByJss: ['', [Validators.required]],
    notice: ['', [Validators.required]],
    noticeTypes: [''],
    noticeTypeFamily: ['', [Validators.required]],
    isLogo: ['', [Validators.required]],
    isHeader: ['', [Validators.required]],
    isPictureBaloPackage: ['', [Validators.required]],
    isLegalDisplay: ['', [Validators.required]],
    posterProductionJSSPrice: ['', [this.checkFieldFilledIfIsLegalDisplay("posterProductionJSSPrice")]],
    posterProductionPrice: ['', [this.checkFieldFilledIfIsLegalDisplay("posterProductionPrice")]],
    billPostingPrice: ['', [this.checkFieldFilledIfIsLegalDisplay("billPostingPrice")]],
    billPostingJSSPrice: ['', [this.checkFieldFilledIfIsLegalDisplay("billPostingJSSPrice")]],
    bailiffReportPrice: ['', [this.checkFieldFilledIfIsLegalDisplay("bailiffReportPrice")]],
    bailiffReportJSSPrice: ['', [this.checkFieldFilledIfIsLegalDisplay("bailiffReportJSSPrice")]],
    isPublicationCertificateDocument: ['', [Validators.required]],
    isProofReadingDocument: ['', [Validators.required]],
  });

  getFormStatus(): boolean {
    this.shalForm.markAllAsTouched();
    return this.shalForm.valid && this.provision.shal!.noticeTypes && this.provision.shal!.noticeTypes.length > 0;
  }

  updateCharacterPrice() {
    if (this.provision.shal!.department != undefined && this.provision.shal!.publicationDate != undefined)
      this.characterPriceService.getCharacterPrice(this.provision.shal!.department, this.provision.shal!.publicationDate).subscribe(response => {
        if (response != null)
          this.characterPrice = response;
      })
  }

  cleanTextarea(fieldName: string) {
    let fieldValue = this.shalForm.get(fieldName)?.value != undefined ? this.shalForm.get(fieldName)?.value : "";
    var l = fieldValue.replace(/ +(?= )/g, '').replace(/(\r\n|\r|\n){2,}/g, '$1\n');

    this.shalForm.get(fieldName)?.setValue(l);
  }

  countCharacterNumber(fieldName: string) {
    let fieldValue = this.shalForm.get(fieldName)?.value != undefined ? this.shalForm.get(fieldName)?.value : "";
    return fieldValue.replace(/ +(?= )/g, '').replace(/(\r\n|\r|\n){2,}/g, ' ').trim().length;
  }

  toggleTabs() {
    if (this.tabs != undefined)
      this.tabs.realignInkBar();
  }

  public displayLabel(object: any): string {
    return object ? object.label : '';
  }

  public displayConfrere(object: Confrere): string {
    return object ? object.denomination : '';
  }

  private _filterDepartment(value: string): Department[] {
    const filterValue = (value != undefined && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    if (this.provision.shal!.confrere != null && this.provision.shal!.confrere != undefined
      && this.provision.shal!.confrere.departments != null && this.provision.shal!.confrere.departments != undefined)
      return this.provision.shal!.confrere.departments.filter(department => department.label != undefined && department.label.toLowerCase().includes(filterValue));
    return [];
  }

  private _filterConfrere(value: string): Confrere[] {
    const filterValue = (value != undefined && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return this.confreres.filter(confrere => confrere.denomination != undefined && confrere.denomination.toLowerCase().includes(filterValue));
  }

  private _filterNoticeType(value: string): NoticeType[] {
    const filterValue = (value != undefined && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return this.noticeTypes.filter(noticeType => noticeType.label != undefined && noticeType.label.toLowerCase().includes(filterValue) && noticeType.noticeTypeFamily.id == this.provision.shal!.noticeTypeFamily.id);
  }

  checkAutocompleteField(fieldName: string): ValidationErrors | null {
    return (control: AbstractControl): ValidationErrors | null => {
      const root = control.root as FormGroup;

      const fieldValue = root.get(fieldName)?.value;
      if (fieldValue != undefined && fieldValue != null && (fieldValue.id == undefined || fieldValue.id == null))
        return {
          notFilled: true
        };
      return null;
    };
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


  checkFieldFilledIfIsLegalDisplay(fieldName: string): ValidationErrors | null {
    return (control: AbstractControl): ValidationErrors | null => {
      const root = control.root as FormGroup;

      const fieldValue = root.get(fieldName)?.value;
      if (this.provision.shal! && this.provision.shal!.isLegalDisplay && this.checkFieldFilledIfIsOrder(fieldName) && (fieldValue == undefined || fieldValue == null || fieldValue.length == 0))
        return {
          notFilled: true
        };
      return null;
    };
  }


  checkFieldFilledIfIsOrder(fieldName: string): ValidationErrors | null {
    return (control: AbstractControl): ValidationErrors | null => {
      const root = control.root as FormGroup;

      const fieldValue = root.get(fieldName)?.value;
      if (instanceOfCustomerOrder(this.provision.quotation) && (fieldValue == undefined || fieldValue == null || fieldValue.length == 0))
        return {
          notFilled: true
        };
      return null;
    };
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
    exportRtfAction.actionClick = (element: Audit): void => {
      downloadTextAsFile("Affaire " + this.provision.id + " - " + formatDate(new Date(element.datetime)) + ".rtf", convertToRtf(element.newValue));
    }
    exportRtfAction.actionTooltip = "Télécharger en .rtf";
    exportRtfAction.actionIcon = "description";
    historyActions.push(exportRtfAction);
    return historyActions;
  }

  compareWithId = compareWithId;
}
