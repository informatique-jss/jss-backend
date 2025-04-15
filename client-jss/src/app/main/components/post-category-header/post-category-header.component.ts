import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { JssCategory } from '../../model/JssCategory';
import { JssCategoryService } from '../../services/jss.category.service';

@Component({
  selector: 'post-category-header',
  templateUrl: './post-category-header.component.html',
  styleUrls: ['./post-category-header.component.css'],
  standalone: false
})
export class PostCategoryHeaderComponent implements OnInit {

  constructor(private jssCategoryService: JssCategoryService,
    private activeRoute: ActivatedRoute
  ) { }

  selectedJssCategory: JssCategory | undefined;

  ngOnInit() {
    let slug = this.activeRoute.snapshot.params['slug'];
    if (slug)
      this.jssCategoryService.getAvailableJssCategories().subscribe(response => { // TODO : change by a getBySlug
        if (response)
          for (let category of response)
            if (category.slug == slug) {
              this.selectedJssCategory = category;
              break;
            }
      })
  }

}
