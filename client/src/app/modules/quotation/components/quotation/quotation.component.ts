import { Component, OnInit, ViewChild, ViewChildren } from '@angular/core';
import { MatAccordion } from '@angular/material/expansion';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute, Router, UrlSegment } from '@angular/router';
import { AppService } from 'src/app/app.service';
import { QUOTATION_DOCUMENT_TYPE_CODE } from 'src/app/libs/Constants';
import { instanceOfCustomerOrder, instanceOfQuotation } from 'src/app/libs/TypeHelper';
import { QUOTATION_ENTITY_TYPE } from 'src/app/routing/search/search.component';
import { SearchService } from 'src/app/search.service';
import { Affaire } from '../../model/Affaire';
import { IQuotation } from '../../model/IQuotation';
import { QuotationService } from '../../services/quotation.service';
import { AffaireComponent } from '../affaire/affaire.component';
import { OrderingCustomerComponent } from '../ordering-customer/ordering-customer.component';
import { QuotationManagementComponent } from '../quotation-management/quotation-management.component';

@Component({
  selector: 'quotation',
  templateUrl: './quotation.component.html',
  styleUrls: ['./quotation.component.css']
})
export class QuotationComponent implements OnInit {
  quotation: IQuotation = {} as IQuotation;
  editMode: boolean = false;
  createMode: boolean = false;

  QUOTATION_ENTITY_TYPE = QUOTATION_ENTITY_TYPE;
  QUOTATION_DOCUMENT_TYPE_CODE = QUOTATION_DOCUMENT_TYPE_CODE;

  selectedTabIndex = 0;

  @ViewChild('tabs', { static: false }) tabs: any;
  @ViewChild(OrderingCustomerComponent) orderingCustomerComponent: OrderingCustomerComponent | undefined;
  @ViewChild(QuotationManagementComponent) quotationManagementComponent: QuotationManagementComponent | undefined;
  @ViewChildren(AffaireComponent) affaireComponents: any;
  @ViewChild(MatAccordion) accordion: MatAccordion | undefined;

  filteredAffaires: Affaire[] = [] as Array<Affaire>;

  constructor(private appService: AppService,
    private quotationService: QuotationService,
    private snackBar: MatSnackBar,
    private activatedRoute: ActivatedRoute,
    protected searchService: SearchService,
    private router: Router) { }

  ngOnInit() {
    this.appService.changeHeaderTitle("Devis");

    let idQuotation: number = this.activatedRoute.snapshot.params.id;
    let url: UrlSegment[] = this.activatedRoute.snapshot.url;

    // Load by order
    if (url != undefined && url != null && url[0] != undefined && url[1] != undefined && url[0].path == "order") {
      //TODO : load by order
      this.appService.changeHeaderTitle("Commande " + this.quotation.id);
      // Load by quotation
    } else if (idQuotation != null && idQuotation != undefined) {
      this.quotationService.getQuotation(idQuotation).subscribe(response => {
        this.quotation = response;
        this.appService.changeHeaderTitle("Devis " + this.quotation.id + " - " +
          (this.quotation.quotationStatus != null ? this.quotation.quotationStatus.label : ""));
        this.toggleTabs();
        this.sortAffaires();
        this.filteredAffaires = this.quotation.affaires;
      })
    } else if (this.createMode == false) {
      // Blank page
      this.appService.changeHeaderTitle("Devis");
    }
  }

  toggleTabs() {
    if (this.tabs != undefined)
      this.tabs.realignInkBar();
  }

  saveQuotation() {
    if (this.getFormsStatus())
      this.quotationService.addOrUpdateQuotation(this.quotation).subscribe(response => {
        this.quotation = response;
        this.editMode = false;
        this.router.navigateByUrl('/', { skipLocationChange: true }).then(() =>
          this.router.navigate(['/quotation/', "" + this.quotation.id])
        );
      })
  }

