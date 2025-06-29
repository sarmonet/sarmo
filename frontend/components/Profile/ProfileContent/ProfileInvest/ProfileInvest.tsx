import {
	BudgetRange,
	BudgetRangeLabels,
	BusinessSector,
	BusinessSectorLabels,
	getInvestInfo,
	IInvestorProfile,
	InvestmentCategory,
	InvestmentCategoryLabels,
	InvestmentGoal,
	InvestmentGoalLabels,
	postInvestInfo,
	TimeCommitment,
	TimeCommitmentLabels,
} from '@/services/getUsers'
import { colors } from '@/utils'
import { AxiosError } from 'axios'
import { useTranslation } from 'next-i18next'
import React, { useEffect, useState } from 'react'
import {
	ButtonBlock,
	ProfileContentWrapper,
	ProfileHandleButton,
	ProfileTitle,
} from '../ProfileContent.styled'

import { ProfileInvestBlock } from './ProfileInvest.styled'
const initialFormData: IInvestorProfile = {
	investmentGoals: [],
	businessExperience: false,
	experiencePeriod: '',
	experienceSphere: '',
	budget: BudgetRange.UNDER_50K,
	preferredInvestmentCategories: [],
	preferredBusinessSectors: [],
	otherSector: '',
	interestsAndHobbies: '',
	timeCommitment: TimeCommitment.PART_TIME,
	aboutMe: '',
}

