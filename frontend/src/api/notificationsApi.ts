import { InformatorDto, NotificationChannel, NotificationDto } from './backendTypes';
import { apiRequest } from './httpClient';

export const notificationsApi = {
  sendEventNotification(token: string, eventId: string, channel: NotificationChannel) {
    return apiRequest<NotificationDto>(`/notifications/events/${eventId}`, {
      method: 'POST',
      token,
      query: { channel },
    });
  },

  getNotifications(token: string) {
    return apiRequest<NotificationDto[]>('/notifications', {
      token,
    });
  },

  getInformators(token: string) {
    return apiRequest<InformatorDto[]>('/informators', {
      token,
    });
  },

  createInformator(token: string, eventId: string, notificationId: string) {
    return apiRequest<InformatorDto>(`/informators/events/${eventId}/notifications/${notificationId}`, {
      method: 'POST',
      token,
    });
  },
};
