import { LoginResponseDto, UserDto } from '../api/backendTypes';
import { UserAccount, UserSession } from '../types/account';

export const mapUserDto = (dto: UserDto): UserAccount => ({
  id: dto.id,
  email: dto.email,
  status: dto.status,
  registeredAt: dto.registeredAt,
  activatedAt: dto.activatedAt,
});

export const mapLoginResponse = (dto: LoginResponseDto): UserSession => ({
  accountId: dto.accountId,
  accessToken: dto.accessToken,
  expiresAt: dto.expiresAt,
  status: 'ACTIVE',
});