export const ProfileInvest: React.FC = () => {
	const [loading, setLoading] = useState(true)
	const [fetchError, setFetchError] = useState<string | null>(null)
	const [edition, setEdition] = useState(false)
	const [isSaving, setIsSaving] = useState(false)
	const [saveError, setSaveError] = useState<string | null>(null)
	const { t } = useTranslation('common')
	const [formData, setFormData] = useState<IInvestorProfile>(initialFormData)
	const [originalData, setOriginalData] =
		useState<IInvestorProfile>(initialFormData)

	useEffect(() => {
		;(async () => {
			setLoading(true)
			try {
				const data = await getInvestInfo()
				setFormData(data ?? initialFormData)
				setOriginalData(data ?? initialFormData)
				setFetchError(null)
			} catch (err) {
				const e = err as AxiosError
				if (e.response?.status === 404) {
					setFetchError(null)
				} else {
					setFetchError(e.message)
				}
			} finally {
				setLoading(false)
			}
		})()
	}, [])
	// eslint-disable-next-line @typescript-eslint/no-explicit-any
	const toggleArray = <K extends keyof IInvestorProfile>(
		field: K,
		// eslint-disable-next-line @typescript-eslint/no-explicit-any
		value: any
	) =>
		setFormData(f => {
			// eslint-disable-next-line @typescript-eslint/no-explicit-any
			const arr = f[field] as any[]
			return {
				...f,
				[field]: arr.includes(value)
					? arr.filter(x => x !== value)
					: [...arr, value],
			}
		})
	// eslint-disable-next-line @typescript-eslint/no-explicit-any
	const setEnum = <K extends keyof IInvestorProfile>(field: K, value: any) =>
		setFormData(f => ({ ...f, [field]: value }))

	const setText = <K extends keyof IInvestorProfile>(field: K, value: string) =>
		setFormData(f => ({ ...f, [field]: value }))

	const handleEdit = () => {
		setSaveError(null)
		setEdition(true)
	}

	const handleCancel = () => {
		setFormData(originalData)
		setSaveError(null)
		setEdition(false)
	}

	const handleSubmit = async (e: React.FormEvent) => {
		e.preventDefault()
		setIsSaving(true)
		setSaveError(null)
		try {
			const updated = await postInvestInfo(formData)
			setFormData(updated)
			setOriginalData(updated)
			setEdition(false)
		} catch (err) {
			setSaveError((err as AxiosError).message)
		} finally {
			setIsSaving(false)
		}
	}

	const readOnly = !edition || isSaving

	if (loading) return <p>Загрузка профиля…</p>

	return (
		<ProfileContentWrapper>
			<div
				style={{
					display: 'flex',
					alignItems: 'center',
					justifyContent: 'space-between',
				}}
			>
				<ProfileTitle bgc='#CABDFF'>
					{t('profileInvest.profileInvest')}
				</ProfileTitle>
			</div>

			{fetchError && <p style={{ color: 'red' }}>{fetchError}</p>}

			<form
				onSubmit={handleSubmit}
				style={{ display: 'flex', flexDirection: 'column', rowGap: '40px' }}
			>
				<ButtonBlock>
					{!edition && (
						<ProfileHandleButton
							onClick={() => setEdition(true)}
							color={colors.mainWhiteTextColor}
						>
							{t('buttons.changeBtn')}
						</ProfileHandleButton>
					)}
					{edition && (
						<>
							<ProfileHandleButton
								type='submit'
								disabled={isSaving}
								onClick={() => handleEdit()}
								color={colors.mainWhiteTextColor}
							>
								{t('buttons.saveBtn')}
							</ProfileHandleButton>
							<ProfileHandleButton
								type='button'
								bgc={colors.btnSecondColor}
								color={colors.mainWhiteTextColor}
								disabled={isSaving}
								onClick={handleCancel}
							>
								{t('buttons.cancelBtn')}
							</ProfileHandleButton>
						</>
					)}
				</ButtonBlock>
				{/* 2. Цели инвестирования */}
				<ProfileInvestBlock>
					<h4>1. {t('profileInvest.investLabel1')}</h4>
					<p>{t('profileInvest.investDescribe1')}</p>
					{Object.values(InvestmentGoal).map(g => (
						<label key={g}>
							<input
								type='checkbox'
								disabled={readOnly}
								checked={formData.investmentGoals.includes(g)}
								onChange={() => toggleArray('investmentGoals', g)}
							/>{' '}
							<span>{InvestmentGoalLabels[g]}</span>
						</label>
					))}
				</ProfileInvestBlock>

				<ProfileInvestBlock>
					<h4>2. {t('profileInvest.investLabel2')}</h4>
					<p>{t('profileInvest.investDescribe2')}</p>
					<label>
						<input
							type='checkbox'
							disabled={readOnly}
							checked={formData.businessExperience}
							onChange={e =>
								// eslint-disable-next-line @typescript-eslint/no-explicit-any
								setText('businessExperience', e.target.checked as any)
							}
						/>
						<span>{t('profileInvest.investCheckbox2')}</span>
					</label>
					{formData.businessExperience && (
						<div>
							<input
								type='text'
								className='input'
								placeholder='Период'
								disabled={readOnly}
								value={formData.experiencePeriod}
								onChange={e => setText('experiencePeriod', e.target.value)}
							/>
							<input
								type='text'
								className='input'
								placeholder='Сфера'
								disabled={readOnly}
								value={formData.experienceSphere}
								onChange={e => setText('experienceSphere', e.target.value)}
							/>
						</div>
					)}
				</ProfileInvestBlock>

				{/* 4. Бюджет */}
				<ProfileInvestBlock>
					<h4>3. {t('profileInvest.investLabel3')}</h4>
					<p>{t('profileInvest.investDescribe3')}</p>
					{Object.values(BudgetRange).map(b => (
						<label key={b}>
							<input
								type='radio'
								name='budget'
								disabled={readOnly}
								checked={formData.budget === b}
								onChange={() => setEnum('budget', b)}
							/>{' '}
							<span>{BudgetRangeLabels[b]}</span>
						</label>
					))}
				</ProfileInvestBlock>

				{/* 5. Категории инвестиций */}
				<ProfileInvestBlock>
					<h4>4. {t('profileInvest.investLabel4')}</h4>
					{Object.values(InvestmentCategory).map(c => (
						<label key={c}>
							<input
								type='checkbox'
								disabled={readOnly}
								checked={formData.preferredInvestmentCategories.includes(c)}
								onChange={() => toggleArray('preferredInvestmentCategories', c)}
							/>{' '}
							<span>{InvestmentCategoryLabels[c]}</span>
						</label>
					))}
				</ProfileInvestBlock>

				{/* 6. Сектора бизнеса */}
				<ProfileInvestBlock>
					<h4>5. {t('profileInvest.investLabel5')}</h4>
					<p>{t('profileInvest.investDescribe5')}</p>
					{Object.values(BusinessSector).map(s => (
						<label key={s}>
							<input
								type='checkbox'
								disabled={readOnly}
								checked={formData.preferredBusinessSectors.includes(s)}
								onChange={() => toggleArray('preferredBusinessSectors', s)}
							/>{' '}
							<span>{BusinessSectorLabels[s]}</span>
						</label>
					))}
					{formData.preferredBusinessSectors.includes(BusinessSector.OTHER) && (
						<input
							type='text'
							className='input'
							placeholder='Другой сектор'
							disabled={readOnly}
							value={formData.otherSector}
							onChange={e => setText('otherSector', e.target.value)}
						/>
					)}
				</ProfileInvestBlock>

				{/* 7. Интересы и хобби */}
				<ProfileInvestBlock>
					<h4>6. {t('profileInvest.investLabel6')}</h4>
					<p>{t('profileInvest.investDescribe6')}</p>
					<textarea
						rows={3}
						className='textarea'
						disabled={readOnly}
						value={formData.interestsAndHobbies}
						onChange={e => setText('interestsAndHobbies', e.target.value)}
					/>
				</ProfileInvestBlock>

				{/* 8. Время участия */}
				<ProfileInvestBlock>
					<h4>7. {t('profileInvest.investLabel7')}</h4>
					<p>{t('profileInvest.investDescribe7')}</p>
					{Object.values(TimeCommitment).map(t => (
						<label key={t}>
							<input
								type='radio'
								name='timeCommitment'
								disabled={readOnly}
								checked={formData.timeCommitment === t}
								onChange={() => setEnum('timeCommitment', t)}
							/>{' '}
							<span>{TimeCommitmentLabels[t]}</span>
						</label>
					))}
				</ProfileInvestBlock>

				{/* 9. Обо мне */}
				<ProfileInvestBlock>
					<h4>8. {t('profileInvest.investLabel8')}</h4>
					<textarea
						rows={4}
						className='textarea'
						disabled={readOnly}
						value={formData.aboutMe}
						onChange={e => setText('aboutMe', e.target.value)}
					/>
				</ProfileInvestBlock>

				{/* Кнопки управления */}

				{saveError && <p style={{ color: 'red' }}>{saveError}</p>}
			</form>
		</ProfileContentWrapper>
	)
}
