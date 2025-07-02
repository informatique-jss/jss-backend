import { AfterContentChecked, ChangeDetectorRef, Component, Input, OnInit, SimpleChanges, ViewChild } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MatTabChangeEvent } from '@angular/material/tabs';
import { copyObject } from 'src/app/libs/GenericHelper';
import { instanceOfResponsable } from 'src/app/libs/TypeHelper';
import { InvoiceSearch } from 'src/app/modules/invoicing/model/InvoiceSearch';
import { City } from 'src/app/modules/miscellaneous/model/City';
import { Country } from 'src/app/modules/miscellaneous/model/Country';
import { Notification } from 'src/app/modules/miscellaneous/model/Notification';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { CityService } from 'src/app/modules/miscellaneous/services/city.service';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { NotificationService } from 'src/app/modules/miscellaneous/services/notification.service';
import { AffaireSearch } from 'src/app/modules/quotation/model/AffaireSearch';
import { OrderingSearch } from 'src/app/modules/quotation/model/OrderingSearch';
import { QuotationSearch } from 'src/app/modules/quotation/model/QuotationSearch';
import { IndexEntity } from 'src/app/routing/search/IndexEntity';
import { RESPONSABLE_ENTITY_TYPE } from 'src/app/routing/search/search.component';
import { AppService } from 'src/app/services/app.service';
import { HabilitationsService } from 'src/app/services/habilitations.service';
import { UserPreferenceService } from '../../../../services/user.preference.service';
import { Document } from "../../../miscellaneous/model/Document";
import { EmployeeService } from '../../../profile/services/employee.service';
import { Responsable } from '../../model/Responsable';
import { RffSearch } from '../../model/RffSearch';
import { SubscriptionPeriodType } from '../../model/SubscriptionPeriodType';
import { Tiers } from '../../model/Tiers';
import { SubscriptionPeriodTypeService } from '../../services/subscription.period.type.service';
import { TiersCategoryService } from '../../services/tiers.category.service';
import { TiersService } from '../../services/tiers.service';
import { TiersTypeService } from '../../services/tiers.type.service';
import { SettlementBillingComponent } from '../settlement-billing/settlement-billing.component';

@Component({
  selector: 'responsable-main',
  templateUrl: './responsable-main.component.html',
  styleUrls: ['./responsable-main.component.css']
})

export class ResponsableMainComponent implements OnInit, AfterContentChecked {

  @Input() tiers: Tiers = {} as Tiers;
  @Input() editMode: boolean = false;

  RESPONSABLE_ENTITY_TYPE = RESPONSABLE_ENTITY_TYPE;

  subscriptionPeriodType12M: SubscriptionPeriodType = this.constantService.getSubscriptionPeriodType12M();

  franceCountry: Country = this.constantService.getCountryFrance();

  filterValue: string = "";

  selectedResponsable: Responsable | null = null;

  selectedResponsableId: number | null = null;

  isSubscriptionPaper: boolean = false;
  isSubscriptionWeb: boolean = false;

  orderingSearch: OrderingSearch = {} as OrderingSearch;
  quotationSearch: QuotationSearch = {} as QuotationSearch;
  provisionSearch: AffaireSearch = {} as AffaireSearch;
  invoiceSearch: InvoiceSearch = {} as InvoiceSearch;
  rffSearch: RffSearch | undefined;

  displayedColumns: SortTableColumn<Responsable>[] = [];
  tableActions: SortTableAction<Responsable>[] = [] as Array<SortTableAction<Responsable>>;
  searchText: string | undefined;

  tiersCategoryPresse = this.constantService.getTiersCategoryPresse();

  @ViewChild(SettlementBillingComponent) documentSettlementBillingComponent: SettlementBillingComponent | undefined;

