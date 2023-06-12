import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { AFFAIRE_ENTITY_TYPE } from '../../../../routing/search/search.component';
import { AppService } from '../../../../services/app.service';
import { Affaire } from '../../model/Affaire';
import { AffaireSearch } from '../../model/AffaireSearch';
import { OrderingSearch } from '../../model/OrderingSearch';
import { QuotationSearch } from '../../model/QuotationSearch';
import { AffaireService } from '../../services/affaire.service';
import { AddAffaireComponent } from '../add-affaire/add-affaire.component';

@Component({
  selector: 'app-affaire',
  templateUrl: './affaire.component.html',
  styleUrls: ['./affaire.component.css']
})
export class AffaireComponent implements OnInit {

  affaire: Affaire | undefined;
  editMode: boolean = false;
  @ViewChild(AddAffaireComponent) addAffaireComponent: AddAffaireComponent | undefined;
  AFFAIRE_ENTITY_TYPE = AFFAIRE_ENTITY_TYPE;
  orderingSearch: OrderingSearch = {} as OrderingSearch;
  quotationSearch: QuotationSearch = {} as QuotationSearch;
  provisionSearch: AffaireSearch = {} as AffaireSearch;

  saveObservableSubscription: Subscription = new Subscription;

  constructor(
    private activatedRoute: ActivatedRoute,
    private appService: AppService,
    private affaireService: AffaireService,
  ) { }

  ngOnInit() {
    let idAffaire = this.activatedRoute.snapshot.params.id;

    if (idAffaire)
      this.affaireService.getAffaire(idAffaire).subscribe(response => {
        if (response) {
          this.affaire = response;
          this.orderingSearch.affaires = [this.affaire];
          this.quotationSearch.affaires = [this.affaire];
          this.appService.changeHeaderTitle("Affaire - " + (this.affaire.denomination ? this.affaire.denomination : this.affaire.firstname + " " + this.affaire.lastname));
          this.provisionSearch.affaire = this.affaire;
        }
      })

    this.saveObservableSubscription = this.appService.saveObservable.subscribe(response => {
      if (response)
        if (this.editMode)
          this.saveAffaire()
        else
          this.editAffaire()
    });
  }

  ngOnDestroy() {
    this.saveObservableSubscription.unsubscribe();
  }

  editAffaire() {
    this.editMode = true;
  }

  saveAffaire(): boolean {
    if (this.getFormsStatus() && this.affaire) {
      this.affaireService.addOrUpdateAffaire(this.affaire).subscribe(response => {
        this.affaire = response;
        this.editMode = false;
      })
    }
    return false;
  }

  getFormsStatus(): boolean {

    let addAffaireComponentStatus = this.addAffaireComponent?.getFormStatus();
    if (addAffaireComponentStatus)
      return true;
    return false;
  }

}
