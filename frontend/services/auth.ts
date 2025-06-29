import { axiosInstanceLogin } from '@/utils/axiosInstance'

export const login = async (
	contact: string | number,
	password: string | number
) => {
	try {
		const { data } = await axiosInstanceLogin.post(
			'login',
			{
				contact,
				password,
			},
			{ headers: { 'Content-Type': 'application/json' } }
		)

		return data
	} catch (error) {
		console.error('❌ Ошибка при регистрации', error)
		throw error
	}
}
export const googleLogin = async (
	code: string,
	codeVerifier: string,
	provider: string
) => {
	try {
		const { data } = await axiosInstanceLogin.post(
			`oauth2/login?code=${code}&codeVerifier=${codeVerifier}&provider=${provider}`,
			{},
			{ headers: { 'Content-Type': 'application/json' } }
		)
		return data
	} catch (error) {
		console.error('❌ Ошибка при входе через Google', error)
		throw error
	}
}

export const register = async (
	contact: string | number,
	password: string | number,
	firstName: string | number,
	lastName: string | number
) => {
	try {
		const { data } = await axiosInstanceLogin.post(
			'register',
			{
				contact,
				password,
				firstName,
				lastName,
			},
			{ headers: { 'Content-Type': 'application/json' } }
		)

		return data
	} catch (error) {
		console.error('❌ Ошибка при регистрации', error)
		console.log(error)
		throw error
	}
}

export const code = async (verificationId: string, verifCode: number) => {
	try {
		const { data } = await axiosInstanceLogin.post(
			`/two-factor/verify/${verificationId}?code=${verifCode}`,
			{},
			{ headers: { 'Content-Type': 'application/json' } }
		)

		return data
	} catch (error) {
		console.error('❌ Ошибка при верификации', error)
		throw error
	}
}
export const resendCode = async (verificationId: string) => {
	try {
		const { data } = await axiosInstanceLogin.post(
			`/two-factor/resend/${verificationId}`,
			{},
			{ headers: { 'Content-Type': 'application/json' } }
		)

		return data
	} catch (error) {
		console.error('❌ Ошибка при верификации', error)
		throw error
	}
}
