import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { GenreService } from 'src/app/modules/miscellaneous/services/guichet-unique/genre.service';
import { Genre } from 'src/app/modules/quotation/model/guichet-unique/referentials/Genre';
import { GenericRadioGroupComponent } from '../../generic-radio-group/generic-radio-group.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'radio-group-genre',
  templateUrl: '../../generic-radio-group/generic-radio-group-code.component.html',
  styleUrls: ['../../generic-radio-group/generic-radio-group.component.css']
})
export class RadioGroupGenreComponent extends GenericRadioGroupComponent<Genre> implements OnInit {
  types: Genre[] = [] as Array<Genre>;

  constructor(
    private formBuild: UntypedFormBuilder, private GenreService: GenreService, private appService3: AppService) {
    super(formBuild, appService3);
  }

  initTypes(): void {
    this.GenreService.getGenre().subscribe(response => { this.types = response })
  }
}
