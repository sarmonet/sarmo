'use client'

import { googleLogin, login } from '@/services/auth'
import { generateCodeChallenge, generateCodeVerifier } from '@/utils/authUtils'
import { useTranslation } from 'next-i18next'
import Link from 'next/link'
import { useRouter, useSearchParams } from 'next/navigation'
import { useCallback, useEffect, useState } from 'react'
import { FaGoogle } from 'react-icons/fa'
import {
	LoginFooter,
	LoginHat,
	LoginMain,
	LoginMainButton,
	LoginSocial,
	LoginSocialButton,
	LoginWrapper,
} from './Login.styled'

export const Login = () => {
	const [username, setUsername] = useState('')
	const [password, setPassword] = useState('')
	const [loading, setLoading] = useState(false)
	const [error, setError] = useState<string | null>(null)
	const router = useRouter()
	const searchParams = useSearchParams()
	const { t } = useTranslation('common')

	useEffect(() => {
		const verifier = generateCodeVerifier()
		localStorage.setItem('google_code_verifier', verifier)
	}, [])

	const initiateGoogleAuth = async () => {
		const codeVerifier = localStorage.getItem('google_code_verifier')
		if (!codeVerifier) {
			console.error('code_verifier not found in localStorage!')
			setError(t('login.authenticationError'))
			return
		}
		const codeChallenge = await generateCodeChallenge(codeVerifier)

		const authorizationUrl =
			`https://accounts.google.com/o/oauth2/v2/auth?` +
			`client_id=${process.env.NEXT_PUBLIC_GOOGLE_CLIENT_ID}&` +
			`redirect_uri=${encodeURIComponent(
				window.location.origin + '/login/oauth2/code/google'
			)}&` +
			`response_type=code&` +
			`scope=profile email&` +
			`code_challenge=${codeChallenge}&` +
			`code_challenge_method=S256`

		window.location.href = authorizationUrl
	}

	const handleGoogleCode = useCallback(
		async (code: string) => {
			try {
				setLoading(true)
				setError(null)

				const codeVerifier = localStorage.getItem('google_code_verifier')
				if (!codeVerifier) {
					console.error('code_verifier not found in localStorage!')
					setError(t('login.authenticationError'))
					setLoading(false)
					return
				}

				const response = await googleLogin(code, codeVerifier, 'google')

				localStorage.setItem('accessToken', response.accessToken)
				localStorage.setItem('refreshToken', response.refreshToken)
				// router.push('/profile');
				window.location.href = '/profile'
			} catch (err) {
				console.error('Error logging in with Google:', err)
				setError(t('login.googleLoginError'))
			} finally {
				setLoading(false)
			}
		},
		[t]
	)
	useEffect(() => {
		const code = searchParams?.get('code')
		if (code) {
			handleGoogleCode(code)
		} else if (searchParams?.get('error')) {
			console.error('Error from Google:', searchParams.get('error'))
			setError(t('login.googleAuthError'))
			setLoading(false)
		}
	}, [searchParams, router, handleGoogleCode, t])

	const handleLogin = async (e: React.FormEvent) => {
		e.preventDefault()

		if (!username || !password) {
			setError(t('login.fillAllFieldsError'))
			return
		}

		setLoading(true)
		setError(null)

		try {
			const response = await login(username, password)
			localStorage.setItem('accessToken', response.accessToken)
			localStorage.setItem('refreshToken', response.refreshToken)
			window.location.href = '/profile'
		} catch (err) {
			console.error('Login error:', err)
			setError(t('login.invalidCredentialsError'))
		} finally {
			setLoading(false)
		}
	}

	return (
		<LoginWrapper>
			<form onSubmit={handleLogin}>
				<LoginHat>
					<h3>{t('login.loginTitle')}</h3>
				</LoginHat>
				<LoginMain>
					<input
						type='text'
						placeholder={t('login.usernamePlaceholder')}
						value={username}
						onChange={e => setUsername(e.target.value)}
					/>
					<input
						type='password'
						placeholder={t('login.passwordPlaceholder')}
						value={password}
						onChange={e => setPassword(e.target.value)}
					/>
					<LoginMainButton type='submit' disabled={loading}>
						{loading ? t('login.loggingInButton') : t('login.loginButton')}
					</LoginMainButton>
					<LoginSocial>
						<LoginSocialButton
							type='button'
							onClick={initiateGoogleAuth}
							disabled={loading}
						>
							{loading ? '...' : <FaGoogle size={24} />}
						</LoginSocialButton>
					</LoginSocial>
					{error && <p style={{ color: 'red' }}>{error}</p>}
				</LoginMain>
				<LoginFooter style={{ marginTop: '20px' }}>
					<Link href='/registration'>
						{t('login.noAccountPrompt')} <span>{t('login.registerLink')}</span>
					</Link>
				</LoginFooter>
			</form>
		</LoginWrapper>
	)
}
