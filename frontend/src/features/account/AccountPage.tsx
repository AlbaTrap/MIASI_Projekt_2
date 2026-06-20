import { useEffect, useState } from 'react';
import { Link, Navigate, useNavigate } from 'react-router-dom';

import { accountApi } from '../../api/accountApi';
import { AccountStatusBadge, accountStatusLabels } from '../../components/account/AccountStatusBadge';
import { ChangePasswordModal } from '../../components/account/ChangePasswordModal';
import { DeleteAccountModal } from '../../components/account/DeleteAccountModal';
import { AppButton } from '../../components/ui/AppButton';
import { LoadingState } from '../../components/ui/LoadingState';
import { mapUserDto } from '../../mappers/accountMapper';
import { useAuth } from '../../state/authStore';
import { SessionStatus, UserAccount } from '../../types/account';

const sessionStatusLabels: Record<SessionStatus, string> = {
  ACTIVE: 'Aktywna',
  EXPIRED: 'Wygasła',
  INVALIDATED: 'Unieważniona',
};

export function AccountPage() {
  const navigate = useNavigate();
  const { clearSession, isAuthenticated, logout, refreshSession, session } = useAuth();
  const [account, setAccount] = useState<UserAccount | null>(null);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [passwordError, setPasswordError] = useState('');
  const [deleteError, setDeleteError] = useState('');
  const [isLoading, setIsLoading] = useState(true);
  const [isDeleteSubmitting, setIsDeleteSubmitting] = useState(false);
  const [changePasswordOpen, setChangePasswordOpen] = useState(false);
  const [deleteAccountOpen, setDeleteAccountOpen] = useState(false);

  useEffect(() => {
    let active = true;

    const loadAccount = async () => {
      if (!session?.accessToken) {
        setIsLoading(false);
        return;
      }

      try {
        setIsLoading(true);
        setError('');
        const data = await accountApi.me(session.accessToken);

        if (active) {
          setAccount(mapUserDto(data));
        }
      } catch (caughtError) {
        if (active) {
          setError(caughtError instanceof Error ? caughtError.message : 'Nie udało się pobrać konta.');
        }
      } finally {
        if (active) {
          setIsLoading(false);
        }
      }
    };

    void loadAccount();

    return () => {
      active = false;
    };
  }, [session?.accessToken]);

  if (!isAuthenticated) {
    return <Navigate replace to="/login" />;
  }

  const handleRefresh = async () => {
    try {
      await refreshSession();
      setError('');
      setMessage('Sesja została odświeżona.');
    } catch (caughtError) {
      setMessage('');
      setError(caughtError instanceof Error ? caughtError.message : 'Nie udało się odświeżyć sesji.');
      clearSession();
    }
  };

  const handleLogout = async () => {
    try {
      await logout();
    } catch {
      clearSession();
    }

    navigate('/login');
  };

  const handleChangePassword = async (oldPassword: string, newPassword: string) => {
    if (!session?.accessToken) {
      return;
    }

    try {
      await accountApi.changePassword(session.accessToken, { oldPassword, newPassword });
      setPasswordError('');
      setChangePasswordOpen(false);
      clearSession();
      navigate('/login');
    } catch (caughtError) {
      setPasswordError(caughtError instanceof Error ? caughtError.message : 'Nie udało się zmienić hasła.');
    }
  };

  const handleDeleteAccount = async () => {
    if (!session?.accessToken) {
      return;
    }

    try {
      setIsDeleteSubmitting(true);
      await accountApi.deleteMe(session.accessToken);
      setDeleteError('');
      setDeleteAccountOpen(false);
      clearSession();
      navigate('/login');
    } catch (caughtError) {
      setDeleteError(caughtError instanceof Error ? caughtError.message : 'Nie udało się usunąć konta.');
    } finally {
      setIsDeleteSubmitting(false);
    }
  };

  return (
    <main className="page-shell narrow-page">
      <section className="panel">
        <h1>Konto użytkownika</h1>
        <Link to="/">
          <AppButton variant="secondary">Wydarzenia</AppButton>
        </Link>
      </section>

      {isLoading ? <LoadingState /> : null}
      {error ? <div className="notice notice-error">{error}</div> : null}

      {account ? (
        <section className="panel">
          <h2>Dane odbiorcy</h2>
          <AccountStatusBadge status={account.status} />
          <p>E-mail: {account.email}</p>
          <p>Status: {accountStatusLabels[account.status]}</p>
        </section>
      ) : null}

      <section className="panel">
        <h2>Sesja użytkownika</h2>
        <p>Status: {session ? sessionStatusLabels[session.status] : 'Brak'}</p>
        <p>Wygasa: {session ? new Date(session.expiresAt).toLocaleString('pl-PL') : 'Brak'}</p>
        {message ? <p className="form-success">{message}</p> : null}
        <div className="button-column">
          <AppButton onClick={handleRefresh}>Odśwież sesję</AppButton>
          <AppButton onClick={handleLogout} variant="secondary">
            Wyloguj
          </AppButton>
        </div>
      </section>

      <section className="panel">
        <h2>Zarządzanie kontem</h2>
        <div className="button-column">
          <AppButton onClick={() => setChangePasswordOpen(true)} variant="secondary">
            Zmień hasło
          </AppButton>
          <AppButton onClick={() => setDeleteAccountOpen(true)} variant="danger">
            Usuń konto
          </AppButton>
        </div>
      </section>

      <ChangePasswordModal
        error={passwordError}
        onCancel={() => {
          setPasswordError('');
          setChangePasswordOpen(false);
        }}
        onSubmit={handleChangePassword}
        open={changePasswordOpen}
      />
      <DeleteAccountModal
        error={deleteError}
        isSubmitting={isDeleteSubmitting}
        onCancel={() => {
          setDeleteError('');
          setDeleteAccountOpen(false);
        }}
        onConfirm={handleDeleteAccount}
        open={deleteAccountOpen}
      />
    </main>
  );
}
