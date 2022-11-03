import { Component, Input, OnInit, SimpleChanges, ViewChild } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { CustomErrorStateMatcher } from 'src/app/app.component';
import { copyObject } from 'src/app/libs/GenericHelper';
import { instanceOfResponsable } from 'src/app/libs/TypeHelper';
import { City } from 'src/app/modules/miscellaneous/model/City';
import { Country } from 'src/app/modules/miscellaneous/model/Country';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { CityService } from 'src/app/modules/miscellaneous/services/city.service';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { RESPONSABLE_ENTITY_TYPE } from 'src/app/routing/search/search.component';
import { AppService } from 'src/app/services/app.service';
import { Civility } from '../../../miscellaneous/model/Civility';
import { Document } from "../../../miscellaneous/model/Document";
import { Language } from '../../../miscellaneous/model/Language';
import { JssSubscription } from '../../model/JssSubscription';
import { Responsable } from '../../model/Responsable';
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

export class ResponsableMainComponent implements OnInit {
  matcher: CustomErrorStateMatcher = new CustomErrorStateMatcher();
  @Input() tiers: Tiers = {} as Tiers;
  @Input() editMode: boolean = false;
  @ViewChild('tabs', { static: false }) tabs: any;

  RESPONSABLE_ENTITY_TYPE = RESPONSABLE_ENTITY_TYPE;

  subscriptionPeriodType12M: SubscriptionPeriodType = this.constantService.getSubscriptionPeriodType12M();

  franceCountry: Country = this.constantService.getCountryFrance();

  filterValue: string = "";

  selectedResponsable: Responsable | null = null;

  selectedResponsableId: number | null = null;

  civilities: Civility[] = [] as Array<Civility>;
  languages: Language[] = [] as Array<Language>;

  isSubscriptionPaper: boolean = false;
  isSubscriptionWeb: boolean = false;

  displayedColumns: SortTableColumn[] = [];
  tableActions: SortTableAction[] = [] as Array<SortTableAction>;
  searchText: string | undefined;

  @ViewChild(SettlementBillingComponent) documentSettlementBillingComponent: SettlementBillingComponent | undefined;

  constructor(private formBuilder: UntypedFormBuilder,
    private cityService: CityService,
    private appService: AppService,
    protected tiersService: TiersService,
    protected tiersTypeService: TiersTypeService,
    private constantService: ConstantService,
    protected subscriptionPeriodTypeService: SubscriptionPeriodTypeService,
    protected tiersCategoryService: TiersCategoryService) { }

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
      this.initDefaultValues();
      this.toggleTabs();

