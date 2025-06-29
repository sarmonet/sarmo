import { code, register, resendCode } from '@/services/auth'
import { isValidPhoneNumber } from 'libphonenumber-js'
import { useTranslation } from 'next-i18next'
import Link from 'next/link'
import { useState } from 'react'
import toast from 'react-hot-toast'
import PhoneInput from 'react-phone-number-input'
import 'react-phone-number-input/style.css'
import { useCatalog } from '../Catalog/CatalogContext/CatalogContext'
import { CodeInput } from '../ui/CodeInput'
import {
	LoginFooter,
	LoginHat,
	LoginMain,
	LoginWrapper,
} from './Registration.styled'
export const Registration = () => {
	const [contactType, setContactType] = useState<'email' | 'phone'>('phone')
	const [email, setEmail] = useState('')
	const [phone, setPhone] = useState('')
	const [, setIsContactValid] = useState(false)
	const { t } = useTranslation('common')
	const [password, setPassword] = useState('')
	const [refCode, setRefCode] = useState('')
	const [verifCode, setVerifCode] = useState<number | undefined>()
	const [firstName, setFirstName] = useState('')
	const [lastName, setLastName] = useState('')
	const { verificationId, setVerificationId } = useCatalog()
	const [error, setError] = useState<string | null>(null)
	const [pending, setPending] = useState(false)

	const validatePassword = (value: string): string | null => {
		return value.length < 8 ? t('registration.passwordLengthError') : null
	}

	const handleFormSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
		event.preventDefault()
		setError(null)

		const contactValue = contactType === 'phone' ? phone : email

		if (
			!contactValue ||
			(contactType === 'phone' && !isValidPhoneNumber(phone))
		) {
			const msg = t('registration.invalidContactError')
			setError(msg)
			toast.error(msg)
			return
		}

		const validationError = validatePassword(password)
		if (validationError) {
			setError(validationError)
			toast.error(validationError)
			return
		}

		try {
			const data = await register(contactValue, password, firstName, lastName)
			if (data) {
				setVerificationId(data)
				setPending(true)
			} else {
				const errMsg = t('registration.registrationErrorMissingId')
				setError(errMsg)
				toast.error(errMsg)
			}
		} catch (error) {
			const errMsg = t('registration.registrationErrorExistingAccount')
			setError(errMsg)
			console.error('Error fetching users:', error)
			toast.error(errMsg)
		} finally {
			setPending(false)
		}
	}

	const handleCodeSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
		event.preventDefault()

		if (!verificationId || !verifCode) return

		try {
			setPending(true)
			const data = await code(verificationId, verifCode)
			if (!data.accessToken || !data.refreshToken) return

			localStorage.setItem('accessToken', data.accessToken)
			localStorage.setItem('refreshToken', data.refreshToken)

			window.location.href = '/profile'
		} catch (error) {
			console.error('❌ Ошибка при верификации', error)
			toast.error(t('registration.verificationError'))
		} finally {
			setPending(false)
		}
	}
	const handleResendCode = async () => {
		if (!verificationId) return

		try {
			setPending(true)
			const data = await resendCode(verificationId)

			setVerificationId(data.newVerificationId)
			if (data) {
				toast.success(t('registration.resendCodeSuccess'))
			} else {
				toast.error(t('registration.resendCodeError'))
			}
		} catch (error) {
			console.error('❌ Ошибка при повторной отправке кода', error)
		} finally {
			setPending(false)
		}
	}

	return (
		<LoginWrapper>
			{!verificationId ? (
				<form onSubmit={handleFormSubmit}>
					<LoginHat>
						<h3>{t('registration.signUpTitle')}</h3>
					</LoginHat>
					<LoginMain>
						<div style={{ display: 'flex', gap: 12, marginBottom: 12 }}>
							<button
								type='button'
								onClick={() => setContactType('phone')}
								className={contactType === 'phone' ? 'active-tab' : ''}
							>
								{t('registration.phoneTab')}
							</button>
							<button
								type='button'
								onClick={() => setContactType('email')}
								className={contactType === 'email' ? 'active-tab' : ''}
							>
								{t('registration.emailTab')}
							</button>
						</div>

						{contactType === 'phone' ? (
							<PhoneInput
								international
								defaultCountry='UZ'
								value={phone}
								onChange={val => {
									setPhone(val || '')
									setIsContactValid(!!val && isValidPhoneNumber(val))
								}}
								className='input'
							/>
						) : (
							<input
								type='email'
								className='input'
								placeholder='Email *'
								value={email}
								onChange={e => {
									setEmail(e.target.value)
									const isValid = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(
										e.target.value
									)
									setIsContactValid(isValid)
								}}
							/>
						)}

						<input
							type='password'
							className='input'
							placeholder={t('registration.passwordPlaceholder')}
							value={password}
							required
							onChange={e => setPassword(e.target.value)}
						/>

						<input
							type='text'
							className='input'
							placeholder={t('registration.firstNamePlaceholder')}
							value={firstName}
							required
							onChange={e => setFirstName(e.target.value)}
						/>

						<input
							type='text'
							className='input'
							placeholder={t('registration.lastNamePlaceholder')}
							value={lastName}
							required
							onChange={e => setLastName(e.target.value)}
						/>

						<input
							type='text'
							className='input'
							placeholder={t('registration.refCodePlaceholder')}
							value={refCode}
							onChange={e => setRefCode(e.target.value)}
						/>

						{error && (
							<p style={{ color: 'red', marginTop: '4px', fontSize: '14px' }}>
								{error}
							</p>
						)}

						<button type='submit' disabled={pending}>
							{pending
								? t('registration.registeringButton')
								: t('registration.registerButton')}
						</button>

						<LoginFooter>
							<Link href='/login'>
								{t('registration.hasAccountPrompt')}{' '}
								<span>{t('registration.loginLink')}</span>
							</Link>
						</LoginFooter>
					</LoginMain>
				</form>
			) : (
				<form onSubmit={handleCodeSubmit}>
					<LoginHat>
						<h3>
							{contactType === 'phone'
								? t('registration.enterCodeTitlePhone')
								: t('registration.enterCodeTitleEmail')}
						</h3>
					</LoginHat>
					<LoginMain>
						<CodeInput
							onSubmit={code => {
								setVerifCode(Number(code))
							}}
						/>
						<button type='submit' disabled={pending}>
							{pending
								? t('registration.verifyingButton')
								: t('registration.verifyButton')}
						</button>

						<LoginFooter className='flex flex-col gap-y-[5px]'>
							<span
								onClick={() => {
									handleResendCode()
								}}
							>
								{t('registration.resendCodePrompt')}
							</span>
							<Link href='/login'>
								{t('registration.hasAccountPrompt')}{' '}
								<span>{t('registration.loginLink')}</span>
							</Link>
						</LoginFooter>
					</LoginMain>
					{error && <p style={{ color: 'red' }}>{error}</p>}
				</form>
			)}
		</LoginWrapper>
	)
}
