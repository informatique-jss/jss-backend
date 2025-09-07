import { Component, OnInit } from '@angular/core';
import { Meta, Title } from '@angular/platform-browser';
import { SHARED_IMPORTS } from '../../../libs/SharedImports';
import { PremiumHubComponent } from "../premium-hub/premium-hub.component";

@Component({
  selector: 'post-tendency-header',
  templateUrl: './post-premium-header.component.html',
  imports: [SHARED_IMPORTS, PremiumHubComponent],
  standalone: true
})
export class PostPremiumHeaderComponent implements OnInit {

  constructor(
    private titleService: Title, private meta: Meta,
  ) { }

  ngOnInit() {
    this.titleService.setTitle("Les articles premium - JSS");
    this.meta.updateTag({ name: 'description', content: "Retrouvez les articles premium de l'actualité juridique et économique. JSS analyse pour vous les dernières annonces, formalités et tendances locales." });
  }
}
