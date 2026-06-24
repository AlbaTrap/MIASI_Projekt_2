import { CatalogEventDto, FavoriteEventDto } from './backendTypes';
import { apiRequest } from './httpClient';

export const favoritesApi = {
  addFavorite(token: string, eventId: string) {
    return apiRequest<FavoriteEventDto>(`/favorites/${eventId}`, {
      method: 'POST',
      token,
    });
  },

  removeFavorite(token: string, eventId: string) {
    return apiRequest<null>(`/favorites/${eventId}`, {
      method: 'DELETE',
      token,
    });
  },

  getFavorites(token: string) {
    return apiRequest<FavoriteEventDto[]>('/favorites', {
      token,
    });
  },

  getFavoriteEvents(token: string) {
    return apiRequest<CatalogEventDto[]>('/favorites/events', {
      token,
    });
  },
};
