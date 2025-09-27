import { Component, OnInit } from '@angular/core';
import { Meta, Title } from '@angular/platform-browser';
import { SHARED_IMPORTS } from '../../../libs/SharedImports';
import { TendencyHubComponent } from "../last-posts-hub/last-posts-hub.component";

@Component({
  selector: 'post-most-seen-header',
  templateUrl: './post-most-seen-header.component.html',
  imports: [SHARED_IMPORTS, TendencyHubComponent],
  standalone: true
})
export class PostMostSeenHeaderComponent implements OnInit {

  constructor(
    private titleService: Title, private meta: Meta,
  ) { }


  ngOnInit() {
    this.titleService.setTitle("Les articles les plus vus - JSS");
    this.meta.updateTag({ name: 'description', content: "Retrouvez les articles les plus vus de l'actualité juridique et économique. JSS analyse pour vous les dernières annonces, formalités et tendances locales." });
  }


}
