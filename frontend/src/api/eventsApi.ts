import { CatalogEventDto, FilterOptionsDto, SearchResultsListDto } from './backendTypes';
import { apiRequest } from './httpClient';

export type SearchEventsParams = {
  phrase?: string;
  category?: string;
  location?: string;
  from?: string;
  to?: string;
  sortBy?: string;
  direction?: string;
  page?: number;
  size?: number;
};

export const eventsApi = {
  searchEvents(params: SearchEventsParams = {}) {
    return apiRequest<SearchResultsListDto>('/events', {
      query: params,
    });
  },

  getEventFilters() {
    return apiRequest<FilterOptionsDto>('/events/filters');
  },

  getEventDetails(eventId: string) {
    return apiRequest<CatalogEventDto>(`/catalog/events/${eventId}`);
  },
};
