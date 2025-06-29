import { useTranslation } from 'next-i18next'
import { useEffect, useState } from 'react'
interface Props {
	value: string
	onChange: (value: string) => void
	onValidityChange: (isValid: boolean, type: 'email' | null) => void
}

export const EmailInput = ({ value, onChange, onValidityChange }: Props) => {
	const [error, setError] = useState<string | null>(null)
	const { t } = useTranslation('common')
	useEffect(() => {
		const trimmed = value.trim()

		if (!trimmed) {
			setError(null)
			onValidityChange(false, null)
			return
		}

		const isValid = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(trimmed)
		setError(isValid ? null : t('registration.invalidEmail'))
		onValidityChange(isValid, isValid ? 'email' : null)
	}, [value])

	return (
		<div style={{ display: 'flex', flexDirection: 'column', gap: 4 }}>
			<input
				type='text'
				className='input'
				placeholder={t('registration.emailTab')}
				value={value}
				onChange={e => onChange(e.target.value)}
			/>
			{error && (
				<small style={{ color: 'red', fontSize: '14px' }}>{error}</small>
			)}
		</div>
	)
}
