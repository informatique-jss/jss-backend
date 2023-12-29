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
  displayedColumns: SortTableColumn<IndexEntity>[] = [];
  tableAction: SortTableAction<IndexEntity>[] = [];
  textSearch: string = "";
  searchObservableRef: Subscription | undefined;

  constructor(
    private appService: AppService,
    private formBuilder: FormBuilder,
    private indexEntityService: IndexEntityService,
  ) { }

  ngOnInit() {
    this.appService.changeHeaderTitle("Affaires");
    this.displayedColumns.push({ id: "affaireLabel", fieldName: "affaireLabel", label: "Affaire", valueFonction: (element: IndexEntity, column: SortTableColumn<IndexEntity>) => { return element.text.denomination ? element.text.denomination : element.text.firstname + ' ' + element.text.lastname } } as SortTableColumn<IndexEntity>);
    this.displayedColumns.push({ id: "siren", fieldName: "siren", label: "Siren", valueFonction: (element: IndexEntity, column: SortTableColumn<IndexEntity>) => { return element.text.siren } } as SortTableColumn<IndexEntity>);
    this.displayedColumns.push({ id: "siret", fieldName: "siret", label: "Siret", valueFonction: (element: IndexEntity, column: SortTableColumn<IndexEntity>) => { return element.text.siret } } as SortTableColumn<IndexEntity>);
    this.displayedColumns.push({ id: "rna", fieldName: "rna", label: "Rna", valueFonction: (element: IndexEntity, column: SortTableColumn<IndexEntity>) => { return element.text.rna } } as SortTableColumn<IndexEntity>);
    this.displayedColumns.push({ id: "address", fieldName: "address", label: "Adresse", valueFonction: (element: IndexEntity, column: SortTableColumn<IndexEntity>) => { return element.text.address } } as SortTableColumn<IndexEntity>);
    this.displayedColumns.push({ id: "postalCode", fieldName: "postalCode", label: "Code postal", valueFonction: (element: IndexEntity, column: SortTableColumn<IndexEntity>) => { return element.text.postalCode } } as SortTableColumn<IndexEntity>);
    this.displayedColumns.push({ id: "city", fieldName: "city", label: "Ville", valueFonction: (element: IndexEntity, column: SortTableColumn<IndexEntity>) => { return element.text.city ? element.text.city.label : "" } } as SortTableColumn<IndexEntity>);

    this.tableAction.push({
      actionIcon: "edit", actionName: "Editer l'affaire", actionLinkFunction: (action: SortTableAction<IndexEntity>, element: IndexEntity) => {
        if (element)
          return ['/affaire', element.entityId];
        return undefined;
      }, display: true,
    } as SortTableAction<IndexEntity>);
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
