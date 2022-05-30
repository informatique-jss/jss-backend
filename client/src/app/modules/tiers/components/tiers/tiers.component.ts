import { Component, OnInit, ViewChild, ViewEncapsulation } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute, Router, UrlSegment } from '@angular/router';
import { AppService } from 'src/app/app.service';
import { isTiersTypeProspect } from 'src/app/libs/CompareHelper';
import { TIERS_ENTITY_TYPE } from 'src/app/routing/search/search.component';
import { SearchService } from 'src/app/search.service';
import { ITiers } from '../../model/ITiers';
import { Responsable } from '../../model/Responsable';
import { Tiers } from '../../model/Tiers';
import { TiersDocument } from '../../model/TiersDocument';
import { TiersDocumentType } from '../../model/TiersDocumentType';
import { TiersService } from '../../services/tiers.service';
import { DocumentManagementComponent } from '../document-management/document-management.component';
import { ResponsableMainComponent } from '../responsable-main/responsable-main.component';
import { SettlementBillingComponent } from '../settlement-billing/settlement-billing.component';
import { PrincipalComponent } from '../tiers-main/tiers-main.component';

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

  TIERS_ENTITY_TYPE = TIERS_ENTITY_TYPE;

  selectedTabIndex = 0;

  @ViewChild('tabs', { static: false }) tabs: any;

  @ViewChild(PrincipalComponent) principalFormComponent: PrincipalComponent | undefined;
  @ViewChild(DocumentManagementComponent) documentManagementFormComponent: DocumentManagementComponent | undefined;
  @ViewChild(SettlementBillingComponent) documentSettlementBillingComponent: SettlementBillingComponent | undefined;
  @ViewChild(ResponsableMainComponent) responsableMainComponent: ResponsableMainComponent | undefined;

  constructor(private appService: AppService,
    private tiersService: TiersService,
    private snackBar: MatSnackBar,
    private activatedRoute: ActivatedRoute,
    protected searchService: SearchService,
    private router: Router) { }

  ngOnInit() {
    this.appService.changeHeaderTitle("Tiers / Responsables");

    let idTiers: number = this.activatedRoute.snapshot.params.id;
    let url: UrlSegment[] = this.activatedRoute.snapshot.url;

    // Load by responsable
    if (url != undefined && url != null && url[0] != undefined && url[1] != undefined && url[0].path == "tiers" && url[1].path == "responsable") {
      this.tiersService.getTiersByResponsable(idTiers).subscribe(response => {
        this.tiers = response;
        this.tiersService.setCurrentViewedTiers(this.tiers);
        this.appService.changeHeaderTitle(this.tiers.denomination != null ? this.tiers.denomination : "");
        this.toggleTabs();
        this.selectedTabIndex = 1;
        this.responsableMainComponent?.setSelectedResponsableId(idTiers);
      })
      // Load by tiers
    } else if (idTiers != null && idTiers != undefined) {
      this.tiersService.getTiers(idTiers).subscribe(response => {
        this.tiers = response;
        this.tiersService.setCurrentViewedTiers(this.tiers);
        this.appService.changeHeaderTitle(this.tiers.denomination != null ? this.tiers.denomination : "");
        this.toggleTabs();
      })
    } else if (this.createMode == false) {
      // Blank page
      this.appService.changeHeaderTitle("Tiers / Responsables");
    }
  }

  isTiersTypeProspect(): boolean {
    return isTiersTypeProspect(this.tiers);
  }

  toggleTabs() {
    if (this.tabs != undefined)
      this.tabs.realignInkBar();
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
    let responsableMainComponentFormStatus = this.responsableMainComponent?.getFormStatus();
    let errorMessages: string[] = [] as Array<string>;
    if (!principalFormStatus)
      errorMessages.push("Onglet Principal");
    if (!documentManagementFormStatus)
      errorMessages.push("Onglet Gestion des pièces");
    if (!documentSettlementBillingFormStatus)
      errorMessages.push("Onglet Réglement, facturation & relance");
    if (!responsableMainComponentFormStatus)
      errorMessages.push("Onglet Responsable");
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
    this.tiers.responsables = [] as Array<Responsable>;
    this.tiersService.setCurrentViewedTiers(this.tiers);
    this.appService.changeHeaderTitle("Nouveau Tiers / Responsable");
    this.toggleTabs();
  }

  openSearch() {
    this.searchService.openSearchOnModule(TIERS_ENTITY_TYPE);
  }

  static getDocument(documentCode: string, tiers: ITiers, tiersDocumentTypes: TiersDocumentType[]) {
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

  static instanceOfTiers(object: ITiers): object is Tiers {
    if (object != null)
      return 'isIndividual' in object;
    return false;
  }

  static instanceOfResponsable(object: ITiers): object is Responsable {
    if (object != null)
      return 'isActive' in object;
    return false;
  }

}
