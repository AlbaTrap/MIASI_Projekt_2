export type EventStatus =
  | 'RAW'
  | 'NORMALIZED'
  | 'AVAILABLE'
  | 'REJECTED'
  | 'PUBLISHED'
  | 'UPDATED'
  | 'HIDDEN'
  | 'CANCELLED'
  | 'ARCHIVED';

export type CatalogEvent = {
  id: string;
  title: string;
  description: string;
  city: string;
  address: string;
  startDate: string;
  endDate: string | null;
  category: string;
  status: EventStatus;
  updateNotice?: string;
};

export type SearchSortField = 'DATE' | 'NAME' | 'CATEGORY';

export type SearchSortDirection = 'ASC' | 'DESC';

export type SearchQuery = {
  phrase: string;
  category: string;
  dateFrom: string;
  dateTo: string;
  location: string;
  selectedLocations: string[];
  sortField: SearchSortField;
  sortDirection: SearchSortDirection;
  pageNumber: number;
  pageSize: number;
};

export type SearchResult = {
  eventId: string;
  name: string;
  shortDescription: string;
  startsAt: string;
  location: string;
  category: string;
  status: EventStatus;
  lifecycleNotice?: string;
};

export type SearchResultList = {
  results: SearchResult[];
  resultCount: number;
  pageNumber: number;
  pageSize: number;
  totalPages: number;
};
