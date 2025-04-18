import { Component, OnInit } from '@angular/core';
import { AppService } from '../../../services/app.service';
import { Serie } from '../../model/Serie';
import { SerieService } from '../../services/serie.service';

@Component({
  selector: 'app-serie-list',
  templateUrl: './serie-list.component.html',
  styleUrls: ['./serie-list.component.css'],
  standalone: false
})
export class SerieListComponent implements OnInit {

  series: Serie[] | undefined;

  constructor(
    private serieService: SerieService,
    private appService: AppService
  ) { }

  ngOnInit() {
    this.serieService.getSeries(0, 10).subscribe(pagedSeries => {
      this.series = pagedSeries.content;
    })
  }

  openSeriePosts(serie: Serie, event: any) {
    this.appService.openRoute(event, "serie/" + serie.slug, undefined);
  }

}
