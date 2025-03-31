import { AfterContentChecked, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatTabChangeEvent } from '@angular/material/tabs';
import { formatDateTimeForSortTable } from 'src/app/libs/FormatHelper';
import { EditCommentDialogComponent } from 'src/app/modules/miscellaneous/components/edit-comment-dialog.component/edit-comment-dialog-component.component';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { AppService } from 'src/app/services/app.service';
import { Pagination } from 'src/app/services/model/Pagination';
import { UserPreferenceService } from 'src/app/services/user.preference.service';
import { Comment } from '../../model/Comment';
import { CommentSearch } from '../../model/CommentSearch';
import { CommentService } from '../../services/comment.service';

@Component({
  selector: 'myjss-comment-management',
  templateUrl: './myjss-comment-management.component.html',
  styleUrls: ['./myjss-comment-management.component.css']
})
export class MyjssCommentManagementComponent implements OnInit, AfterContentChecked {

  myjssComments: Comment[] = [];
  myjssCommentsPagination: Pagination = {} as Pagination;
  displayedColumnsComments: SortTableColumn<Comment>[] = [];
  tableActionComment: SortTableAction<Comment>[] = [];

  commentSearch: CommentSearch | undefined;



  constructor(
    private userPreferenceService: UserPreferenceService,
    private formBuilder: FormBuilder,
    private appService: AppService,
    private commentService: CommentService,
    private changeDetectorRef: ChangeDetectorRef,
    private editCommentDialog: MatDialog,
  ) { }

  ngOnInit() {
    this.displayedColumnsComments = [];
    this.commentSearch = {} as CommentSearch;

    this.displayedColumnsComments.push({ id: "id", fieldName: "id", label: "N°" } as SortTableColumn<Comment>);
    this.displayedColumnsComments.push({ id: "content", fieldName: "content", label: "Contenu du commentaire", isShrinkColumn: true } as SortTableColumn<Comment>);
    this.displayedColumnsComments.push({ id: "entityId", fieldName: "post.titleText", label: "Titre de l'article", isShrinkColumn: true } as SortTableColumn<Comment>);
    this.displayedColumnsComments.push({ id: "creationDate", fieldName: "creationDate", label: "Date de création", valueFonction: formatDateTimeForSortTable } as SortTableColumn<Comment>);
    this.displayedColumnsComments.push({ id: "mail", fieldName: "mail.mail", label: "Mail de l'auteur" } as SortTableColumn<Comment>);
    this.displayedColumnsComments.push({ id: "firstName&lastName", fieldName: "authorFirstName " + "authorLastName", label: "Prénom & nom de l'auteur", valueFonction: (element: Comment, column: SortTableColumn<Comment>) => { return (element.authorFirstName + " " + element.authorLastName) } } as SortTableColumn<Comment>);
    this.displayedColumnsComments.push({ id: "isModerated", fieldName: "isModerated", label: "Est modéré ?", valueFonction: (element: Comment, column: SortTableColumn<Comment>) => { return (element.isModerated) ? "Oui" : "Non" } } as SortTableColumn<Comment>);
    this.displayedColumnsComments.push({ id: "isDeleted", fieldName: "isDeleted", label: "Est supprimé ?", valueFonction: (element: Comment, column: SortTableColumn<Comment>) => { return (element.isDeleted) ? "Oui" : "Non" } } as SortTableColumn<Comment>);

    this.tableActionComment.push({
      actionIcon: "visibility", actionName: "Marquer comme modéré", actionClick: (column: SortTableAction<Comment>, element: Comment, event: any) => {
        element.isModerated = true;
        this.commentService.updateIsModerated(element.isModerated, element.id).subscribe();
      }, display: true,
    } as SortTableAction<Comment>);

    this.tableActionComment.push({
      actionIcon: "visibility_off", actionName: "Marquer comme non modéré", actionClick: (column: SortTableAction<Comment>, element: Comment, event: any) => {
        element.isModerated = false;
        this.commentService.updateIsModerated(element.isModerated, element.id).subscribe();
      }, display: true,
    } as SortTableAction<Comment>);

    this.tableActionComment.push({
      actionIcon: 'mode_comment', actionName: 'Modifier le commentaire', actionClick: (column: SortTableAction<Comment>, element: Comment, event: any) => {
        let dialogRef = this.editCommentDialog.open(EditCommentDialogComponent, {
          width: '40%'
        });
        dialogRef.componentInstance.comment = element.content;

        dialogRef.afterClosed().subscribe(newContent => {
          if (newContent) {
            this.commentService.updateContent(newContent, element.id).subscribe(response => { this.searchComments(this.myjssCommentsPagination.pageNumber) });
          }
        });
      }, display: true,
    } as SortTableAction<Comment>);

    this.tableActionComment.push({
      actionIcon: "open_in_new", actionName: "Voir le commentaire dans l'article", actionClick: (column: SortTableAction<Comment>, element: Comment, event: any) => {
        this.appService.openMyJssRoute("post/" + element.post.slug + "#" + element.post.id);
      }, display: true,
    } as SortTableAction<Comment>);

    this.tableActionComment.push({
      actionIcon: "delete", actionName: "Supprimer le commentaire", actionClick: (column: SortTableAction<Comment>, element: Comment, event: any) => {
        element.isDeleted = true;
        this.commentService.updateIsDeleted(element.isDeleted, element.id).subscribe();
      }, display: true,
    } as SortTableAction<Comment>);
  }

  commentSearchForm = this.formBuilder.group({
  });

  searchComments(page: number) {
    if (this.commentSearch) {
      if (this.commentSearch.creationDate) {
        this.commentSearch.creationDate = new Date(this.commentSearch.creationDate.setHours(12));
      }
      this.commentService.getComments(page, 1000000, this.commentSearch).subscribe(data => {
        if (page == 0) {
          this.myjssComments = data.content;
        }
        this.myjssCommentsPagination = data.page;
      })
    }
  }

  //Tabs management
  index: number = 0;
  onTabChange(event: MatTabChangeEvent) {
    this.userPreferenceService.setUserTabsSelectionIndex('myjss-comment-management', event.index);
  }

  ngAfterContentChecked(): void {
    this.changeDetectorRef.detectChanges();
  }
}


