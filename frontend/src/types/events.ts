export type EventStatus = 'DRAFT' | 'PUBLISHED' | 'HIDDEN' | 'CANCELLED' | 'ARCHIVED';

export type CatalogEvent = {
  id: string;
  title: string;
  description: string;
  placeName: string;
  city: string;
  address: string;
  startDate: string;
  endDate: string | null;
  category: string;
  organizerName: string;
  sourceType: string;
  status: EventStatus;
  createdAt: string;
  updatedAt: string;
  cancelReason: string | null;
};

export type SearchSortField = 'DATE' | 'NAME' | 'CATEGORY';

export type SearchSortDirection = 'ASC' | 'DESC';

export type SearchQuery = {
  phrase: string;
  category: string;
  location: string;
  selectedLocations: string[];
  dateFrom: string;
  dateTo: string;
  sortField: SearchSortField;
  sortDirection: SearchSortDirection;
  pageNumber: number;
  pageSize: number;
};

export type EventFilterOptions = {
  categories: string[];
  locations: string[];
};

export type SearchResult = {
  eventId: string;
  name: string;
  shortDescription: string;
  startsAt: string;
  location: string;
  category: string;
  status?: EventStatus;
  cancelReason?: string | null;
};

export type SearchResultList = {
  results: SearchResult[];
  resultCount: number;
  pageNumber: number;
  pageSize: number;
  totalPages: number;
};