  getFormsStatus(): boolean {
    let orderingCustomerFormStatus = this.orderingCustomerComponent?.getFormStatus();
    let quotationManagementFormStatus = this.quotationManagementComponent?.getFormStatus();

    let affaireStatus = true;
    this.affaireComponents.toArray().forEach((affaireComponent: { getFormStatus: () => any; }) => {
      affaireStatus = affaireStatus && affaireComponent.getFormStatus();
    });

    let errorMessages: string[] = [] as Array<string>;
    if (!orderingCustomerFormStatus)
      errorMessages.push("Onglet Donneur d'ordre");
    if (!quotationManagementFormStatus)
      errorMessages.push("Onglet Gestion du devis");
    if (!affaireStatus)
      errorMessages.push("Onglet Prestations");
    if (errorMessages.length > 0) {
      let errorMessage = "Les onglets suivants ne sont pas correctement remplis. Veuillez les compléter avant de sauvegarder : " + errorMessages.join(" / ");
      let sb = this.snackBar.open(errorMessage, 'Fermer', {
        duration: 60 * 1000, panelClass: ["red-snackbar"]
      });
      sb.onAction().subscribe(() => {
        sb.dismiss();
      });
      return false;
    }
    return true;
  }

  editQuotation() {
    this.editMode = true;
  }

  createQuotation() {
    this.createMode = true;
    this.editMode = true;
    this.quotation = {} as IQuotation;
    this.appService.changeHeaderTitle("Nouveau devis");
    this.toggleTabs();
  }

  openSearch() {
    this.searchService.openSearchOnModule(QUOTATION_ENTITY_TYPE);
  }


  applyFilter(filterValue: any) {
    if (filterValue == null || filterValue == undefined || filterValue.length == 0) {
      this.filteredAffaires = this.quotation.affaires;
      return;
    }
    this.filteredAffaires = [] as Array<Affaire>;
    if (this.quotation && this.quotation.affaires) {
      this.quotation.affaires.forEach(affaire => {
        const dataStr = JSON.stringify(affaire).toLowerCase();
        if (dataStr.indexOf(filterValue.value.toLowerCase()) >= 0)
          this.filteredAffaires.push(affaire);
      })
    }
  }

  createAffaire() {
    if (this.quotation && !this.quotation.affaires)
      this.quotation.affaires = [] as Array<Affaire>;
    let affaire = {} as Affaire;
    this.quotation.affaires.push(affaire);
    this.sortAffaires();
    this.applyFilter(null);
  }

  deleteAffaire(index: number) {
    if (this.filteredAffaires && this.quotation && this.quotation.affaires) {
      for (let i = 0; i < this.quotation.affaires.length; i++) {
        const affaire = this.quotation.affaires[i];
        if (this.sameAffaire(affaire, this.filteredAffaires[index]))
          this.quotation.affaires.splice(i, 1);
      }
    }
    this.applyFilter(null);
  }

  sameAffaire(p1: Affaire, p2: Affaire): boolean {
    return JSON.stringify(p1).toLowerCase() == JSON.stringify(p2).toLowerCase();
  }

  sortAffaires() {
    if (this.quotation && this.quotation.affaires)
      this.quotation.affaires.sort((a: Affaire, b: Affaire) => {
        if (a == null && b != null)
          return -1;
        if (b != null && b == null)
          return 1;
        if (a.id == null && b.id != null)
          return -1;
        if (b.id != null && b.id == null)
          return 1;
        if (a == null && b == null)
          return 0;
        let nameA = "";
        let nameB = "";
        if (a.isIndividual) {
          nameA = (a.firstname != null ? a.firstname : "") + (a.lastname != null ? a.lastname : "");
        } else {
          nameA = a.denomination;
        }
        if (b.isIndividual) {
          nameB = (b.firstname != null ? b.firstname : "") + (b.lastname != null ? b.lastname : "");
        } else {
          nameB = b.denomination;
        }
        return nameA.localeCompare(nameB);
      })
  }

  sendQuotation() {
    //TODO
  }

  confirmStatus() {
    //TODO
  }

  instanceOfQuotation = instanceOfQuotation;
  instanceOfCustomerOrder = instanceOfCustomerOrder;

}
