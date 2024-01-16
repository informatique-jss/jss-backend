import { Component, Input, OnInit, ViewEncapsulation } from '@angular/core';
import { AbstractControl, FormBuilder, UntypedFormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';
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
import { ITiers } from 'src/app/modules/tiers/model/ITiers';
import { COMPETENT_AUTHORITY_ENTITY_TYPE } from 'src/app/routing/search/search.component';
import { AppService } from '../../../../services/app.service';
import { UserPreferenceService } from '../../../../services/user.preference.service';
import { CompetentAuthority } from '../../../miscellaneous/model/CompetentAuthority';
import { CompetentAuthorityType } from '../../../miscellaneous/model/CompetentAuthorityType';
import { CompetentAuthorityService } from '../../../miscellaneous/services/competent.authority.service';

@Component({
  selector: 'competent-authority',
  templateUrl: './competent-authority.component.html',
  styleUrls: ['./competent-authority.component.css'],
  encapsulation: ViewEncapsulation.None,
})
export class CompetentAuthorityComponent implements OnInit {
  constructor(private competentAuthorityService: CompetentAuthorityService,
    private cityService: CityService,
    private formBuilder: FormBuilder,
    private constantService: ConstantService,
    protected activatedRoute: ActivatedRoute,
    private appService: AppService,
    protected paymentTypeService: PaymentTypeService,
    private userPreferenceService: UserPreferenceService
  ) {
  }

  competentAuthorities: CompetentAuthority[] = [];
  filteredCompetentAuthorities: CompetentAuthority[] = [];
  searchText: string = "";
  selectedcompetentAuthority: CompetentAuthority | undefined;
  selectedCompetentAuthorityType: CompetentAuthorityType | undefined;
  displayedColumns: SortTableColumn<CompetentAuthority>[] = [];
  editMode: boolean = false;
  selectedCompetentAuthorityId: number | undefined;
  invoiceSearch: InvoiceSearch = {} as InvoiceSearch;

  saveObservableSubscription: Subscription = new Subscription;
  COMPETENT_AUTHORITY_ENTITY_TYPE = COMPETENT_AUTHORITY_ENTITY_TYPE;

  @Input() idCompetentAuthority: number | undefined;

  ngOnInit(): void {
    if (!this.idCompetentAuthority)
      this.appService.changeHeaderTitle("Autorités compétentes");

    this.selectedCompetentAuthorityId = this.activatedRoute.snapshot.params.id;
    if (!this.selectedCompetentAuthorityId)
      this.selectedCompetentAuthorityId = this.userPreferenceService.getUserTabsSelectionIndex('competent-authority-selected');

    if (this.idCompetentAuthority)
      this.selectedCompetentAuthorityId = this.idCompetentAuthority;

    if (this.selectedCompetentAuthorityId) {
      this.competentAuthorityService.getCompetentAuthorityById(this.selectedCompetentAuthorityId).subscribe(response => {
        this.selectCompetentAuthority(response);
      });
    }

    this.displayedColumns = [];
    this.displayedColumns.push({ id: "id", fieldName: "id", label: "Identifiant technique" } as SortTableColumn<CompetentAuthority>);
    this.displayedColumns.push({ id: "code", fieldName: "code", label: "Codification fonctionnelle" } as SortTableColumn<CompetentAuthority>);
    this.displayedColumns.push({ id: "label", fieldName: "label", label: "Libellé" } as SortTableColumn<CompetentAuthority>);
    this.displayedColumns.push({ id: "competentAuthorityType", fieldName: "competentAuthorityType.label", label: "Type" } as SortTableColumn<CompetentAuthority>);

    this.saveObservableSubscription = this.appService.saveObservable.subscribe(response => {
      if (response)
        if (this.editMode)
          this.saveCompetentAuthority()
        else if (this.selectedcompetentAuthority && this.selectedcompetentAuthority.id)
          this.editCompetentAuthority()
    });
  }

  getCitiesForCurrentCompetentAuthority() {
    if (this.selectedcompetentAuthority)
      this.cityService.getCitiesForCompetentAuthority(this.selectedcompetentAuthority).subscribe(response => this.selectedcompetentAuthority!.cities = response);
  }

  ngOnDestroy() {
    this.saveObservableSubscription.unsubscribe();
  }

  entityForm2 = this.formBuilder.group({
  });

  selectCompetentAuthority(element: CompetentAuthority) {
    this.selectedcompetentAuthority = element;
    this.selectedCompetentAuthorityId = element.id;
    this.userPreferenceService.setUserTabsSelectionIndex('competent-authority-selected', this.selectedCompetentAuthorityId!);
    this.restoreTab();
    this.getCitiesForCurrentCompetentAuthority();
    if (!this.idCompetentAuthority)
      this.appService.changeHeaderTitle(element.label);

    setTimeout(() => {
      this.invoiceSearch.customerOrders = [{ id: this.selectedcompetentAuthority!.id } as ITiers];
      this.invoiceSearch.invoiceStatus = [this.constantService.getInvoiceStatusReceived()];
    }, 0);

  }

  limitTextareaSize(numberOfLine: number) {
    if (this.selectedcompetentAuthority?.mailRecipient != null) {
      var l = this.selectedcompetentAuthority?.mailRecipient.replace(/\r\n/g, "\n").replace(/\r/g, "").split(/\n/g);//split lines
      var outValue = "";
      if (l.length > numberOfLine) {
        outValue = l.slice(0, numberOfLine).join("\n");
        this.selectedcompetentAuthority.mailRecipient = outValue;
      }
    }
  }

  fillPostalCode(city: City) {
    if (this.selectedcompetentAuthority! != null) {
      if (this.selectedcompetentAuthority!.country == null || this.selectedcompetentAuthority!.country == undefined)
        this.selectedcompetentAuthority!.country = city.country;

      if (this.selectedcompetentAuthority!.country.id == this.constantService.getCountryFrance().id && city.postalCode != null && !this.selectedcompetentAuthority!.postalCode)
        this.selectedcompetentAuthority!.postalCode = city.postalCode;
    }
  }

  fillCity(postalCode: string) {
    if (this.selectedcompetentAuthority! != null) {
      this.cityService.getCitiesFilteredByPostalCode(postalCode).subscribe(response => {
        if (response != null && response != undefined && response.length == 1) {
          let city = response[0];
          if (this.selectedcompetentAuthority! != null) {
            if (this.selectedcompetentAuthority!.country == null || this.selectedcompetentAuthority!.country == undefined)
              this.selectedcompetentAuthority!.country = city.country;

            this.selectedcompetentAuthority!.city = city;
          }
        }
      })
    }
  }

  applyFilter(filterValue: any) {
    let filterValueCast = (filterValue as HTMLInputElement);
    filterValue = filterValueCast.value;
    this.searchText = filterValue ? filterValue.trim().toLowerCase() : "";
    this.filteredCompetentAuthorities = [];
    this.selectedcompetentAuthority = undefined;

    if (this.competentAuthorities)
      for (let competentAuthority of this.competentAuthorities)
        if (competentAuthority.label.toLocaleLowerCase().indexOf(this.searchText.normalize("NFD").replace(/[\u0300-\u036f]/g, "")) >= 0)
          this.filteredCompetentAuthorities.push(competentAuthority);
  }

  isLoading = false;
  fetchCompetentAuthorities() {
    if (this.selectedCompetentAuthorityType && !this.isLoading) {
      this.isLoading = true;
      this.competentAuthorityService.getCompetentAuthoritiesByType(this.selectedCompetentAuthorityType).subscribe(response => {
        this.competentAuthorities = response;
        this.applyFilter(this.searchText);
        this.isLoading = false;
      })
    }
  }

  saveCompetentAuthority() {
    if (this.getFormStatus() && this.selectedcompetentAuthority) {
      this.editMode = false;
      this.competentAuthorityService.addOrUpdateCompetentAuthority(this.selectedcompetentAuthority).subscribe(response => {
        this.selectedcompetentAuthority = response;
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

  addCompetentAuthority() {
    this.selectedcompetentAuthority = {} as CompetentAuthority;
    this.editMode = true;
  }

  editCompetentAuthority() {
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
    this.userPreferenceService.setUserTabsSelectionIndex('competent-authority', event.index);
  }

  restoreTab() {
    this.index = this.userPreferenceService.getUserTabsSelectionIndex('competent-authority');
  }

}
