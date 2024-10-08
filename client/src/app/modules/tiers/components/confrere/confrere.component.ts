import { Component, Input, OnInit, ViewChild, ViewEncapsulation } from '@angular/core';
import { AbstractControl, FormBuilder, UntypedFormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';
import { MatAccordion } from '@angular/material/expansion';
import { MatTabChangeEvent } from '@angular/material/tabs';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { validateVat } from 'src/app/libs/CustomFormsValidatorsHelper';
import { InvoiceSearch } from 'src/app/modules/invoicing/model/InvoiceSearch';
import { City } from 'src/app/modules/miscellaneous/model/City';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { CityService } from 'src/app/modules/miscellaneous/services/city.service';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { PaymentTypeService } from 'src/app/modules/miscellaneous/services/payment.type.service';
import { Confrere } from 'src/app/modules/quotation/model/Confrere';
import { OrderingSearch } from 'src/app/modules/quotation/model/OrderingSearch';
import { QuotationSearch } from 'src/app/modules/quotation/model/QuotationSearch';
import { ConfrereService } from 'src/app/modules/quotation/services/confrere.service';
import { IndexEntityService } from 'src/app/routing/search/index.entity.service';
import { IndexEntity } from 'src/app/routing/search/IndexEntity';
import { CONFRERE_ENTITY_TYPE } from 'src/app/routing/search/search.component';
import { AppService } from '../../../../services/app.service';
import { UserPreferenceService } from '../../../../services/user.preference.service';
import { Responsable } from '../../model/Responsable';
import { ResponsableService } from '../../services/responsable.service';

@Component({
  selector: 'confrere',
  templateUrl: './confrere.component.html',
  styleUrls: ['./confrere.component.css'],
  encapsulation: ViewEncapsulation.None,
})
export class ConfrereComponent implements OnInit {
  constructor(private confrereService: ConfrereService,
    private cityService: CityService,
    private formBuilder: FormBuilder,
    private activatedRoute: ActivatedRoute,
    private constantService: ConstantService,
    private appService: AppService,
    protected paymentTypeService: PaymentTypeService,
    private userPreferenceService: UserPreferenceService,
    private responsableService: ResponsableService,
    private indexEntityService: IndexEntityService,
  ) {
  }

  confreres: Confrere[] = [];
  searchText: string = "";
  selectedConfrere: Confrere | undefined;
  selectedConfrereId: number | undefined;
  displayedColumns: SortTableColumn<Confrere>[] = [];
  editMode: boolean = false;
  CONFRERE_ENTITY_TYPE = CONFRERE_ENTITY_TYPE;
  searchedResponsable: IndexEntity | undefined;

  orderingSearch: OrderingSearch = {} as OrderingSearch;
  quotationSearch: QuotationSearch = {} as QuotationSearch;
  invoiceSearch: InvoiceSearch = {} as InvoiceSearch;
  responsableAccountSearch: Responsable | undefined;

  saveObservableSubscription: Subscription = new Subscription;

  @Input() idConfrere: number | undefined;

  journalTypePaper = this.constantService.getJournalTypePaper();

  ngOnInit(): void {
    if (!this.idConfrere)
      this.appService.changeHeaderTitle("Confrères");
    let idConfrere = this.activatedRoute.snapshot.params.id;
    if (this.idConfrere)
      idConfrere = this.idConfrere;

    if (idConfrere && idConfrere != "null") {
      this.selectedConfrereId = parseInt(idConfrere);
      this.confrereService.getConfrereById(this.selectedConfrereId).subscribe(response => {
        this.confreres = [];
        this.confreres.push(response);
        this.selectConfrere(this.confreres[0]);
      })
    }

    this.displayedColumns = [];
    this.displayedColumns.push({ id: "id", fieldName: "id", label: "Identifiant technique" } as SortTableColumn<Confrere>);
    this.displayedColumns.push({ id: "code", fieldName: "code", label: "Codification fonctionnelle" } as SortTableColumn<Confrere>);
    this.displayedColumns.push({ id: "label", fieldName: "label", label: "Libellé" } as SortTableColumn<Confrere>);
    this.displayedColumns.push({ id: "type", fieldName: "journalType.label", label: "Type" } as SortTableColumn<Confrere>);
    this.displayedColumns.push({ id: "departments", fieldName: "departments", label: "Habilitations", valueFonction: (element: Confrere, column: SortTableColumn<Confrere>) => { return ((element.departments) ? element.departments.map((e: { code: any; }) => e.code).join(", ") : "") } } as SortTableColumn<Confrere>);
    this.displayedColumns.push({ id: "weekDays", fieldName: "weekDays", label: "Jours de parution", valueFonction: (element: Confrere, column: SortTableColumn<Confrere>) => { return ((element.departments) ? element.weekDays.map((e: { label: any; }) => e.label).join(", ") : "") } } as SortTableColumn<Confrere>);
    this.displayedColumns.push({ id: "mails", fieldName: "mails", label: "Mails", valueFonction: (element: Confrere, column: SortTableColumn<Confrere>) => { return ((element.mails) ? element.mails.map((e: { mail: any; }) => e.mail).join(", ") : "") } } as SortTableColumn<Confrere>);
    this.displayedColumns.push({ id: "phones", fieldName: "phones", label: "Téléphones", valueFonction: (element: Confrere, column: SortTableColumn<Confrere>) => { return ((element.phones) ? element.phones.map((e: { phoneNumber: any; }) => e.phoneNumber).join(", ") : "") } } as SortTableColumn<Confrere>);

    this.saveObservableSubscription = this.appService.saveObservable.subscribe(response => {
      if (response)
        if (this.editMode)
          this.saveConfrere()
        else if (this.selectedConfrere && this.selectedConfrere.id)
          this.editConfrere()
    });
  }

  ngOnDestroy() {
    this.saveObservableSubscription.unsubscribe();
  }

  entityForm2 = this.formBuilder.group({
    boardGrade: [''],
    publicationCertificateDocumentGrade: [''],
    billingGrade: [''],
    paperGrade: [''],
  });

  grades: string[] = ["+", "++", "+++", "++++", "+++++"];

  @ViewChild(MatAccordion) accordion: MatAccordion | undefined;

  selectConfrere(element: Confrere) {
    this.selectedConfrere = element;
    this.selectedConfrereId = element.id;
    this.restoreTab();
    if (!this.idConfrere)
      this.appService.changeHeaderTitle(element.label);

    this.orderingSearch.customerOrders = [];
    this.quotationSearch.customerOrders = [];
    this.invoiceSearch.customerOrders = [];
    this.responsableAccountSearch = undefined;

    if (this.selectedConfrere) {
      setTimeout(() =>
        this.orderingSearch.customerOrders = [this.selectedConfrere! as any], 0);
      setTimeout(() =>
        this.quotationSearch.customerOrders = [this.selectedConfrere! as any], 0);
      setTimeout(() =>
        this.invoiceSearch.customerOrders = [this.selectedConfrere! as any], 0);
      if (this.selectedConfrere.responsable)
        setTimeout(() =>
          this.responsableAccountSearch = this.selectedConfrere?.responsable, 0);

      if (this.selectedConfrere.responsable) {
        this.indexEntityService.getResponsableByKeyword(this.selectedConfrere.responsable.id + "", false).subscribe(response => this.searchedResponsable = response[0]);
      }
    }
  }

  limitTextareaSize(numberOfLine: number) {
    if (this.selectedConfrere?.mailRecipient != null) {
      var l = this.selectedConfrere?.mailRecipient.replace(/\r\n/g, "\n").replace(/\r/g, "").split(/\n/g);//split lines
      var outValue = "";
      if (l.length > numberOfLine) {
        outValue = l.slice(0, numberOfLine).join("\n");
        this.selectedConfrere.mailRecipient = outValue;
      }
    }
  }

  fillPostalCode(city: City) {
    if (this.selectedConfrere! != null) {
      if (this.selectedConfrere!.country == null || this.selectedConfrere!.country == undefined)
        this.selectedConfrere!.country = city.country;

      if (this.selectedConfrere!.country.id == this.constantService.getCountryFrance().id && city.postalCode != null && !this.selectedConfrere!.postalCode)
        this.selectedConfrere!.postalCode = city.postalCode;
    }
  }

  fillCity(postalCode: string) {
    if (this.selectedConfrere! != null) {
      this.cityService.getCitiesFilteredByPostalCode(postalCode).subscribe(response => {
        if (response != null && response != undefined && response.length == 1) {
          let city = response[0];
          if (this.selectedConfrere! != null) {
            if (this.selectedConfrere!.country == null || this.selectedConfrere!.country == undefined)
              this.selectedConfrere!.country = city.country;

            this.selectedConfrere!.city = city;
          }
        }
      })
    }
  }

  fillResponsable(responsable: IndexEntity) {
    this.responsableService.getResponsable(responsable.entityId).subscribe(response => {
      if (this.selectedConfrere)
        this.selectedConfrere.responsable = response;
    });
  }

  applyFilter(filterValue: any) {
    let filterValueCast = (filterValue as HTMLInputElement);
    filterValue = filterValueCast.value.trim();
    this.searchText = filterValue.toLowerCase();
    if (this.searchText.length > 1) {
      this.confrereService.getConfrereFilteredByDepartmentAndName(undefined, this.searchText).subscribe(response => this.confreres = response);
    }
  }

  saveConfrere() {
    if (this.getFormStatus() && this.selectedConfrere) {
      this.editMode = false;

      this.confrereService.addOrUpdateConfrere(this.selectedConfrere).subscribe(response => {
        this.selectedConfrere = response;
      });
    } else {
      this.appService.displaySnackBar("Erreur, certains champs ne sont pas correctement renseignés !", true, 15);
    }
  }

  getFormStatus() {
    if (!this.entityForm2 || !this.entityForm2.valid) {
      return false;
    }
    return true;
  }

  addConfrere() {
    this.selectedConfrere = {} as Confrere;
    this.editMode = true;
  }

  editConfrere() {
    this.editMode = true;
  }

  checkVAT(fieldName: string): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const root = control.root as UntypedFormGroup;
      const fieldValue = root.get(fieldName)?.value;
      if ((fieldValue == undefined || fieldValue == null || fieldValue.length == 0 || !validateVat(fieldValue)))
        return {
          notFilled: true
        };
      return null;
    };
  }

  //Tabs management
  index: number = 0;
  onTabChange(event: MatTabChangeEvent) {
    this.userPreferenceService.setUserTabsSelectionIndex('confrere', event.index);
  }

  restoreTab() {
    this.index = this.userPreferenceService.getUserTabsSelectionIndex('confrere');
  }

}
