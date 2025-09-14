import { Component, OnInit } from '@angular/core';
import { Meta, Title } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import { SHARED_IMPORTS } from '../../../libs/SharedImports';
import { Serie } from '../../model/Serie';
import { SerieService } from '../../services/serie.service';
import { SerieHubComponent } from '../serie-hub/serie-hub.component';

@Component({
  selector: 'app-post-serie-header',
  templateUrl: './post-serie-header.component.html',
  styleUrls: ['./post-serie-header.component.css'],
  imports: [SHARED_IMPORTS, SerieHubComponent],
  standalone: true
})
export class PostSerieHeaderComponent implements OnInit {

  constructor(private serieService: SerieService,
    private titleService: Title, private meta: Meta,
    private activeRoute: ActivatedRoute
  ) { }

  selectedSerie: Serie | undefined;

  ngOnInit() {
    this.refresh();
    this.activeRoute.paramMap.subscribe(params => {
      this.refresh();
    });
  }

  refresh() {
    this.titleService.setTitle("Tous nos articles - JSS");
    this.meta.updateTag({ name: 'description', content: "Retrouvez l'actualité juridique et économique. JSS analyse pour vous les dernières annonces, formalités et tendances locales." });
    let slug = this.activeRoute.snapshot.params['slug'];
    if (slug)
      this.serieService.getSerieBySlug(slug).subscribe(response => {
        if (response) {
          this.selectedSerie = response;
          this.titleService.setTitle("Tous nos articles - " + this.selectedSerie.name + " - JSS");
          this.meta.updateTag({ name: 'description', content: this.selectedSerie.name + " - Retrouvez l'actualité juridique et économique. JSS analyse pour vous les dernières annonces, formalités et tendances locales." });
        }
      });
  }

}
