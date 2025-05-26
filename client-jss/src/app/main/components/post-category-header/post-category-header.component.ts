import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { SHARED_IMPORTS } from '../../../libs/SharedImports';
import { JssCategory } from '../../model/JssCategory';
import { JssCategoryService } from '../../services/jss.category.service';
import { CategoryHubComponent } from '../category-hub/category-hub.component';

@Component({
  selector: 'post-category-header',
  templateUrl: './post-category-header.component.html',
  styleUrls: ['./post-category-header.component.css'],
  imports: [SHARED_IMPORTS, CategoryHubComponent],
  standalone: true
})
export class PostCategoryHeaderComponent implements OnInit {

  constructor(private jssCategoryService: JssCategoryService,
    private activeRoute: ActivatedRoute
  ) { }

  selectedJssCategory: JssCategory | undefined;

  ngOnInit() {
    let slug = this.activeRoute.snapshot.params['slug'];
    if (slug)
      this.jssCategoryService.getJssCategoryBySlug(slug).subscribe(response => {
        if (response)
          this.selectedJssCategory = response;
      });
  }

}
