import { Pagination } from "./Pagination";

export interface PagedContent<T> {
  content: T[];
  page: Pagination;
}
