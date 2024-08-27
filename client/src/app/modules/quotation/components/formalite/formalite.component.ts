import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { MatTabChangeEvent } from '@angular/material/tabs';
import { FORMALITE_STATUS_WAITING_DOCUMENT_AUTHORITY, GUICHET_UNIQUE_BASE_URL, GUICHET_UNIQUE_STATUS_AMENDMENT_PENDING, GUICHET_UNIQUE_STATUS_AMENDMENT_SIGNATURE_PENDING, INFOGREFFE_BASE_URL } from 'src/app/libs/Constants';
import { instanceOfCustomerOrder, instanceOfFormaliteGuichetUnique } from 'src/app/libs/TypeHelper';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { FORMALITE_ENTITY_TYPE, PROVISION_ENTITY_TYPE } from 'src/app/routing/search/search.component';
import { instanceOfFormaliteInfogreffe } from '../../../../libs/TypeHelper';
import { HabilitationsService } from '../../../../services/habilitations.service';
import { UserPreferenceService } from '../../../../services/user.preference.service';
import { ConstantService } from '../../../miscellaneous/services/constant.service';
import { Affaire } from '../../model/Affaire';
import { Formalite } from '../../model/Formalite';
import { FormaliteDialogChoose } from '../../model/FormaliteDialogChoose';
import { FormaliteStatus } from '../../model/FormaliteStatus';
import { FormaliteGuichetUnique } from '../../model/guichet-unique/FormaliteGuichetUnique';
import { FormaliteInfogreffe } from '../../model/infogreffe/FormaliteInfogreffe';
import { IQuotation } from '../../model/IQuotation';
import { Provision } from '../../model/Provision';
import { FormaliteStatusService } from '../../services/formalite.status.service';
import { FormaliteAssociateDialog } from '../formalite-associate-dialog/formalite-associate-dialog';

@Component({
  selector: 'formalite',
  templateUrl: './formalite.component.html',
  styleUrls: ['./formalite.component.css']
})
export class FormaliteComponent implements OnInit {

  @Input() formalite: Formalite = {} as Formalite;
  @Input() provision: Provision | undefined;
  @Input() affaire: Affaire | undefined;
  @Input() editMode: boolean = false;
  @Input() instanceOfCustomerOrder: boolean = false;
  @Input() isStatusOpen: boolean = true;
  @Input() quotation: IQuotation | undefined;
  @Output() provisionChange: EventEmitter<Provision> = new EventEmitter<Provision>();


  FORMALITE_STATUS_WAITING_DOCUMENT_AUTHORITY = FORMALITE_STATUS_WAITING_DOCUMENT_AUTHORITY;
  PROVISION_ENTITY_TYPE = PROVISION_ENTITY_TYPE;
  GUICHET_UNIQUE_STATUS_AMENDMENT_PENDING = GUICHET_UNIQUE_STATUS_AMENDMENT_PENDING;
  GUICHET_UNIQUE_STATUS_AMENDMENT_SIGNATURE_PENDING = GUICHET_UNIQUE_STATUS_AMENDMENT_SIGNATURE_PENDING;
  instanceOfCustomerOrderFn = instanceOfCustomerOrder;

  competentAuthorityInpi = this.constantService.getCompetentAuthorityInpi();
  competentAuthorityInfogreffe = this.constantService.getCompetentAuthorityInfogreffe();

  formaliteStatus: FormaliteStatus[] | undefined;
  displayedColumns: SortTableColumn<FormaliteGuichetUnique | FormaliteInfogreffe>[] = [] as Array<SortTableColumn<FormaliteGuichetUnique | FormaliteInfogreffe>>;
  formalites: (FormaliteGuichetUnique | FormaliteInfogreffe)[] = [] as Array<FormaliteGuichetUnique | FormaliteInfogreffe>;
  tableAction: SortTableAction<FormaliteGuichetUnique | FormaliteInfogreffe>[] = [] as Array<SortTableAction<FormaliteGuichetUnique | FormaliteInfogreffe>>;
  searchText: string | undefined;

