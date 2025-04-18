import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Author } from '../../model/Author';
import { AuthorService } from '../../services/author.service';

@Component({
  selector: 'app-post-author-header',
  templateUrl: './post-author-header.component.html',
  styleUrls: ['./post-author-header.component.css'],
  standalone: false
})
export class PostAuthorHeaderComponent implements OnInit {

  constructor(private authorService: AuthorService,
    private activeRoute: ActivatedRoute
  ) { }

  selectedAuthor: Author | undefined;

  ngOnInit() {
    let slug = this.activeRoute.snapshot.params['slug'];
    if (slug)
      this.authorService.getAuthorBySlug(slug).subscribe(response => {
        if (response)
          this.selectedAuthor = response;
      });
  }

}
