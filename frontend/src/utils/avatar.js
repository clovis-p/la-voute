import DefaultAvatar from '@/assets/test-images/default-pp.jpg';

export function avatarSrc(base64) {
  if (!base64) {
    return DefaultAvatar;
  }
  if (base64.startsWith('data:')) {
    return base64;
  }
  return `data:image/jpeg;base64,${base64}`;
}
