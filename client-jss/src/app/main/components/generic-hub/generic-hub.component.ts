import { Directive, Input, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { PagedContent } from '../../model/PagedContent';
import { Post } from '../../model/Post';
import { Tag } from '../../model/Tag';

@Directive()
export abstract class GenericHubComponent<T> implements OnInit {

  @Input() selectedEntityType: T | undefined;
  linkedTags: Tag[] = [] as Array<Tag>;
  mostSeenPosts: Post[] = [] as Array<Post>;
  postsByEntityType: Post[] = [] as Array<Post>;
  pageSize: number = 1;
  page: number = 0;
  totalPage: number = 0;

  constructor() { }

  //TODO variabiliser la catégorie, récup les tags liés, récup articles par page, récup articles plus vus, barre de recherche
  //comment savoir combien de numéros de pages affichés si on doit envoyer une requete au back à chaque changement de page ?
  // récupérer l'ensemble des posts par pages dès l'init ?


  ngOnInit() {
    this.fetchPosts(0);
  }

  abstract getAllPostByEntityType(selectedEntityType: T, page: number, pageSize: number): Observable<PagedContent<Post>>;

  fetchPosts(page: number) {
    if (this.selectedEntityType)
      this.getAllPostByEntityType(this.selectedEntityType, page, this.pageSize).subscribe(data => {
        this.postsByEntityType = data.content;
        this.totalPage = data.page.totalPages;
      });
  }

  goToPage(pageNumber: number): void {
    if (pageNumber >= 0 && pageNumber < this.totalPage) {
      this.fetchPosts(pageNumber);
    }
  }

  get pages(): number[] {
    const pagesToShow = 5;
    const start = Math.max(0, this.page - Math.floor(pagesToShow / 2));
    const end = Math.min(this.totalPage, start + pagesToShow);
    return Array.from({ length: end - start }, (_, i) => start + i);
  }

}