  constructor(private formBuilder: UntypedFormBuilder,
    private cityService: CityService,
    private appService: AppService,
    protected tiersService: TiersService,
    protected tiersTypeService: TiersTypeService,
    private employeeService: EmployeeService,
    protected habilitationService: HabilitationsService,
    private constantService: ConstantService,
    protected subscriptionPeriodTypeService: SubscriptionPeriodTypeService,
    private changeDetectorRef: ChangeDetectorRef,
    protected tiersCategoryService: TiersCategoryService,
    private userPreferenceService: UserPreferenceService,
    private notificationService: NotificationService
  ) { }

  ngAfterContentChecked(): void {
    this.changeDetectorRef.detectChanges();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (this.tableActions && this.tableActions[0]) {
      if (this.editMode) {
        this.tableActions[0].display = true;
      } else {
        this.tableActions[0].display = false;
      }
    }

    if (changes.tiers != undefined && this.tiers.responsables != undefined && this.tiers.responsables != null) {
      this.principalForm.markAllAsTouched();
      this.setDataTable();

      if (this.selectedResponsableId != null)
        this.selectResponsableById(this.selectedResponsableId);

    }
  }

  ngOnInit() {
    // Trigger it to show mandatory fields
    this.principalForm.markAllAsTouched();

    // Table definition
    this.displayedColumns = [];
    this.displayedColumns.push({ id: "id", fieldName: "id", label: "N° du responsable" } as SortTableColumn<Responsable>);
    this.displayedColumns.push({ id: "lastname", fieldName: "lastname", label: "Nom" } as SortTableColumn<Responsable>);
    this.displayedColumns.push({ id: "firstname", fieldName: "firstname", label: "Prénom" } as SortTableColumn<Responsable>);
    this.displayedColumns.push({ id: "function", fieldName: "function", label: "Fonction" } as SortTableColumn<Responsable>);
    this.displayedColumns.push({ id: "mail", fieldName: "mail.mail", label: "Mail" } as SortTableColumn<Responsable>);
    this.displayedColumns.push({ id: "phones", fieldName: "phones", label: "Téléphones", valueFonction: (element: Responsable, column: SortTableColumn<Responsable>) => { return ((element.phones) ? element.phones.map((e: { phoneNumber: any; }) => e.phoneNumber).join(", ") : "") } } as SortTableColumn<Responsable>);
    this.displayedColumns.push({ id: "salesEmployee", fieldName: "salesEmployee", label: "Commercial", valueFonction: (element: Responsable, column: SortTableColumn<Responsable>) => { return (element && element.salesEmployee) ? element.salesEmployee.firstname + " " + element.salesEmployee.lastname : "" } } as SortTableColumn<Responsable>);
    this.displayedColumns.push({ id: "formalisteEmployee", fieldName: "formalisteEmployee", label: "Formaliste", valueFonction: (element: Responsable, column: SortTableColumn<Responsable>) => { return (element && element.formalisteEmployee) ? element.formalisteEmployee.firstname + " " + element.formalisteEmployee.lastname : "" } } as SortTableColumn<Responsable>);
    this.displayedColumns.push({ id: "insertionEmployee", fieldName: "insertionEmployee", label: "Publiciste", valueFonction: (element: Responsable, column: SortTableColumn<Responsable>) => { return (element && element.insertionEmployee) ? element.insertionEmployee.firstname + " " + element.insertionEmployee.lastname : "" } } as SortTableColumn<Responsable>);

    this.tableActions.push({
      actionIcon: "delete", actionName: 'Supprimer le responsable', display: true, actionClick: (column: SortTableAction<Responsable>, element: Responsable, event: any) => {
        let hash = JSON.stringify(element).toLowerCase();
        for (let i = 0; i < this.tiers.responsables.length; i++) {
          let responsable = this.tiers.responsables[i];
          if (JSON.stringify(responsable).toLowerCase() == hash && responsable.id == undefined) {
            this.tiers.responsables.splice(i, 1);
            this.selectedResponsable = null;
            this.tiersService.setCurrentViewedResponsable(null);
            this.setDataTable();
            return;
          }
        }
      }
    } as SortTableAction<Responsable>);

    this.restoreTab();
  }

