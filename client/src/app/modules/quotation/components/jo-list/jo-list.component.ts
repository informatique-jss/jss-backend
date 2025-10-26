import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { AppService } from 'src/app/services/app.service';
import { Affaire } from '../../model/Affaire';
import { JoNotice } from '../../model/JoNotice';
import { JoNoticeService } from '../../services/jo-notice.service';

@Component({
  selector: 'jo-list',
  templateUrl: './jo-list.component.html',
  styleUrls: ['./jo-list.component.css']
})
export class JoListComponent implements OnInit {
  jos: JoNotice[] | undefined;
  displayedColumns: SortTableColumn<JoNotice>[] = [];
  tableAction: SortTableAction<JoNotice>[] = [];
  textSearch: string = "";
  @Input() affaire: Affaire | undefined;

  constructor(
    private appService: AppService,
    private formBuilder: FormBuilder,
    private joService: JoNoticeService
  ) { }

  ngOnInit() {
    this.appService.changeHeaderTitle("JO Associations");
    this.searchJo();
  }

  joSearchForm = this.formBuilder.group({
  });

  searchJo() {
    if (this.affaire)
      this.joService.getJoNoticeForAffaire(this.affaire.id).subscribe(response => {
        this.jos = response
      });
  }

  getJoToDisplay() {
    if (!this.textSearch || this.textSearch.length < 2)
      return this.jos;

    return this.jos!.filter(b => JSON.stringify(b).toLocaleLowerCase().indexOf(this.textSearch.toLocaleLowerCase()) >= 0);
  }

  openOnJo(jo: JoNotice) {
    window.open('https://www.journal-officiel.gouv.fr/pages/associations-detail-annonce/?q.id=id:' + jo.id, "_blank");
  }
}
