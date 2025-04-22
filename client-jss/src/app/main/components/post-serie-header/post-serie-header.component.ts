import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Serie } from '../../model/Serie';
import { SerieService } from '../../services/serie.service';

@Component({
  selector: 'app-post-serie-header',
  templateUrl: './post-serie-header.component.html',
  styleUrls: ['./post-serie-header.component.css'],
  standalone: false
})
export class PostSerieHeaderComponent implements OnInit {

  constructor(private serieService: SerieService,
    private activeRoute: ActivatedRoute
  ) { }

  selectedSerie: Serie | undefined;

  ngOnInit() {
    let slug = this.activeRoute.snapshot.params['slug'];
    if (slug)
      this.serieService.getSerieBySlug(slug).subscribe(response => {
        if (response)
          this.selectedSerie = response;
      });
  }

}