  constructor(
    private formBuilder: FormBuilder,
    private constantService: ConstantService,
    private habilitationsService: HabilitationsService,
    private formaliteStatusService: FormaliteStatusService,
    private userPreferenceService: UserPreferenceService,
    public associateFormaliteLiasseDialog: MatDialog,
  ) { }

  formaliteForm = this.formBuilder.group({});
  canAddNewInvoice() {
    return this.habilitationsService.canAddNewInvoice();
  }
  FORMALITE_ENTITY_TYPE = FORMALITE_ENTITY_TYPE;

  ngOnInit() {
    this.formaliteStatusService.getFormaliteStatus().subscribe(response => { this.formaliteStatus = response });
    this.restoreTab();
    this.displayedColumns = [];
    this.displayedColumns.push({ id: "competentAuthorityServiceProvider", fieldName: "competentAuthorityServiceProvider", label: "Fournisseur de service", valueFonction: (element: FormaliteGuichetUnique | FormaliteInfogreffe) => { if (instanceOfFormaliteInfogreffe(element)) return this.competentAuthorityInfogreffe.label; return this.competentAuthorityInpi.label; } } as unknown as SortTableColumn<FormaliteGuichetUnique | FormaliteInfogreffe>);
    this.displayedColumns.push({ id: "referenceProvider", fieldName: "referenceProvider", label: "Référence fournisseur", valueFonction: (element: FormaliteGuichetUnique | FormaliteInfogreffe) => { if (instanceOfFormaliteInfogreffe(element)) return element.identifiantFormalite.formaliteNumero; return element.liasseNumber } } as unknown as SortTableColumn<FormaliteGuichetUnique | FormaliteInfogreffe>);
    this.displayedColumns.push({ id: "formaliteStatus", fieldName: "formaliteStatus", label: "Statut de la formalité", valueFonction: (element: FormaliteGuichetUnique | FormaliteInfogreffe) => { if (instanceOfFormaliteInfogreffe(element)) return ""; return element.status.label; } } as unknown as SortTableColumn<FormaliteGuichetUnique | FormaliteInfogreffe>);
    this.displayedColumns.push({ id: "isAuthorizedToSign", fieldName: "isAuthorizedToSign", label: "Autorisé à signer/payer", valueFonction: (element: FormaliteGuichetUnique | FormaliteInfogreffe) => { if (instanceOfFormaliteInfogreffe(element)) return ""; if (!instanceOfFormaliteInfogreffe(element) && element.isAuthorizedToSign) return "Oui"; return "Non"; } } as unknown as SortTableColumn<FormaliteGuichetUnique | FormaliteInfogreffe>);

    this.tableAction.push({
      actionIcon: 'approval', actionName: "Autoriser à Signer/payer ?", actionClick: (column: SortTableAction<FormaliteGuichetUnique | FormaliteInfogreffe>, element: FormaliteGuichetUnique | FormaliteInfogreffe, event: any) => {
        if (instanceOfFormaliteGuichetUnique(element) && this.editMode)
          if (element.status.code == GUICHET_UNIQUE_STATUS_AMENDMENT_PENDING || element.status.code == GUICHET_UNIQUE_STATUS_AMENDMENT_SIGNATURE_PENDING)
            element.isAuthorizedToSign = true;
      }, display: true,
    } as SortTableAction<FormaliteGuichetUnique | FormaliteInfogreffe>);

    this.tableAction.push({
      actionIcon: 'link', actionName: "Ouvrir le lien", actionClick: (column: SortTableAction<FormaliteGuichetUnique | FormaliteInfogreffe>, element: FormaliteGuichetUnique | FormaliteInfogreffe, event: any) => {
        if (instanceOfFormaliteGuichetUnique(element)) {
          if (element.isFormality)
            window.open(GUICHET_UNIQUE_BASE_URL + element.id, "_blank");
          if (element.isAnnualAccounts)
            window.open(GUICHET_UNIQUE_BASE_URL + "annual-accounts/" + element.id, "_blank");
          if (element.isActeDeposit)
            window.open(GUICHET_UNIQUE_BASE_URL + "acte-deposits/" + element.id, "_blank");
        }
        if (instanceOfFormaliteInfogreffe(element))
          if (element.urlReprise)
            window.open(INFOGREFFE_BASE_URL + element.urlReprise);
      }, display: true,
    } as SortTableAction<FormaliteGuichetUnique | FormaliteInfogreffe>);

    this.tableAction.push({
      actionIcon: 'delete', actionName: "Supprimer la liasse", actionClick: (column: SortTableAction<FormaliteGuichetUnique | FormaliteInfogreffe>, element: FormaliteGuichetUnique | FormaliteInfogreffe, event: any) => {
        if (this.editMode) {
          if (instanceOfFormaliteGuichetUnique(element))
            this.formalite.formalitesGuichetUnique = this.formalite.formalitesGuichetUnique.slice(this.formalite.formalitesGuichetUnique.indexOf(element), 1);
          if (instanceOfFormaliteInfogreffe(element))
            this.formalite.formalitesInfogreffe = this.formalite.formalitesInfogreffe.slice(this.formalite.formalitesInfogreffe.indexOf(element), 1);
          this.setFormaliteTableData();
        }
      }, display: true,
    } as SortTableAction<FormaliteGuichetUnique | FormaliteInfogreffe>);

    this.setFormaliteTableData();
  }

