import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { InvoiceSearch } from 'src/app/modules/invoicing/model/InvoiceSearch';
import { Provider } from 'src/app/modules/miscellaneous/model/Provider';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { PaymentTypeService } from 'src/app/modules/miscellaneous/services/payment.type.service';
import { ProviderService } from 'src/app/modules/miscellaneous/services/provider.service';
import { ITiers } from 'src/app/modules/tiers/model/ITiers';
import { PROVIDER_ENTITY_TYPE } from 'src/app/routing/search/search.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'app-provider',
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
  ) {
  }

  providers: Provider[] = [];
  filteredProviders: Provider[] = [];
  searchText: string = "";
  selectedProvider: Provider | undefined;
  selectedProviderId: number | undefined;
  displayedColumns: SortTableColumn[] = [];
  editMode: boolean = false;
  invoiceSearch: InvoiceSearch = {} as InvoiceSearch;

  saveObservableSubscription: Subscription = new Subscription;
  PROVIDER_ENTITY_TYPE = PROVIDER_ENTITY_TYPE;

  paymentTypePrelevement = this.constantService.getPaymentTypePrelevement();
  paymentTypeVirement = this.constantService.getPaymentTypeVirement();
  paymentTypeCB = this.constantService.getPaymentTypeCB();


  ngOnInit(): void {
    this.appService.changeHeaderTitle("Fournisseurs");

    this.selectedProviderId = this.activatedRoute.snapshot.params.id;

    this.displayedColumns = [];
    this.displayedColumns.push({ id: "id", fieldName: "id", label: "Identifiant technique" } as SortTableColumn);
    this.displayedColumns.push({ id: "code", fieldName: "code", label: "Codification fonctionnelle" } as SortTableColumn);
    this.displayedColumns.push({ id: "label", fieldName: "label", label: "Libellé" } as SortTableColumn);

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
}
