import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { formatDateFrance } from 'src/app/libs/FormatHelper';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { AppService } from 'src/app/services/app.service';
import { Affaire } from '../../model/Affaire';
import { BodaccNotice } from '../../model/BodaccNotice';
import { BodaccNoticeService } from '../../services/bodacc-notice.service';

@Component({
  selector: 'bodacc-list',
  templateUrl: './bodacc-list.component.html',
  styleUrls: ['./bodacc-list.component.css']
})
export class BodaccListComponent implements OnInit {
  bodaccs: BodaccNotice[] | undefined;
  displayedColumns: SortTableColumn<BodaccNotice>[] = [];
  tableAction: SortTableAction<BodaccNotice>[] = [];
  textSearch: string = "";
  @Input() affaire: Affaire | undefined;

  constructor(
    private appService: AppService,
    private formBuilder: FormBuilder,
    private bodaccService: BodaccNoticeService
  ) { }

  ngOnInit() {
    this.appService.changeHeaderTitle("Bodacc");
    this.searchBodacc();
  }

  bodaccSearchForm = this.formBuilder.group({
  });

  searchBodacc() {
    if (this.affaire)
      this.bodaccService.getBodaccNoticeForAffaire(this.affaire.id).subscribe(response => {
        this.bodaccs = response
      });
  }

  getBodaccToDisplay() {
    if (!this.textSearch || this.textSearch.length < 2)
      return this.bodaccs;

    return this.bodaccs!.filter(b => JSON.stringify(b).toLocaleLowerCase().indexOf(this.textSearch.toLocaleLowerCase()) >= 0);
  }

  getModificationsGenerales(bodacc: BodaccNotice) {
    if (bodacc && bodacc.modificationsgenerales) {
      let parsed = JSON.parse(bodacc.modificationsgenerales);
      if (parsed && parsed.descriptif)
        return parsed.descriptif;
    }

    return "";
  }

  getDepot(bodacc: BodaccNotice) {
    if (bodacc && bodacc.depot) {
      let parsed = JSON.parse(bodacc.depot);
      let out = [];
      if (parsed && parsed.typeDepot)
        out.push(parsed.typeDepot);
      if (parsed && parsed.dateCloture)
        out.push("Clôture : " + parsed.dateCloture);
      if (parsed && parsed.descriptif)
        out.push(parsed.descriptif);

      if (out && out.length > 0)
        return out.join(" - ");
    }

    return "";
  }

  getActe(bodacc: BodaccNotice) {
    if (bodacc && bodacc.acte) {
      let parsed = JSON.parse(bodacc.acte);
      let out = [];

      if (parsed && parsed.immatriculation) {
        if (parsed.immatriculation.categorieImmatriculation)
          out.push(parsed.immatriculation.categorieImmatriculation);
        if (parsed.immatriculation.descriptif)
          out.push(parsed.immatriculation.descriptif);
        if (parsed.immatriculation.dateCommencementActivite)
          out.push(formatDateFrance(new Date(parsed.immatriculation.dateCommencementActivite)));
      }

      if (parsed && parsed.vente) {
        if (parsed.vente.categorieVente)
          out.push(parsed.vente.categorieVente);
        if (parsed.vente.descriptif)
          out.push(parsed.vente.descriptif);
        if (parsed.vente.dateImmatriculation)
          out.push(formatDateFrance(new Date(parsed.vente.dateImmatriculation)));
      }

      if (parsed && parsed.creation) {
        if (parsed.creation.categorieCreation)
          out.push(parsed.creation.categorieCreation);
        if (parsed.creation.descriptif)
          out.push(parsed.creation.descriptif);
        if (parsed.creation.dateImmatriculation)
          out.push(formatDateFrance(new Date(parsed.creation.dateImmatriculation)));
      }

      if (out && out.length > 0)
        return out.join(" - ");
    }

    return "";
  }

  openOnBodacc(bodacc: BodaccNotice) {
    window.open(bodacc.url_complete, "_blank");
  }
}
