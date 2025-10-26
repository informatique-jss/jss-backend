import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { AppService } from 'src/app/services/app.service';
import { Affaire } from '../../model/Affaire';
import { BaloNotice } from '../../model/BaloNotice';
import { BaloNoticeService } from '../../services/balo-notice.service';

@Component({
  selector: 'balo-list',
  templateUrl: './balo-list.component.html',
  styleUrls: ['./balo-list.component.css']
})
export class BaloListComponent implements OnInit {
  balos: BaloNotice[] | undefined;
  displayedColumns: SortTableColumn<BaloNotice>[] = [];
  tableAction: SortTableAction<BaloNotice>[] = [];
  textSearch: string = "";
  @Input() affaire: Affaire | undefined;

  constructor(
    private appService: AppService,
    private formBuilder: FormBuilder,
    private baloService: BaloNoticeService
  ) { }

  ngOnInit() {
    this.appService.changeHeaderTitle("BALO");
    this.searchBalo();
  }

  baloSearchForm = this.formBuilder.group({
  });

  searchBalo() {
    if (this.affaire)
      this.baloService.getBaloNoticeForAffaire(this.affaire.id).subscribe(response => {
        this.balos = response
      });
  }

  getBaloToDisplay() {
    if (!this.textSearch || this.textSearch.length < 2)
      return this.balos;

    return this.balos!.filter(b => JSON.stringify(b).toLocaleLowerCase().indexOf(this.textSearch.toLocaleLowerCase()) >= 0);
  }

  openOnBalo(balo: BaloNotice) {
    window.open('https://www.journal-officiel.gouv.fr/pages/balo-annonce-unitaire/?q.id=id_annonce:' + balo.id_annonce, "_blank");
  }

  getLabel(balo: BaloNotice) {
    if (balo.facette_categorie_libelle)
      return balo.facette_categorie_libelle.replace("#", " - ").replace("#", "");
    return "";
  }
}
