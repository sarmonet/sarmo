import { isValidPhoneNumber } from 'libphonenumber-js'
import { useTranslation } from 'next-i18next'
import { useEffect, useState } from 'react'
import PhoneInput from 'react-phone-number-input'
import 'react-phone-number-input/style.css'
interface Props {
	value: string
	onChange: (value: string) => void
	onValidityChange: (isValid: boolean, type: 'phone' | null) => void
}

export const PhoneInputWithValidation = ({
	value,
	onChange,
	onValidityChange,
}: Props) => {
	const [error, setError] = useState<string | null>(null)
	const { t } = useTranslation('common')
	useEffect(() => {
		if (!value) {
			setError(null)
			onValidityChange(false, null)
			return
		}

		const isValid = isValidPhoneNumber(value)
		setError(isValid ? null : t('registration.invalidPhoneNumber'))
		onValidityChange(isValid, isValid ? 'phone' : null)
	}, [value])

	return (
		<div style={{ display: 'flex', flexDirection: 'column', gap: 4 }}>
			<PhoneInput
				international
				defaultCountry='RU'
				placeholder={t('registration.phoneInputPlaceholder')}
				value={value}
				onChange={val => onChange(val || '')}
			/>
			{error && (
				<small style={{ color: 'red', fontSize: '14px' }}>{error}</small>
			)}
		</div>
	)
}
