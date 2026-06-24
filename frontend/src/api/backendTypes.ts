export type ApiResponse<T> = {
  success: boolean;
  message: string;
  data: T;
};

export type RegisterRequest = {
  email: string;
  password: string;
};

export type RegisterResponseDto = {
  accountId: string;
  email: string;
  verificationTokenForDemo: string;
};

export type LoginRequest = {
  email: string;
  password: string;
};

export type LoginResponseDto = {
  accountId: string;
  accessToken: string;
  expiresAt: string;
};

export type UserDto = {
  id: string;
  email: string;
  status: 'PENDING_CONFIRMATION' | 'ACTIVE' | 'BLOCKED' | 'DELETED';
  registeredAt: string;
  activatedAt: string | null;
  phoneNumber: string | null;
};

export type ChangePasswordRequest = {
  oldPassword: string;
  newPassword: string;
};

export type ChangePhoneNumberRequest = {
  phoneNumber: string;
};

export type CatalogEventStatus = 'DRAFT' | 'PUBLISHED' | 'HIDDEN' | 'CANCELLED' | 'ARCHIVED';

export type SearchResultDto = {
  eventId: string;
  title: string;
  shortDescription: string;
  startDate: string;
  location: string;
  category: string;
};

export type SearchResultsListDto = {
  results: SearchResultDto[];
  totalResults: number;
  pageNumber: number;
  pageSize: number;
};

export type FilterOptionsDto = {
  categories: string[];
  locations: string[];
};

export type CatalogEventDto = {
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
  status: CatalogEventStatus;
  createdAt: string;
  updatedAt: string;
  cancelReason: string | null;
};

export type FavoriteEventDto = {
  id: string;
  userId: string;
  eventId: string;
  addedAt: string;
};

export type NotificationChannel = 'EMAIL' | 'SMS';

export type NotificationStatus = 'CREATED' | 'SENT' | 'FAILED';

export type NotificationDto = {
  id: string;
  userId: string;
  eventId: string;
  channel: NotificationChannel;
  status: NotificationStatus;
  subject: string;
  message: string;
  createdAt: string;
  sentAt: string | null;
  failureReason: string | null;
};

export type InformatorDto = {
  id: string;
  userId: string;
  eventId: string;
  notificationId: string;
  message: string;
  createdAt: string;
};
