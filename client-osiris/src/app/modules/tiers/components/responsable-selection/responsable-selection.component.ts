import { AsyncPipe } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbPagination, NgbPaginationNext, NgbPaginationPrevious } from '@ng-bootstrap/ng-bootstrap';
import { NgIcon } from '@ng-icons/core';
import { LucideAngularModule, LucideSearch, LucideShield, LucideUserCheck } from 'lucide-angular';
import { Observable } from 'rxjs';
import { NgbdSortableHeader } from '../../../../libs/inspinia/directive/sortable.directive';
import { TableService } from '../../../../libs/inspinia/services/table.service';
import { toTitleCase } from '../../../../libs/inspinia/utils/string-utils';
import { Responsable } from '../../../profile/model/Responsable';
import { Tiers } from '../../../profile/model/Tiers';
import { ResponsableService } from '../../services/responsable.service';
import { TiersService } from '../../services/tiers.service';

type UserType = {
    id: string;
    name: string;
    email: string;
    avatar: string;
    role: string;
    date: string;
    time: string;
    status: "inactive" | "active" | "suspended",
    selected?: boolean
}

@Component({
    selector: 'responsable-selection',
    imports: [
        LucideAngularModule,
        NgIcon,
        NgbdSortableHeader,
        FormsModule,
        NgbPagination,
        NgbPaginationNext,
        NgbPaginationPrevious,
        AsyncPipe
    ],
    standalone: true,
    providers: [TableService],
    templateUrl: './responsable-selection.component.html'
})
export class ResponsableSelectionComponent implements OnInit {
    selectAll = false;

    tiers: Tiers | undefined;

    users$: Observable<Responsable[]>
    total$: Observable<number>;

    users: Responsable[] = [];
    protected readonly toTitleCase = toTitleCase;

    protected readonly LucideSearch = LucideSearch;
    protected readonly LucideShield = LucideShield;
    protected readonly LucideUserCheck = LucideUserCheck;

    constructor(
        public tableService: TableService<Responsable>,
        private responsableService: ResponsableService,
        private tiersService: TiersService,
        private cdr: ChangeDetectorRef,

    ) {
        this.users$ = this.tableService.items$
        this.total$ = this.tableService.total$
    }

    ngOnInit(): void {
        this.tiersService.getSelectedTiers().subscribe(tiersId => {
            if (tiersId) {
                this.responsableService.getResponsablesByTiers(tiersId).subscribe(res => {
                    this.users = res;
                    this.tableService.setItems(this.users, 4);
                });
            }
        });
    }

    toggleAllSelection() {
        this.tableService.setAllSelection(this.selectAll);
        this.responsableService.setSelectedResponsables(this.tableService.getSelectedItems());
    }

    toggleSingleSelection() {
        this.tableService.total$.subscribe(number => {
            this.selectAll = this.tableService.getSelectedItems().length == number;
            this.responsableService.setSelectedResponsables(this.tableService.getSelectedItems());
        });
    }

    deleteSelected() {
        this.tableService.deleteSelectedItems();
        this.selectAll = false;
    }

    get hasSelection(): boolean {
        return this.tableService.hasSelectedItems();
    }
}