  setFormaliteTableData() {
    this.formalites = [] as Array<FormaliteGuichetUnique | FormaliteInfogreffe>;
    if (this.formalite.formalitesGuichetUnique)
      for (let i = 0; i < this.formalite.formalitesGuichetUnique.length; i++)
        this.formalites?.push(this.formalite.formalitesGuichetUnique[i]);
    if (this.formalite.formalitesInfogreffe)
      for (let i = 0; i < this.formalite.formalitesInfogreffe.length; i++)
        this.formalites?.push(this.formalite.formalitesInfogreffe[i]);
  }

  applyFilter(filterValue: any) {
    let filterValueCast = (filterValue as HTMLInputElement);
    filterValue = filterValueCast.value.trim();
    this.searchText = filterValue.toLowerCase();
  }

  associateLiasseWithFormalite() {
    const dialogRef: MatDialogRef<FormaliteAssociateDialog> = this.associateFormaliteLiasseDialog.open(FormaliteAssociateDialog, {
      maxWidth: "600px",
    });

    dialogRef.afterClosed().subscribe((result: FormaliteDialogChoose) => {
      if (result && result.formaliteGuichetUnique && result.competentAuthorityServiceProvider.id == this.competentAuthorityInpi.id) {
        if (this.formalite.formalitesGuichetUnique == null)
          this.formalite.formalitesGuichetUnique = [] as Array<FormaliteGuichetUnique>;
        this.formalite.formalitesGuichetUnique.push(result.formaliteGuichetUnique);
      }

      if (result && result.formaliteInfogreffe && result.competentAuthorityServiceProvider.id == this.competentAuthorityInfogreffe.id) {
        if (this.formalite.formalitesInfogreffe == null)
          this.formalite.formalitesInfogreffe = [] as Array<FormaliteInfogreffe>;
        this.formalite.formalitesInfogreffe.push(result.formaliteInfogreffe);
      }
      this.setFormaliteTableData();
    });
  }

  ngOnChanges(changes: SimpleChanges) {

  }

  getFormStatus() {
    this.formaliteForm.markAllAsTouched();
    let status = this.formaliteForm.valid;

    return status;
  }

  provisionChangeFunction() {
    this.provisionChange.emit(this.provision);
  }

  //Tabs management
  index: number = 0;
  onTabChange(event: MatTabChangeEvent) {
    this.userPreferenceService.setUserTabsSelectionIndex('formalite', event.index);
  }

  restoreTab() {
    this.index = this.userPreferenceService.getUserTabsSelectionIndex('formalite');
  }
}
