import { EventDto } from './backendTypes';
import { apiRequest } from './httpClient';

type SearchEventsParams = {
  category?: string;
  city?: string;
  from?: string;
  to?: string;
};

export const eventsApi = {
  searchEvents(params: SearchEventsParams = {}) {
    return apiRequest<EventDto[]>('/events', {
      query: params,
    });
  },

  getEventDetails(eventId: string) {
    return apiRequest<EventDto>(`/events/${eventId}`);
  },
};
