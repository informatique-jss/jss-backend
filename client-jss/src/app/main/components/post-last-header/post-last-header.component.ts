import { Component, OnInit } from '@angular/core';
import { Meta, Title } from '@angular/platform-browser';
import { SHARED_IMPORTS } from '../../../libs/SharedImports';
import { TendencyHubComponent } from "../last-posts-hub/last-posts-hub.component";

@Component({
  selector: 'post-last-header',
  templateUrl: './post-last-header.component.html',
  imports: [SHARED_IMPORTS, TendencyHubComponent],
  standalone: true
})
export class PostLastHeaderComponent implements OnInit {

  constructor(
    private titleService: Title, private meta: Meta,
  ) { }


  ngOnInit() {
    this.titleService.setTitle("Les derniers articles publiés - JSS");
    this.meta.updateTag({ name: 'description', content: "Retrouvez les derniers articles d'actualité juridique et économique. JSS analyse pour vous les dernières annonces, formalités et tendances locales." });
  }


}
