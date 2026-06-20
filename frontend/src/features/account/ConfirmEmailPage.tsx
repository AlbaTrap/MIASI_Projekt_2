import { FormEvent, useMemo, useState } from 'react';
import { Link, useSearchParams } from 'react-router-dom';

import { accountApi } from '../../api/accountApi';
import { AppButton } from '../../components/ui/AppButton';
import { AppTextInput } from '../../components/ui/AppTextInput';

export function ConfirmEmailPage() {
  const [searchParams] = useSearchParams();
  const initialToken = useMemo(() => searchParams.get('token') ?? '', [searchParams]);
  const [token, setToken] = useState(initialToken);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  const submit = async (event: FormEvent) => {
    event.preventDefault();
    setIsSubmitting(true);
    setError('');
    setMessage('');

    try {
      await accountApi.confirmEmail(token);
      setMessage('Adres e-mail został potwierdzony. Konto jest aktywne.');
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Nie udało się potwierdzić e-maila.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <main className="auth-page">
      <form className="auth-panel" onSubmit={submit}>
        <h1>Potwierdź e-mail</h1>
        <p>Wpisz token weryfikacyjny wygenerowany po rejestracji konta.</p>
        <AppTextInput
          label="Token weryfikacyjny"
          onChange={(event) => setToken(event.target.value)}
          required
          value={token}
        />
        {error ? <p className="form-error">{error}</p> : null}
        {message ? <p className="form-success">{message}</p> : null}
        <div className="button-row">
          <AppButton disabled={isSubmitting} type="submit">
            Potwierdź e-mail
          </AppButton>
          <Link to="/login">
            <AppButton variant="secondary">Przejdź do logowania</AppButton>
          </Link>
        </div>
      </form>
    </main>
  );
}