      if (this.selectedResponsableId != null)
        this.selectResponsableById(this.selectedResponsableId);
    }
  }

  ngOnInit() {
    // Trigger it to show mandatory fields
    this.principalForm.markAllAsTouched();

    // Table definition
    this.displayedColumns = [];
    this.displayedColumns.push({ id: "id", fieldName: "id", label: "N° du responsable" } as SortTableColumn);
    this.displayedColumns.push({ id: "name", fieldName: "name", label: "Nom", valueFonction: (element: any) => { return (element) ? element.firstname + " " + element.lastname : "" } } as SortTableColumn);
    this.displayedColumns.push({ id: "address", fieldName: "address", label: "Adresse" } as SortTableColumn);
    this.displayedColumns.push({ id: "city", fieldName: "city.label", label: "Ville" } as SortTableColumn);
    this.displayedColumns.push({ id: "salesEmployee", fieldName: "salesEmployee", label: "Commercial", valueFonction: (element: any) => { return (element && element.salesEmployee) ? element.salesEmployee.firstname + " " + element.salesEmployee.lastname : "" } } as SortTableColumn);
    this.displayedColumns.push({ id: "formalisteEmployee", fieldName: "formalisteEmployee", label: "Formaliste", valueFonction: (element: any) => { return (element && element.formalisteEmployee) ? element.formalisteEmployee.firstname + " " + element.formalisteEmployee.lastname : "" } } as SortTableColumn);
    this.displayedColumns.push({ id: "insertionEmployee", fieldName: "insertionEmployee", label: "Publiciste", valueFonction: (element: any) => { return (element && element.insertionEmployee) ? element.insertionEmployee.firstname + " " + element.insertionEmployee.lastname : "" } } as SortTableColumn);

    this.tableActions.push({ actionIcon: "delete", actionName: 'Supprimer le responsable', display: true, actionClick: (action: SortTableAction, element: any) => { return this.deleteResponsable(element) } } as SortTableAction);
  }

  toggleTabs() {
    if (this.tabs != undefined)
      this.tabs.realignInkBar();
  }

  setDataTable() {
    this.tiers.responsables.sort(function (a: Responsable, b: Responsable) {
      return (a.firstname + "" + a.lastname).localeCompare(b.firstname + "" + a.lastname);
    });
    this.toggleTabs();
  }

  setSelectedResponsableId(selectedResponsableId: number) {
    this.selectedResponsableId = selectedResponsableId;
  }

  selectResponsableById(responsableId: number) {
    if (this.selectedResponsable == null || this.getFormStatus()) {
      this.tiers.responsables.forEach(responsable => {
        if (responsable.id == responsableId) {
          this.selectedResponsable = responsable;
          this.tiersService.setCurrentViewedResponsable(responsable);
          this.toggleTabs();
          this.initDefaultValues();
          if (this.tiers.denomination != null) {
            this.appService.changeHeaderTitle(this.tiers.denomination + " - " + (this.selectedResponsable.firstname != null ? (this.selectedResponsable.firstname + " " + this.selectedResponsable.lastname) : ""));
          } else if (this.tiers.firstname != null) {
            this.appService.changeHeaderTitle(this.tiers.firstname + " " + this.tiers.lastname + " - " + (this.selectedResponsable.firstname != null ? (this.selectedResponsable.firstname + " " + this.selectedResponsable.lastname) : ""));
          }
        }
      })
    } else {
      this.appService.displaySnackBar("Compléter la saisie du responsable courant avant de continuer", true, 60);
    }
  }
  selectResponsable(responsable: Responsable) {
    let responsableId = responsable.id;
    this.selectResponsableById(responsableId);
  }

  deleteResponsable(responsableRow: any) {
    let hash = JSON.stringify(responsableRow).toLowerCase();
    for (let i = 0; i < this.tiers.responsables.length; i++) {
      let responsable = this.tiers.responsables[i];
      if (JSON.stringify(responsable).toLowerCase() == hash) {
        this.tiers.responsables.splice(i, 1);
        this.selectedResponsable = null;
        this.tiersService.setCurrentViewedResponsable(null);
        this.setDataTable();
        return;
      }
    }
  }

  addResponsable() {
    if (this.selectedResponsable == null || this.getFormStatus()) {
      this.selectedResponsable = {} as Responsable;
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
      this.toggleTabs();
      this.initDefaultValues();
    } else {
      this.appService.displaySnackBar("Compléter la saisie du responsable courant avant de continuer", true, 60);
    }
  }

  initDefaultValues() {
    if (this.selectedResponsable != null && (this.selectedResponsable?.jssSubscription == null || this.selectedResponsable.jssSubscription == undefined)) {
      this.selectedResponsable.jssSubscription = { isPaperSubscription: false, isWebSubscription: false } as JssSubscription;
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
    jssSubscription1: [''],
    jssSubscription2: [''],

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

      if (this.selectedResponsable.country.id == this.franceCountry.id && city.postalCode != null)
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
    console.log(this.principalForm);
    console.log(this.documentSettlementBillingComponent?.getFormStatus());
    if (this.selectedResponsable != null) {
      let documentSettlementBillingFormStatus = this.documentSettlementBillingComponent?.getFormStatus();
      this.principalForm.markAllAsTouched();

      if (this.selectedResponsable != null && (this.selectedResponsable.isBouclette == null || this.selectedResponsable.isBouclette == undefined))
        this.selectedResponsable.isBouclette = false;

      if (this.selectedResponsable?.jssSubscription != undefined && this.selectedResponsable.jssSubscription.isPaperSubscription)
        this.selectedResponsable.jssSubscription.isWebSubscription = true;

      status = status && this.principalForm.valid && (documentSettlementBillingFormStatus! || this.isResponsableTypeProspect());
    }
    return status;
  }


}
