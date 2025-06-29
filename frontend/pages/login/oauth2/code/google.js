// pages/login/oauth2/code/google.js
import { googleLogin } from '@/services/auth'
import Image from "next/legacy/image"
import { withRouter } from 'next/router'
import React, { useCallback, useEffect } from 'react'

const GoogleCallbackPage = withRouter(({ router }) => {
  const handleGoogleCode = useCallback(async (code) => {
    const codeVerifier = localStorage.getItem('google_code_verifier');
    if (!codeVerifier) {
      console.error('code_verifier не найден в localStorage!');
      router.push('/login?error=code_verifier_missing');
      return;
    }

    try {
      const response = await googleLogin(code, codeVerifier, 'google');
      localStorage.setItem('accessToken', response.accessToken);
      localStorage.setItem('refreshToken', response.refreshToken);
      // router.push('/profile');
      window.location.href = '/profile';
    } catch (error) {
      console.error('Ошибка отправки кода на бэкэнд:', error);
      router.push('/login?error=backend_auth_failed');
    }
  }, [router]);

  useEffect(() => {
    const code = router.query.code;
    const error = router.query.error;

    if (error) {
      console.error('Ошибка аутентификации Google:', error);
      router.push('/login?error=google_auth_failed');
      return;
    }

    if (code) {
      handleGoogleCode(code);
    }
  }, [router, handleGoogleCode]);

  return (
    <div style={{ height: '100dvh', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
      <Image src={'/images/gif/loading.gif'} alt='loading...' width={46} height={46} />
    </div>
  );
});

export default GoogleCallbackPage;