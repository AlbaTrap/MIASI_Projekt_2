import {
  createContext,
  PropsWithChildren,
  useCallback,
  useContext,
  useMemo,
  useState,
} from 'react';

import { accountApi } from '../api/accountApi';
import { mapLoginResponse } from '../mappers/accountMapper';
import { UserSession } from '../types/account';

type AuthContextValue = {
  session: UserSession | null;
  isAuthenticated: boolean;
  login: (email: string, password: string) => Promise<UserSession>;
  logout: () => Promise<void>;
  refreshSession: () => Promise<UserSession>;
  clearSession: () => void;
};

const STORAGE_KEY = 'wroclaw-events-session';

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

const readStoredSession = (): UserSession | null => {
  const rawValue = window.localStorage.getItem(STORAGE_KEY);

  if (!rawValue) {
    return null;
  }

  try {
    return JSON.parse(rawValue) as UserSession;
  } catch {
    window.localStorage.removeItem(STORAGE_KEY);
    return null;
  }
};

const persistSession = (session: UserSession | null) => {
  if (!session) {
    window.localStorage.removeItem(STORAGE_KEY);
    return;
  }

  window.localStorage.setItem(STORAGE_KEY, JSON.stringify(session));
};

export function AuthProvider({ children }: PropsWithChildren) {
  const [session, setSession] = useState<UserSession | null>(() => readStoredSession());

  const updateSession = useCallback((nextSession: UserSession | null) => {
    setSession(nextSession);
    persistSession(nextSession);
  }, []);

  const login = useCallback(
    async (email: string, password: string) => {
      const response = await accountApi.login({ email, password });
      const nextSession = mapLoginResponse(response);
      updateSession(nextSession);
      return nextSession;
    },
    [updateSession],
  );

  const logout = useCallback(async () => {
    if (session?.accessToken) {
      await accountApi.logout(session.accessToken);
    }

    updateSession(null);
  }, [session?.accessToken, updateSession]);

  const refreshSession = useCallback(async () => {
    if (!session?.accessToken) {
      throw new Error('Brak aktywnej sesji.');
    }

    const response = await accountApi.refresh(session.accessToken);
    const nextSession = mapLoginResponse(response);
    updateSession(nextSession);
    return nextSession;
  }, [session?.accessToken, updateSession]);

  const clearSession = useCallback(() => {
    updateSession(null);
  }, [updateSession]);

  const value = useMemo<AuthContextValue>(
    () => ({
      session,
      isAuthenticated: Boolean(session?.accessToken),
      login,
      logout,
      refreshSession,
      clearSession,
    }),
    [clearSession, login, logout, refreshSession, session],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export const useAuth = () => {
  const value = useContext(AuthContext);

  if (!value) {
    throw new Error('useAuth must be used inside AuthProvider.');
  }

  return value;
};