  setDataTable() {
    this.tiers.responsables.sort(function (a: Responsable, b: Responsable) {
      return (a.lastname + "" + a.firstname).localeCompare(b.lastname + "" + a.firstname);
    });
  }

  setSelectedResponsableId(selectedResponsableId: number) {
    this.selectedResponsableId = selectedResponsableId;
  }

  selectResponsableById(responsableId: number) {
    if (!this.editMode || this.selectedResponsable == null || this.getFormStatus()) {
      this.tiers.responsables.forEach(responsable => {
        if (responsable.id == responsableId) {
          this.selectedResponsable = responsable;

          this.orderingSearch.customerOrders = [];
          this.quotationSearch.customerOrders = [];
          this.provisionSearch.customerOrders = [];
          this.invoiceSearch.customerOrders = [];
          this.rffSearch = {} as RffSearch;

          setTimeout(() =>
            this.orderingSearch.customerOrders = [responsable as any as Tiers], 0);
          setTimeout(() =>
            this.quotationSearch.customerOrders = [responsable as any as Tiers], 0);
          setTimeout(() =>
            this.provisionSearch.customerOrders = [responsable as any as Tiers], 0);
          setTimeout(() =>
            this.invoiceSearch.customerOrders = [responsable as any as Tiers], 0);
          setTimeout(() => {
            this.rffSearch = {} as RffSearch;
            this.rffSearch.responsable = { entityId: responsable.id } as IndexEntity;
            this.rffSearch.isHideCancelledRff = false;

            let start = new Date();
            let d = new Date(start.getTime());
            d.setFullYear(d.getFullYear() - 1);
            this.rffSearch.startDate = d;

            let end = new Date();
            let d2 = new Date(end.getTime());
            d2.setFullYear(d2.getFullYear() + 1);
            this.rffSearch.endDate = d2;
          }, 0);

          this.tiersService.setCurrentViewedResponsable(responsable);
          if (this.tiers.denomination != null) {
            this.appService.changeHeaderTitle(this.tiers.denomination + " - " + (this.selectedResponsable.firstname != null ? (this.selectedResponsable.firstname + " " + this.selectedResponsable.lastname) : ""));
          } else if (this.tiers.firstname != null) {
            this.appService.changeHeaderTitle(this.tiers.firstname + " " + this.tiers.lastname + " - " + (this.selectedResponsable.firstname != null ? (this.selectedResponsable.firstname + " " + this.selectedResponsable.lastname) : ""));
          }
        }
      })
    } else {
      this.appService.displaySnackBar("Compléter la saisie du responsable courant avant de continuer", true, 15);
    }
  }
  selectResponsable(responsable: Responsable) {
    let responsableId = responsable.id;
    this.selectResponsableById(responsableId);
  }

  addResponsable() {
    if (this.selectedResponsable == null || this.getFormStatus()) {
      this.selectedResponsable = {} as Responsable;
      this.selectedResponsable.language = this.constantService.getLanguageFrench();
      this.selectedResponsable.salesEmployee = this.tiers.salesEmployee;
      this.selectedResponsable.insertionEmployee = this.tiers.insertionEmployee;
      this.selectedResponsable.formalisteEmployee = this.tiers.formalisteEmployee;
      this.tiers.responsables.push(this.selectedResponsable);

      if (this.tiers && this.tiers.documents) {
        this.selectedResponsable.documents = [] as Array<Document>;
        for (let document of this.tiers.documents)
          this.selectedResponsable.documents.push(copyObject(document));
      }

      this.selectedResponsable.isActive = true;
      if (this.isResponsableTypeProspect())
        this.selectedResponsable.tiersType = this.constantService.getTiersTypeProspect();
      this.tiersService.setCurrentViewedResponsable(this.selectedResponsable);
      this.setDataTable();
    } else {
      this.appService.displaySnackBar("Compléter la saisie du responsable courant avant de continuer", true, 15);
    }
  }


