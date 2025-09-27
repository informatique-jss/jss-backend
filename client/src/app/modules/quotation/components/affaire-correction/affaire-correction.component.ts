import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { formatDateTimeForSortTable } from 'src/app/libs/FormatHelper';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { AppService } from 'src/app/services/app.service';
import { Affaire } from '../../model/Affaire';
import { AffaireService } from '../../services/affaire.service';

@Component({
  selector: 'app-affaire-correction',
  templateUrl: './affaire-correction.component.html',
  styleUrls: ['./affaire-correction.component.css']
})
export class AffaireCorrectionComponent implements OnInit {

  affaires: Affaire[] | undefined;
  displayedColumns: SortTableColumn<Affaire>[] = [];
  tableAction: SortTableAction<Affaire>[] = [];

  constructor(
    private appService: AppService,
    private formBuilder: FormBuilder,
    private affaireService: AffaireService
  ) { }

  ngOnInit() {
    this.appService.changeHeaderTitle("Affaires");
    this.displayedColumns.push({ id: "affaire.denomination", fieldName: "affaireLabel", label: "Affaire", valueFonction: (element: Affaire, column: SortTableColumn<Affaire>) => { return element.denomination } } as SortTableColumn<Affaire>);
    this.displayedColumns.push({ id: "siren", fieldName: "siren", label: "Siren" } as SortTableColumn<Affaire>);
    this.displayedColumns.push({ id: "siret", fieldName: "siret", label: "Siret" } as SortTableColumn<Affaire>);
    this.displayedColumns.push({ id: "address", fieldName: "address", label: "Adresse" } as SortTableColumn<Affaire>);
    this.displayedColumns.push({ id: "postalCode", fieldName: "postalCode", label: "Code postal" } as SortTableColumn<Affaire>);
    this.displayedColumns.push({ id: "city.label", fieldName: "city", label: "Ville", valueFonction: (element: Affaire, column: SortTableColumn<Affaire>) => { return element.city ? element.city.label : "" } } as SortTableColumn<Affaire>);
    this.displayedColumns.push({ id: "createdDateTime", fieldName: "createdDateTime", label: "Date de création", valueFonction: formatDateTimeForSortTable } as SortTableColumn<Affaire>);

    this.tableAction.push({
      actionIcon: "edit", actionName: "Editer l'affaire", actionLinkFunction: (action: SortTableAction<Affaire>, element: Affaire) => {
        if (element)
          return ['/affaire', element.id];
        return undefined;
      }, display: true,
    } as SortTableAction<Affaire>);

    this.searchAffaires();
  }

  affaireSearchForm = this.formBuilder.group({
  });

  searchAffaires() {
    this.affaireService.searchAffaireForCorrection().subscribe(response => {
      this.affaires = response;
    });
  }
}
