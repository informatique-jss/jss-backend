import { Component, OnInit } from '@angular/core';
import { Meta, Title } from '@angular/platform-browser';
import { SHARED_IMPORTS } from '../../../libs/SharedImports';
import { TendencyHubComponent } from '../tendency-hub/tendency-hub.component';

@Component({
  selector: 'post-tendency-header',
  templateUrl: './post-tendency-header.component.html',
  styleUrls: ['./post-tendency-header.component.css'],
  imports: [SHARED_IMPORTS, TendencyHubComponent],
  standalone: true
})
export class PostTendencyHeaderComponent implements OnInit {

  constructor(
    private titleService: Title, private meta: Meta,
  ) { }

  ngOnInit() {
    this.titleService.setTitle("Les articles en tendances - JSS");
    this.meta.updateTag({ name: 'description', content: "Anticipez les évolutions du droit avec les articles tendances de JSS. Nous analysons pour vous les sujets émergents et les futurs enjeux juridiques et économiques." });
  }
}