  applyFilter(filterValue: any) {
    let filterValueCast = (filterValue as HTMLInputElement);
    filterValue = filterValueCast.value.trim();
    this.searchText = filterValue.toLowerCase();
  }

  isResponsableTypeProspect(): boolean {
    return this.tiers && this.tiers.tiersType && this.constantService.getTiersTypeProspect().id == this.tiers.tiersType.id;
  }

  principalForm = this.formBuilder.group({
  });

  limitTextareaSize(numberOfLine: number) {
    if (this.tiers.mailRecipient != null) {
      var l = this.tiers.mailRecipient.replace(/\r\n/g, "\n").replace(/\r/g, "").split(/\n/g);//split lines
      var outValue = "";
      if (l.length > numberOfLine) {
        outValue = l.slice(0, numberOfLine).join("\n");
        this.tiers.mailRecipient = outValue;
      }
    }
  }

  fillPostalCode(city: City) {
    if (this.selectedResponsable != null) {
      if (this.selectedResponsable.country == null || this.selectedResponsable.country == undefined)
        this.selectedResponsable.country = city.country;

      if (this.selectedResponsable.country.id == this.franceCountry.id && city.postalCode != null && !this.selectedResponsable.postalCode)
        this.selectedResponsable.postalCode = city.postalCode;
    }
  }

  fillCity(postalCode: string) {
    if (this.selectedResponsable != null) {
      this.cityService.getCitiesFilteredByPostalCode(postalCode).subscribe(response => {
        if (response != null && response != undefined && response.length == 1) {
          let city = response[0];
          if (this.selectedResponsable != null) {
            if (this.selectedResponsable.country == null || this.selectedResponsable.country == undefined)
              this.selectedResponsable.country = city.country;

            this.selectedResponsable.city = city;
          }
        }
      })
    }
  }
  instanceOfResponsable = instanceOfResponsable;

  getFormStatus(): boolean {
    let status = true;
    if (this.selectedResponsable != null) {
      let documentSettlementBillingFormStatus = this.documentSettlementBillingComponent?.getFormStatus();
      this.principalForm.markAllAsTouched();

      if (this.selectedResponsable != null && (this.selectedResponsable.isBouclette == null || this.selectedResponsable.isBouclette == undefined))
        this.selectedResponsable.isBouclette = false;

      status = status && this.principalForm.valid && (documentSettlementBillingFormStatus! || this.isResponsableTypeProspect());
    }
    return status;
  }

  //Tabs management
  index: number = 0;
  onTabChange(event: MatTabChangeEvent) {
    this.userPreferenceService.setUserTabsSelectionIndex('responsable', event.index);
  }

  restoreTab() {
    this.index = this.userPreferenceService.getUserTabsSelectionIndex('responsable');
  }

  renewPassword() {
    if (!this.selectedResponsable || !this.selectedResponsable.mail) {
      this.appService.displaySnackBar("Aucune adresse mail disponible pour ce responsable !", true, 20);
      return;
    }

    this.employeeService.renewResponsablePassword(this.selectedResponsable!).subscribe(response => { });
  }

  addNewNotification() {
    if (this.selectedResponsable)
      this.appService.addPersonnalNotification(() => this.responsableNotification = undefined, this.responsableNotification, undefined, undefined, undefined, undefined, undefined, undefined, this.selectedResponsable, undefined);
  }

  responsableNotification: Notification[] | undefined;

  getNotificationForResponsable() {
    if (this.responsableNotification == undefined) {
      this.responsableNotification = [];
      if (this.selectedResponsable)
        this.notificationService.getNotificationsForResponsable(this.selectedResponsable.id).subscribe(response => this.responsableNotification = response);
    }
    return this.responsableNotification;
  }

  canDisplayNotifications() {
    return this.habilitationService.canDisplayNotifications();
  }

  canChooseProductionEmployeeOnITiers() {
    return this.habilitationService.canChooseProductionEmployeeOnITiers();
  }
}
