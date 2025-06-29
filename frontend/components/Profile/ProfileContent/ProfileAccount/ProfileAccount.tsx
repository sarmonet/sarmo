import { useCatalog } from '@/components/Catalog/CatalogContext/CatalogContext'
import { putUserInfo } from '@/services/getUsers'
import { colors } from '@/utils'
import { useTranslation } from 'next-i18next'
import { useState } from 'react'
import toast from 'react-hot-toast'
import {
	ButtonBlock,
	ProfileContentBlock,
	ProfileContentWrapper,
	ProfileHandleButton,
	ProfileTitle,
} from '../ProfileContent.styled'
export const ProfileAccount = () => {
	const { user } = useCatalog()
	const [edition, setEdition] = useState(false)
	const [name, setName] = useState(user?.firstName || '')
	const [email, setEmail] = useState(user?.email || '')
	const [phone, setPhone] = useState(user?.phoneNumber || '')
	const [lastName, setLastName] = useState(user?.lastName || '')
	const [country, setCountry] = useState(user?.country || '')
	const [city, setCity] = useState(user?.city || '')
	const [birthDate, setBirthDate] = useState(user?.birthDate || '')
	const [fullAddress, setFullAddress] = useState(user?.fullAddress || '')
	const { t } = useTranslation('common')
	const handleEdition = (e: React.FormEvent) => {
		e.preventDefault()
		setEdition(true)
	}
	const handleSubmit = (e: React.FormEvent) => {
		e.preventDefault()
		const userData = {
			email: email,
			firstName: name,
			lastName: lastName,
			phoneNumber: phone,
			country: country,
			city: city,
			birthDate: birthDate,
			fullAddress: fullAddress,
		}
		putUserInfo(userData)
			.then(() => {
				toast.success('✅ Данные успешно обновлены!')
			})
			.catch(error => {
				toast.success('❌ Ошибка при обновлении данных:', error)
			})
		setEdition(false)
	}
	const handleCancel = (e: React.FormEvent) => {
		e.preventDefault()
		setEdition(false)
		setName(user?.firstName || '')
		setEmail(user?.email || '')
		setLastName(user?.lastName || '')
		setCountry(user?.country || '')
		setCity(user?.city || '')
		setBirthDate(user?.birthDate || '')
		setFullAddress(user?.fullAddress || '')
	}
	return (
		<ProfileContentWrapper>
			<ProfileTitle bgc='#CABDFF'>
				{t('profileAccount.profileAccount')}
			</ProfileTitle>
			<form onSubmit={handleSubmit}>
				<ProfileContentBlock>
					<div>
						<label htmlFor='name'>{t('profileAccount.firstName')}</label>
						<input
							type='text'
							value={name}
							placeholder={t('profileAccount.firstName')}
							onChange={e => setName(e.target.value)}
							readOnly={!edition}
						/>
					</div>

					<div>
						<label htmlFor='lastName'>{t('profileAccount.lastName')}</label>
						<input
							type='text'
							placeholder={t('profileAccount.lastName')}
							value={lastName}
							onChange={e => setLastName(e.target.value)}
							readOnly={!edition}
						/>
					</div>
				</ProfileContentBlock>
				<ProfileContentBlock>
					<div>
						<label htmlFor='email'>{t('profileAccount.profileEmail')}</label>
						<input
							type='text'
							value={email}
							onChange={e => setEmail(e.target.value)}
							readOnly={!edition}
						/>
					</div>
					<div>
						<label htmlFor='phone'>{t('profileAccount.profilePhone')}</label>
						<input
							type='text'
							value={phone}
							placeholder='Номер телефона'
							onChange={e => setPhone(e.target.value)}
							readOnly={!edition}
						/>
					</div>
				</ProfileContentBlock>
				<ProfileContentBlock>
					<div>
						<label htmlFor='country'>
							{t('profileAccount.profileCountry')}
						</label>
						<input
							type='text'
							value={country}
							placeholder='Страна'
							onChange={e => setCountry(e.target.value)}
							readOnly={!edition}
						/>
					</div>
					<div>
						<label htmlFor='city'>{t('profileAccount.profileCity')}</label>
						<input
							type='text'
							value={city}
							placeholder='Город'
							onChange={e => setCity(e.target.value)}
							readOnly={!edition}
						/>
					</div>
				</ProfileContentBlock>
				<ProfileContentBlock>
					<div>
						<label htmlFor='address'>
							{t('profileAccount.profileFullAddress')}
						</label>
						<input
							type='text'
							value={fullAddress}
							placeholder='Полный адресс'
							onChange={e => setFullAddress(e.target.value)}
							readOnly={!edition}
						/>
					</div>
				</ProfileContentBlock>

				{edition ? (
					<ButtonBlock>
						<ProfileHandleButton
							color={`${colors.mainWhiteTextColor}`}
							type='submit'
						>
							{t('buttons.saveChangesBtn')}
						</ProfileHandleButton>
						<ProfileHandleButton
							onClick={handleCancel}
							bgc={`${colors.btnSecondColor}`}
							color={`${colors.mainWhiteTextColor}`}
							type='button'
						>
							{t('buttons.cancelChangesBtn')}
						</ProfileHandleButton>
					</ButtonBlock>
				) : (
					<ButtonBlock>
						<ProfileHandleButton
							color={`${colors.mainWhiteTextColor}`}
							type='button'
							onClick={handleEdition}
						>
							{t('buttons.changeBtn')}
						</ProfileHandleButton>
					</ButtonBlock>
				)}
			</form>
		</ProfileContentWrapper>
	)
}
