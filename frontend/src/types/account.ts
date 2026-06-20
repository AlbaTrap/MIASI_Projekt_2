export type AccountStatus = 'PENDING_CONFIRMATION' | 'ACTIVE' | 'BLOCKED' | 'DELETED';

export type SessionStatus = 'ACTIVE' | 'EXPIRED' | 'INVALIDATED';

export type UserAccount = {
  id: string;
  email: string;
  status: AccountStatus;
  registeredAt: string;
  activatedAt: string | null;
};

export type UserSession = {
  accountId: string;
  accessToken: string;
  expiresAt: string;
  status: SessionStatus;
};

export type AuthState = {
  session: UserSession | null;
  isAuthenticated: boolean;
};
