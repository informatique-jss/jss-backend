import { Component, OnInit, ViewChild, ViewEncapsulation } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute, Router } from '@angular/router';
import { Subject } from 'rxjs';
import { AppService } from 'src/app/app.service';
import { isTiersTypeProspect } from 'src/app/libs/CompareHelper';
import { TIERS_ENTITY_TYPE } from 'src/app/routing/search/search.component';
import { SearchService } from 'src/app/search.service';
import { Tiers } from '../../model/Tiers';
import { TiersDocument } from '../../model/TiersDocument';
import { TiersDocumentType } from '../../model/TiersDocumentType';
import { TiersService } from '../../services/tiers.service';
import { DocumentManagementComponent } from '../document-management/document-management.component';
import { PrincipalComponent } from '../main/main.component';
import { SettlementBillingComponent } from '../settlement-billing/settlement-billing.component';

@Component({
  selector: 'app-tiers',
  templateUrl: './tiers.component.html',
  styleUrls: ['./tiers.component.css'],
  encapsulation: ViewEncapsulation.None,
})
export class TiersComponent implements OnInit {

  tiers: Tiers = {} as Tiers;
  editMode: boolean = false;
  createMode: boolean = false;

  @ViewChild(PrincipalComponent) principalFormComponent: PrincipalComponent | undefined;
  @ViewChild(DocumentManagementComponent) documentManagementFormComponent: DocumentManagementComponent | undefined;
  @ViewChild(SettlementBillingComponent) documentSettlementBillingComponent: SettlementBillingComponent | undefined;

  constructor(private appService: AppService,
    private tiersService: TiersService,
    private snackBar: MatSnackBar,
    private activatedRoute: ActivatedRoute,
    protected searchService: SearchService,
    private router: Router) { }

  ngOnInit() {
    this.appService.changeHeaderTitle("Tiers / Responsables");

    let idTiers: number = this.activatedRoute.snapshot.params.id;

    if (idTiers != null && idTiers != undefined) {
      this.tiersService.getTiers(idTiers).subscribe(response => {
        this.tiers = response;
        this.appService.changeHeaderTitle(this.tiers.denomination != null ? this.tiers.denomination : "");
      })
    } else if (this.createMode == false) {
      // Search for Tiers
      this.appService.changeHeaderTitle("Tiers / Responsables");
    }
  }

  isTiersTypeProspect(): boolean {
    return isTiersTypeProspect(this.tiers);
  }

  saveTiers() {
    if (this.getFormsStatus())
      this.tiersService.addOrUpdateTiers(this.tiers).subscribe(response => {
        this.tiers = response;
        this.editMode = false;
        this.router.navigateByUrl('/', { skipLocationChange: true }).then(() =>
          this.router.navigate(['/tiers/', "" + this.tiers.id])
        );
      })
  }

  getFormsStatus(): boolean {
    let principalFormStatus = this.principalFormComponent?.getFormStatus();
    let documentManagementFormStatus = this.documentManagementFormComponent?.getFormStatus();
    let documentSettlementBillingFormStatus = this.documentSettlementBillingComponent?.getFormStatus();
    let errorMessages: string[] = [] as Array<string>;
    if (!principalFormStatus)
      errorMessages.push("Onglet Principal");
    if (!documentManagementFormStatus)
      errorMessages.push("Onglet Gestion des pièces");
    if (!documentSettlementBillingFormStatus)
      errorMessages.push("Onglet Réglement, facturation & relance");
    if (errorMessages.length > 0) {
      let errorMessage = "Les onglets suivants ne sont pas correctement remplis. Veuillez les compléter avant de sauvegarder : " + errorMessages.join(" / ");
      let sb = this.snackBar.open(errorMessage, 'Fermer', {
        duration: 60 * 1000
      });
      sb.onAction().subscribe(() => {
        sb.dismiss();
      });
      return false;
    }
    return true;
  }

  editTiers() {
    this.editMode = true;
  }

  createTiers() {
    this.createMode = true;
    this.editMode = true;
    this.tiers = {} as Tiers;
    this.appService.changeHeaderTitle("Nouveau Tiers / Responsable");
  }

  openSearch() {
    this.searchService.openSearchOnModule(TIERS_ENTITY_TYPE);
  }

  static getDocument(documentCode: string, tiers: Tiers, tiersDocumentTypes: TiersDocumentType[]) {
    // Tiers not loaded
    if (tiers == null || TiersComponent.getDocumentType(documentCode, tiersDocumentTypes).id == undefined)
      return {} as TiersDocument;

    // No document in Tiers
    if (tiers.documents == null || tiers.documents == undefined) {
      tiers.documents = [] as Array<TiersDocument>;
      let doc = {} as TiersDocument;
      doc.tiersDocumentType = this.getDocumentType(documentCode, tiersDocumentTypes);
      tiers.documents.push(doc);
      return tiers.documents[0];
    }

    // Document currently exists
    if (tiers.documents.length > 0) {
      for (let i = 0; i < tiers.documents.length; i++) {
        const documentFound = tiers.documents[i];
        if (documentFound.tiersDocumentType.code == documentCode) {
          return documentFound;
        }
      }
    }

    // Document not exists, create it
    let doc = {} as TiersDocument;
    doc.tiersDocumentType = this.getDocumentType(documentCode, tiersDocumentTypes);
    tiers.documents.push(doc);
    return tiers.documents[tiers.documents.length - 1];
  }

  static getDocumentType(codeTypeDocument: string, tiersDocumentTypes: TiersDocumentType[]): TiersDocumentType {
    if (tiersDocumentTypes.length > 0) {
      for (let i = 0; i < tiersDocumentTypes.length; i++) {
        const tiersDocumentType = tiersDocumentTypes[i];
        if (tiersDocumentType.code == codeTypeDocument)
          return tiersDocumentType;
      }
    }
    return {} as TiersDocumentType;
  }

}
