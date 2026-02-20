import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { SHARED_IMPORTS } from '../../../libs/SharedImports';
import { TrustHtmlPipe } from "../../../libs/TrustHtmlPipe";
import { Serie } from '../../model/Serie';
import { SerieService } from '../../services/serie.service';

@Component({
  selector: 'series',
  templateUrl: './series.component.html',
  styleUrls: ['./series.component.css'],
  imports: [SHARED_IMPORTS, TrustHtmlPipe],
  standalone: true
})
export class SeriesComponent implements OnInit {

  pageSize: number = 4;
  page: number = 0;
  totalPage: number = 0;
  series: Serie[] = [] as Array<Serie>;
  topSerie: Serie | undefined;

  constructor(private serieService: SerieService,
    protected activeRoute: ActivatedRoute,
    protected router: Router) { }

  ngOnInit() {
    this.serieService.getSeries(0, 1).subscribe(response => {
      if (response)
        this.topSerie = response.content[0];
    });
    this.router.events.subscribe(url => {
      if (url instanceof NavigationEnd && this.activeRoute.firstChild) {
        this.activeRoute.firstChild.paramMap.subscribe(paramsMap => {
          let newPage = paramsMap.get("page-number");
          if (newPage != undefined)
            this.page = Number.parseInt(newPage);
        });
      }
      this.fetchSeries(this.page, this.pageSize);
    });
  }

  fetchSeries(page: number, pageSize: number) {
    this.serieService.getSeries(page, pageSize).subscribe(response => {
      if (response)
        this.series = response.content;
      this.totalPage = response.page.totalPages;
    });
  }

  get pages(): number[] {
    const pagesToShow = 5;
    let start = Math.max(0, this.page - Math.floor(pagesToShow / 2));
    let end = start + pagesToShow;

    if (end > this.totalPage) {
      end = this.totalPage;
      start = Math.max(0, end - pagesToShow);
    }
    return Array.from({ length: end - start }, (_, i) => start + i);
  }
}
