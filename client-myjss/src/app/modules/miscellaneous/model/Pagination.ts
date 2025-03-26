export interface Pagination<T> {
    content: T[];
    numberOfElements: number;
    totalPages: number;
    size: number;
    number: number;
    totalElements: number;
}