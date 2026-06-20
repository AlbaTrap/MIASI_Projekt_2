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
};

export type ChangePasswordRequest = {
  oldPassword: string;
  newPassword: string;
};

export type EventDto = {
  id: string;
  title: string;
  description: string;
  city: string;
  address: string;
  startDate: string;
  endDate: string | null;
  category: string;
  status:
    | 'RAW'
    | 'NORMALIZED'
    | 'AVAILABLE'
    | 'REJECTED'
    | 'PUBLISHED'
    | 'UPDATED'
    | 'HIDDEN'
    | 'CANCELLED'
    | 'ARCHIVED';
  updateNotice?: string;
};

export type FavoriteEventDto = {
  id: string;
  userId: string;
  eventId: string;
  addedAt: string;
};
