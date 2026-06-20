import { ButtonHTMLAttributes } from 'react';

type AppButtonProps = ButtonHTMLAttributes<HTMLButtonElement> & {
  variant?: 'primary' | 'secondary' | 'danger';
};

export function AppButton({
  className = '',
  type = 'button',
  variant = 'primary',
  ...props
}: AppButtonProps) {
  return (
    <button
      className={`app-button app-button-${variant} ${className}`.trim()}
      type={type}
      {...props}
    />
  );
}
