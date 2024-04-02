import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatTabChangeEvent } from '@angular/material/tabs';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { InvoiceSearch } from 'src/app/modules/invoicing/model/InvoiceSearch';
import { City } from 'src/app/modules/miscellaneous/model/City';
import { Provider } from 'src/app/modules/miscellaneous/model/Provider';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { PaymentTypeService } from 'src/app/modules/miscellaneous/services/payment.type.service';
import { ProviderService } from 'src/app/modules/miscellaneous/services/provider.service';
import { ITiers } from 'src/app/modules/tiers/model/ITiers';
import { PROVIDER_ENTITY_TYPE } from 'src/app/routing/search/search.component';
import { AppService } from 'src/app/services/app.service';
import { UserPreferenceService } from '../../../../services/user.preference.service';
import { CityService } from '../../../miscellaneous/services/city.service';

@Component({
  selector: 'provider',
  templateUrl: './provider.component.html',
  styleUrls: ['./provider.component.css']
})
export class ProviderComponent implements OnInit {
  constructor(private providerService: ProviderService,
    private formBuilder: FormBuilder,
    private constantService: ConstantService,
    private appService: AppService,
    protected paymentTypeService: PaymentTypeService,
    protected activatedRoute: ActivatedRoute,
    private userPreferenceService: UserPreferenceService,
    private cityService: CityService
  ) {
  }

  providers: Provider[] = [];
  filteredProviders: Provider[] = [];
  searchText: string = "";
  selectedProvider: Provider | undefined;
  selectedProviderId: number | undefined;
  displayedColumns: SortTableColumn<Provider>[] = [];
  editMode: boolean = false;
  invoiceSearch: InvoiceSearch = {} as InvoiceSearch;

  saveObservableSubscription: Subscription = new Subscription;
  PROVIDER_ENTITY_TYPE = PROVIDER_ENTITY_TYPE;

  paymentTypePrelevement = this.constantService.getPaymentTypePrelevement();
  paymentTypeVirement = this.constantService.getPaymentTypeVirement();
  paymentTypeCB = this.constantService.getPaymentTypeCB();

  @Input() idProvider: number | undefined;

  ngOnInit(): void {
    if (!this.idProvider)
      this.appService.changeHeaderTitle("Fournisseurs");

    this.selectedProviderId = this.activatedRoute.snapshot.params.id;
    if (!this.selectedProviderId)
      this.selectedProviderId = this.userPreferenceService.getUserTabsSelectionIndex('provider-selected');

    if (this.idProvider)
      this.selectedProviderId = this.idProvider;

    this.displayedColumns = [];
    this.displayedColumns.push({ id: "id", fieldName: "id", label: "Identifiant technique" } as SortTableColumn<Provider>);
    this.displayedColumns.push({ id: "code", fieldName: "code", label: "Codification fonctionnelle" } as SortTableColumn<Provider>);
    this.displayedColumns.push({ id: "label", fieldName: "label", label: "Libellé" } as SortTableColumn<Provider>);

    this.saveObservableSubscription = this.appService.saveObservable.subscribe(response => {
      if (response)
        if (this.editMode)
          this.saveProvider()
        else if (this.selectedProvider && this.selectedProvider.id)
          this.editProvider()
    });

    this.providerService.getProviders().subscribe(providers => {
      this.providers = providers
      if (this.selectedProviderId && this.providers)
        for (let provider of this.providers)
          if (provider.id == this.selectedProviderId)
            this.selectProvider(provider);
    });

  }

  ngOnDestroy() {
    this.saveObservableSubscription.unsubscribe();
  }

  entityForm2 = this.formBuilder.group({
  });

  selectProvider(element: Provider) {
    this.selectedProvider = element;
    this.selectedProviderId = element.id;
    this.userPreferenceService.setUserTabsSelectionIndex('provider-selected', this.selectedProviderId!);
    this.restoreTab();

    if (!this.idProvider)
      this.appService.changeHeaderTitle(element.label);
    this.invoiceSearch.customerOrders = [];
    setTimeout(() =>
      this.invoiceSearch.customerOrders = [({ id: this.selectedProvider!.id } as any) as ITiers], 0);
  }

  applyFilter(filterValue: any) {
    let filterValueCast = (filterValue as HTMLInputElement);
    filterValue = filterValueCast.value;
    this.searchText = filterValue ? filterValue.trim().toLowerCase() : "";
    this.filteredProviders = [];
    this.selectedProvider = undefined;

    if (this.providers)
      for (let provider of this.providers)
        if (provider.label.toLocaleLowerCase().indexOf(this.searchText.normalize("NFD").replace(/[\u0300-\u036f]/g, "")) >= 0)
          this.filteredProviders.push(provider);
  }

  saveProvider() {
    if (this.getFormStatus() && this.selectedProvider) {
      this.editMode = false;
      this.providerService.addOrUpdateProvider(this.selectedProvider).subscribe(response => {
        this.selectedProvider = response;
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

  addProvider() {
    this.selectedProvider = {} as Provider;
    this.editMode = true;
  }

  editProvider() {
    this.editMode = true;
  }

  //Tabs management
  index: number = 0;
  onTabChange(event: MatTabChangeEvent) {
    this.userPreferenceService.setUserTabsSelectionIndex('provider', event.index);
  }

  restoreTab() {
    this.index = this.userPreferenceService.getUserTabsSelectionIndex('provider');
  }

  fillPostalCode(city: City) {
    if (this.selectedProvider) {
      if (this.selectedProvider.country == null || this.selectedProvider.country == undefined)
        this.selectedProvider.country = city.country;

      if (this.selectedProvider.country.id == this.constantService.getCountryFrance().id && city.postalCode != null && !this.selectedProvider.postalCode)
        this.selectedProvider.postalCode = city.postalCode;
    }
  }

  fillCity(postalCode: string) {
    if (this.selectedProvider) {
      this.cityService.getCitiesFilteredByPostalCode(postalCode).subscribe(response => {
        if (response != null && response != undefined && response.length == 1) {
          let city = response[0];
          if (this.selectedProvider!.country == null || this.selectedProvider!.country == undefined)
            this.selectedProvider!.country = city.country;

          this.selectedProvider!.city = city;
        }
      })
    }
  }
}
