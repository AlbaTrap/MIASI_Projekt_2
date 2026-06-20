import { FormEvent, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';

import { accountApi } from '../../api/accountApi';
import { AppButton } from '../../components/ui/AppButton';
import { AppTextInput } from '../../components/ui/AppTextInput';

export function RegisterPage() {
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [verificationToken, setVerificationToken] = useState('');
  const [error, setError] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  const submit = async (event: FormEvent) => {
    event.preventDefault();
    setIsSubmitting(true);
    setError('');
    setVerificationToken('');

    try {
      const result = await accountApi.register({ email, password });
      setVerificationToken(result.verificationTokenForDemo);
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Nie udało się zarejestrować konta.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <main className="auth-page">
      <form className="auth-panel" onSubmit={submit}>
        <h1>Zarejestruj konto</h1>
        <p>
          Podaj adres e-mail i hasło. Konto zostanie utworzone jako oczekujące na
          potwierdzenie adresu e-mail.
        </p>
        <AppTextInput
          autoComplete="email"
          label="Adres e-mail"
          onChange={(event) => setEmail(event.target.value)}
          required
          type="email"
          value={email}
        />
        <AppTextInput
          autoComplete="new-password"
          label="Hasło"
          onChange={(event) => setPassword(event.target.value)}
          required
          type="password"
          value={password}
        />
        <p className="hint">Hasło musi mieć co najmniej 8 znaków oraz zawierać literę i cyfrę.</p>
        {error ? <p className="form-error">{error}</p> : null}

        {verificationToken ? (
          <div className="success-box">
            <strong>Konto oczekuje na potwierdzenie.</strong>
            <code>{verificationToken}</code>
            <AppButton
              onClick={() => navigate(`/confirm-email?token=${encodeURIComponent(verificationToken)}`)}
            >
              Przejdź do potwierdzenia e-maila
            </AppButton>
          </div>
        ) : null}

        <div className="button-row">
          <AppButton disabled={isSubmitting} type="submit">
            Zarejestruj
          </AppButton>
          <Link to="/login">
            <AppButton disabled={isSubmitting} variant="secondary">
              Mam już konto
            </AppButton>
          </Link>
        </div>
      </form>
    </main>
  );
}
