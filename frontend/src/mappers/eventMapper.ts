import {
  CatalogEventDto,
  FilterOptionsDto,
  SearchResultDto,
  SearchResultsListDto,
} from '../api/backendTypes';
import { CatalogEvent, EventFilterOptions, SearchResult, SearchResultList } from '../types/events';
import { formatCategoryLabel } from '../utils/eventLabels';

export const mapCatalogEventDto = (dto: CatalogEventDto): CatalogEvent => ({
  id: dto.id,
  title: dto.title,
  description: dto.description || '',
  placeName: dto.placeName || '',
  city: dto.city || '',
  address: dto.address || '',
  startDate: dto.startDate,
  endDate: dto.endDate,
  category: formatCategoryLabel(dto.category),
  organizerName: dto.organizerName || '',
  sourceType: dto.sourceType || '',
  status: dto.status,
  createdAt: dto.createdAt,
  updatedAt: dto.updatedAt,
  cancelReason: dto.cancelReason || null,
});

export const mapSearchResultDto = (dto: SearchResultDto): SearchResult => ({
  eventId: dto.eventId,
  name: dto.title,
  shortDescription: dto.shortDescription || '',
  startsAt: dto.startDate,
  location: dto.location || '',
  category: formatCategoryLabel(dto.category),
});

export const mapSearchResultsListDto = (dto: SearchResultsListDto): SearchResultList => {
  const pageSize = Math.max(1, dto.pageSize);

  return {
    results: dto.results.map(mapSearchResultDto),
    resultCount: dto.totalResults,
    pageNumber: dto.pageNumber + 1,
    pageSize,
    totalPages: Math.max(1, Math.ceil(dto.totalResults / pageSize)),
  };
};

export const mapFilterOptionsDto = (dto: FilterOptionsDto): EventFilterOptions => ({
  categories: dto.categories,
  locations: dto.locations,
});

export const mapCatalogEventToSearchResult = (event: CatalogEvent): SearchResult => ({
  eventId: event.id,
  name: event.title,
  shortDescription: event.description,
  startsAt: event.startDate,
  location: [event.placeName, event.address, event.city].filter(Boolean).join(', '),
  category: event.category,
  status: event.status,
  cancelReason: event.cancelReason,
});
