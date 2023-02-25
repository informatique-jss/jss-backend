import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Subscription } from 'rxjs';
import { City } from 'src/app/modules/miscellaneous/model/City';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { CityService } from 'src/app/modules/miscellaneous/services/city.service';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { PaymentTypeService } from 'src/app/modules/miscellaneous/services/payment.type.service';
import { AppService } from '../../../../services/app.service';
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
    private appService: AppService,
    protected paymentTypeService: PaymentTypeService,) {
  }

  competentAuthorities: CompetentAuthority[] = [];
  filteredCompetentAuthorities: CompetentAuthority[] = [];
  searchText: string = "";
  selectedcompetentAuthority: CompetentAuthority | undefined;
  selectedCompetentAuthorityType: CompetentAuthorityType | undefined;
  selectedcompetentAuthorityId: number | undefined;
  displayedColumns: SortTableColumn[] = [];
  editMode: boolean = false;

  saveObservableSubscription: Subscription = new Subscription;


  ngOnInit(): void {
    this.appService.changeHeaderTitle("Autorités compétentes");

    this.displayedColumns = [];
    this.displayedColumns.push({ id: "id", fieldName: "id", label: "Identifiant technique" } as SortTableColumn);
    this.displayedColumns.push({ id: "code", fieldName: "code", label: "Codification fonctionnelle" } as SortTableColumn);
    this.displayedColumns.push({ id: "label", fieldName: "label", label: "Libellé" } as SortTableColumn);
    this.displayedColumns.push({ id: "competentAuthorityType", fieldName: "competentAuthorityType.label", label: "Type" } as SortTableColumn);

    this.saveObservableSubscription = this.appService.saveObservable.subscribe(response => {
      if (response)
        if (this.editMode)
          this.saveCompetentAuthority()
        else if (this.selectedcompetentAuthority && this.selectedcompetentAuthority.id)
          this.editCompetentAuthority()
    });
  }

  ngOnDestroy() {
    this.saveObservableSubscription.unsubscribe();
  }

  entityForm2 = this.formBuilder.group({
  });

  selectCompetentAuthority(element: CompetentAuthority) {
    this.selectedcompetentAuthority = element;
    this.selectedcompetentAuthorityId = element.id;
    this.appService.changeHeaderTitle(element.label);
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
        if (competentAuthority.label.toLocaleLowerCase().indexOf(this.searchText) >= 0)
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

}
