import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Subscription } from 'rxjs';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { IndexEntity } from 'src/app/routing/search/IndexEntity';
import { IndexEntityService } from 'src/app/routing/search/index.entity.service';
import { AppService } from 'src/app/services/app.service';
import { AFFAIRE_ENTITY_TYPE } from '../../../../routing/search/search.component';

@Component({
  selector: 'affaire-list',
  templateUrl: './affaire-list.component.html',
  styleUrls: ['./affaire-list.component.css']
})
export class AffaireListComponent implements OnInit {
  affaires: IndexEntity[] | undefined;
  displayedColumns: SortTableColumn[] = [];
  tableAction: SortTableAction[] = [];
  textSearch: string = "";
  searchObservableRef: Subscription | undefined;

  constructor(
    private appService: AppService,
    private formBuilder: FormBuilder,
    private indexEntityService: IndexEntityService,
  ) { }

  ngOnInit() {
    this.appService.changeHeaderTitle("Affaires");
    this.displayedColumns.push({ id: "affaireLabel", fieldName: "affaireLabel", label: "Affaire", valueFonction: (element: any) => { return element.text.denomination ? element.text.denomination : element.text.firstname + ' ' + element.text.lastname } } as SortTableColumn);
    this.displayedColumns.push({ id: "siren", fieldName: "siren", label: "Siren", valueFonction: (element: any) => { return element.text.siren } } as SortTableColumn);
    this.displayedColumns.push({ id: "siret", fieldName: "siret", label: "Siret", valueFonction: (element: any) => { return element.text.siret } } as SortTableColumn);
    this.displayedColumns.push({ id: "rna", fieldName: "rna", label: "Rna", valueFonction: (element: any) => { return element.text.rna } } as SortTableColumn);
    this.displayedColumns.push({ id: "address", fieldName: "address", label: "Adresse", valueFonction: (element: any) => { return element.text.address } } as SortTableColumn);
    this.displayedColumns.push({ id: "postalCode", fieldName: "postalCode", label: "Code postal", valueFonction: (element: any) => { return element.text.postalCode } } as SortTableColumn);
    this.displayedColumns.push({ id: "city", fieldName: "city", label: "Ville", valueFonction: (element: any) => { return element.text.city ? element.text.city.label : "" } } as SortTableColumn);

    this.tableAction.push({
      actionIcon: "edit", actionName: "Editer l'affaire", actionLinkFunction: (action: SortTableAction, element: any) => {
        if (element)
          return ['/affaire', element.entityId];
        return undefined;
      }, display: true,
    } as SortTableAction);
  }

  affaireSearchForm = this.formBuilder.group({
  });

  searchAffaires() {
    if (this.searchObservableRef)
      this.searchObservableRef.unsubscribe();

    if (this.textSearch.length >= 2) {
      this.searchObservableRef = this.indexEntityService.searchEntitiesByType(this.textSearch, AFFAIRE_ENTITY_TYPE).subscribe(response => {
        this.affaires = response;
        if (this.affaires)
          for (let affaire of this.affaires)
            affaire.text = JSON.parse(affaire.text);
      });
    }
  }
}
