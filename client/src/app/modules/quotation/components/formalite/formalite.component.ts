import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatTabChangeEvent } from '@angular/material/tabs';
import { FORMALITE_STATUS_WAITING_DOCUMENT_AUTHORITY, GUICHET_UNIQUE_STATUS_AMENDMENT_PENDING, GUICHET_UNIQUE_STATUS_AMENDMENT_SIGNATURE_PENDING } from 'src/app/libs/Constants';
import { instanceOfCustomerOrder } from 'src/app/libs/TypeHelper';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { FORMALITE_ENTITY_TYPE, PROVISION_ENTITY_TYPE } from 'src/app/routing/search/search.component';
import { HabilitationsService } from '../../../../services/habilitations.service';
import { UserPreferenceService } from '../../../../services/user.preference.service';
import { ConstantService } from '../../../miscellaneous/services/constant.service';
import { Affaire } from '../../model/Affaire';
import { Formalite } from '../../model/Formalite';
import { FormaliteStatus } from '../../model/FormaliteStatus';
import { IQuotation } from '../../model/IQuotation';
import { Provision } from '../../model/Provision';
import { FormaliteStatusService } from '../../services/formalite.status.service';
import { FormaliteGuichetUnique } from '../../model/guichet-unique/FormaliteGuichetUnique';
import { FormaliteInfogreffe } from '../../model/infogreffe/FormaliteInfogreffe';
import { instanceOfFormaliteInfogreffe } from '../../../../libs/TypeHelper';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { FormaliteDialogChoose } from '../../model/FormaliteDialogChoose';
import { FormaliteService } from '../../services/formalite.service';
import { FormaliteAssociateDialog } from '../formalite-associate-dialog/formalite-associate-dialog';
import { FormaliteGuichetUniqueService } from 'src/app/modules/miscellaneous/services/formalite.guichet.unique.service';

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

  constructor(
    private formBuilder: FormBuilder,
    private constantService: ConstantService,
    private habilitationsService: HabilitationsService,
    private formaliteStatusService: FormaliteStatusService,
    private userPreferenceService: UserPreferenceService,
    private formaliteService: FormaliteService,
    private formaliteGuichetUniqueService: FormaliteGuichetUniqueService,
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
    //this.displayedColumns.push({ id: "", fieldName: "", label: "Dernière relance de l'AC" } as SortTableColumn<FormaliteGuichetUnique | FormaliteInfogreffe>);

    this.tableAction.push({
      actionIcon: 'visibility', actionName: "Autoriser à Signer/payer ?", actionClick: (column: SortTableAction<FormaliteGuichetUnique | FormaliteInfogreffe>, element: FormaliteGuichetUnique | FormaliteInfogreffe, event: any) => {
        if (!instanceOfFormaliteInfogreffe(element))
          if (element.status.code == GUICHET_UNIQUE_STATUS_AMENDMENT_PENDING || element.status.code == GUICHET_UNIQUE_STATUS_AMENDMENT_SIGNATURE_PENDING)
            this.updateIsAuthorizedToSign(element);
      }, display: true,
    } as SortTableAction<FormaliteGuichetUnique | FormaliteInfogreffe>);

    if (this.formalite.formalitesGuichetUnique)
      for (let i = 0; i < this.formalite.formalitesGuichetUnique.length; i++)
        this.formalites?.push(this.formalite.formalitesGuichetUnique[i]);
    if (this.formalite.formalitesInfogreffe)
      for (let i = 0; i < this.formalite.formalitesInfogreffe.length; i++)
        this.formalites?.push(this.formalite.formalitesInfogreffe[i]);
  }

  updateIsAuthorizedToSign(element: FormaliteGuichetUnique) {
    if (element && element.isAuthorizedToSign)
      element.isAuthorizedToSign = false;
    else element.isAuthorizedToSign = true;
    this.formaliteGuichetUniqueService.addOrUpdateFormaliteGuichetUnique(element);
  }

  associateLiasseWithProvider() {
    const dialogRef: MatDialogRef<FormaliteAssociateDialog> = this.associateFormaliteLiasseDialog.open(FormaliteAssociateDialog, {
      maxWidth: "600px",
    });

    dialogRef.afterClosed().subscribe((result: FormaliteDialogChoose) => {
      if (result && result.formaliteGuichetUnique && result.competentAuthorityServiceProvider.id == this.competentAuthorityInpi.id)
        this.formalite.formalitesGuichetUnique.push(result.formaliteGuichetUnique);

      if (result && result.formaliteInfogreffe && result.competentAuthorityServiceProvider.id == this.competentAuthorityInfogreffe.id)
        this.formalite.formalitesInfogreffe.push(result.formaliteInfogreffe);

      this.formaliteService.addOrUpdateFormalite(this.formalite).subscribe(response => {
        this.formalite = response;
      });
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
