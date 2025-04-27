import { Component, OnInit, ViewChild } from '@angular/core';
import { MatTabChangeEvent } from '@angular/material/tabs';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { NotificationService } from 'src/app/modules/miscellaneous/services/notification.service';
import { HabilitationsService } from 'src/app/services/habilitations.service';
import { AFFAIRE_ENTITY_TYPE } from '../../../../routing/search/search.component';
import { AppService } from '../../../../services/app.service';
import { UserPreferenceService } from '../../../../services/user.preference.service';
import { Notification } from '../../../miscellaneous/model/Notification';
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
    protected habilitationService: HabilitationsService,
    private userPreferenceService: UserPreferenceService,
    private notificationService: NotificationService
  ) { }

  ngOnInit() {
    let idAffaire = this.activatedRoute.snapshot.params.id;

    if (idAffaire)
      this.affaireService.getAffaire(idAffaire).subscribe(response => {
        if (response) {
          this.affaire = response;
          this.restoreTab();
          this.orderingSearch.affaire = this.affaire;
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
      if (this.affaire.siret)
        if (((this.affaire.siret) as any).siret)
          this.affaire.siret = ((this.affaire.siret) as any).siret;

      if (this.affaire.siren)
        if (((this.affaire.siren) as any).siren)
          this.affaire.siren = ((this.affaire.siren) as any).siren;
      this.affaireService.addOrUpdateAffaire(this.affaire).subscribe(response => {
        this.affaire = response;
        this.editMode = false;
      })
    }
    return false;
  }

  refreshAffaire() {
    if (this.affaire)
      this.affaireService.refreshAffaire(this.affaire).subscribe(response => {
        this.affaire = response;
      })
  }

  getFormsStatus(): boolean {

    let addAffaireComponentStatus = this.addAffaireComponent?.getFormStatus();
    if (addAffaireComponentStatus)
      return true;
    return false;
  }

  //Tabs management
  index: number = 0;
  onTabChange(event: MatTabChangeEvent) {
    this.userPreferenceService.setUserTabsSelectionIndex('affaire', event.index);
  }

  restoreTab() {
    this.index = this.userPreferenceService.getUserTabsSelectionIndex('affaire');
  }


  addNewNotification() {
    this.appService.addPersonnalNotification(() => this.affaireNotification = undefined, this.affaireNotification, undefined, undefined, undefined, undefined, undefined, undefined, undefined, this.affaire);
  }

  affaireNotification: Notification[] | undefined;

  getNotificationForAffaire() {
    if (this.affaireNotification == undefined) {
      if (this.affaire) {
        this.affaireNotification = [];
        this.notificationService.getNotificationsForAffaire(this.affaire.id).subscribe(response => this.affaireNotification = response);
      }
    }
    return this.affaireNotification;
  }

  canDisplayNotifications() {
    return this.habilitationService.canDisplayNotifications();
  }

}
