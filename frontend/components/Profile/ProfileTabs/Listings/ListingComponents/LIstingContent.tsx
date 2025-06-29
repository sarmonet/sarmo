import { ICatalogSub } from '@/components/Catalog/Catalog-data/CatalogInterface/Catalog.interface'
import { IFields } from '@/components/Catalog/Catalog-data/CatalogInterface/Filter.interface'
import { useCatalog } from '@/components/Catalog/CatalogContext/CatalogContext'
import { DropDown } from '@/components/DropDown/DropDown'
import { getSubCategories } from '@/services/getCategories'
import {
	getFieldsById,
	getInvestFieldsById,
	ICreateListing,
	postListings,
} from '@/services/getListings'
import {
	postDoc,
	postImage,
	postImages,
	postVideo,
} from '@/services/uploadFiles'
import { colors } from '@/utils'
import { AxiosError } from 'axios'
import { useTranslation } from 'next-i18next'
import { useCallback, useEffect, useState } from 'react'
import toast from 'react-hot-toast'
import Select from 'react-select'
import { ProfileTitle } from '../../../ProfileContent/ProfileContent.styled'
import {
	ListingButton,
	ListingField,
	ListingFields,
	ListingInvest,
	ListingLabel,
	ListingMain,
	ListingTitle,
	ListingTop,
	ListingWrapper,
} from '../Listings.styled'
import { CreateModal } from './CreateModal'

import { Checkbox } from '@mui/material'
import FormControlLabel from '@mui/material/FormControlLabel'
import FormGroup from '@mui/material/FormGroup'
import Switch from '@mui/material/Switch'

type InvestOptionValue = 'true' | 'false'

export const ListingCreate = () => {
	const {
		categories,
		setSubCategories,
		subCategories,
		activeCategory,
		setActiveCategory,
		setFields,
		fields,
		user,
	} = useCatalog()

	const [formData, setFormData] = useState({
		title: '',
		description: '',
		price: '',
		invest: false,
		country: '',
		city: '',
		fullAddress: '',
		mainImage: '',
		videoUrl: '',
		fields: {} as Record<string, string>,
		status: 'INACTIVE',
	})

	const [selectedInvestType, setSelectedInvestType] =
		useState<InvestOptionValue | null>(null)
	const [selectedFile, setSelectedFile] = useState<File | null>(null)
	const [selectedFiles, setSelectedFiles] = useState<File[]>([])
	const [tooltip, setTooltip] = useState<string | null>(null)
	const { t } = useTranslation('common')
	const [selectedVideo, setSelectedVideo] = useState<File | null>(null)
	const [selectedDynamicFiles, setSelectedDynamicFiles] = useState<
		Record<string, File | null>
	>({})

	const [error, setError] = useState('')
	const [isSubmitting, setIsSubmitting] = useState(false)
	const [selectedSubCategory, setSelectedSubCategory] =
		useState<ICatalogSub | null>(null)
	const [isCreated, setIsCreated] = useState(false)
	const [listingId, setListingId] = useState<number>(0)
	const countryOptions = [
		{ value: 'Узбекистан', label: t('options.countries.uzbekistan') },
		{ value: 'Казахстан', label: t('options.countries.kazakhstan') },
		{
			value: 'Киргизская Республика',
			label: t('options.countries.kyrgyzRepublic'),
		},
		{ value: 'Таджикистан', label: t('options.countries.tajikistan') },
		{ value: 'Туркменистан', label: t('options.countries.turkmenistan') },
		{ value: 'Азербайджан', label: t('options.countries.azerbaijan') },
		{ value: 'Грузия', label: t('options.countries.georgia') },
		{ value: 'Армения', label: t('options.countries.armenia') },
		{ value: 'Украина', label: t('options.countries.ukraine') },
		{ value: 'Турция', label: t('options.countries.turkey') },
		{ value: 'Россия', label: t('options.countries.russia') },
		{ value: 'Республика Беларусь', label: t('options.countries.belarus') },
		{ value: 'Китай', label: t('options.countries.china') },
	]
	const listFieldOptions: Record<string, string[]> = {
		[t('options.businessDevelopmentStage.title')]: [
			t('options.businessDevelopmentStage.idea'),
			t('options.businessDevelopmentStage.mvp'),
			t('options.businessDevelopmentStage.growthStage'),
			t('options.businessDevelopmentStage.profitableBusiness'),
		],
		[t('options.formOfParticipation.title')]: [
			t('options.formOfParticipation.companyShare'),
			t('options.formOfParticipation.loan'),
			t('options.formOfParticipation.mentorshipPlusMoney'),
		],
		[t('options.transactionType.title')]: [
			t('options.transactionType.ventureInvestments'),
			t('options.transactionType.partialFullBuyout'),
			t('options.transactionType.jointVenture'),
			t('options.transactionType.credit'),
		],
	}

	const handleFieldChange = (fieldName: string, value: string) => {
		setFormData(prev => ({
			...prev,
			fields: {
				...prev.fields,
				[fieldName]: value,
			},
		}))
	}

	const handleInvestChange = useCallback(
		(value: string) => {
			const investValue = value as InvestOptionValue

			setSelectedInvestType(investValue)
			setFormData(prev => ({
				...prev,
				invest: investValue === 'true',
				fields: {},
			}))
			setSelectedDynamicFiles({})
			setActiveCategory(null)
			setSubCategories([])
			setSelectedSubCategory(null)
			setFields(null)
			setError('')
		},
		[
			setActiveCategory,
			setSubCategories,
			setSelectedSubCategory,
			setFormData,
			setFields,
			setSelectedDynamicFiles,
		]
	)

	const fetchFieldsData = useCallback(
		async (
			currentFormDataFields: Record<string, string>
		): Promise<{
			fieldsData: IFields | null
			initialFormDataFields: Record<string, string>
		}> => {
			if (!activeCategory) {
				setFields(null)
				return { fieldsData: null, initialFormDataFields: {} }
			}
			try {
				let fieldsResponse: IFields | null = null
				if (selectedInvestType === 'true') {
					fieldsResponse = await getInvestFieldsById({ id: activeCategory.id })
					console.log(fieldsResponse)
				} else if (selectedInvestType === 'false') {
					fieldsResponse = await getFieldsById({ id: activeCategory.id })
					console.log(fieldsResponse)
				}

				let fieldsData: IFields | null = null
				if (fieldsResponse) {
					fieldsData = fieldsResponse as IFields
				}

				const initialFormDataFields: Record<string, string> = {}

				if (fieldsData?.fields) {
					fieldsData.fields.forEach(field => {
						if (field.type === 'Boolean' && field.required) {
							initialFormDataFields[field.name] =
								currentFormDataFields[field.name] !== undefined
									? currentFormDataFields[field.name]
									: 'false'
						} else if (
							field.type !== 'File' &&
							currentFormDataFields[field.name] !== undefined
						) {
							initialFormDataFields[field.name] =
								currentFormDataFields[field.name]
						}
					})
				}

				return { fieldsData, initialFormDataFields }
			} catch (error) {
				console.error('❌ Ошибка при получении данных полей:', error)
				setFields(null)
				return { fieldsData: null, initialFormDataFields: {} }
			}
		},
		[activeCategory, setFields]
	)

	useEffect(() => {
		setSelectedDynamicFiles({})

		fetchFieldsData(formData.fields).then(
			({ fieldsData, initialFormDataFields }) => {
				setFields(fieldsData)
				setFormData(prev => ({
					...prev,
					fields: { ...initialFormDataFields },
				}))
			}
		)
	}, [activeCategory, fetchFieldsData])

	const dropDownOptions =
		categories
			?.filter(category => {
				if (user?.roles?.name === 'USER') {
					return category.name !== 'Бизнес-планы'
				}
				return true
			})
			.map(category => ({
				value: category.id.toString(),
				label: category.name,
			})) || []

	const dropDownOptionsSub =
		subCategories?.map(sub => ({
			value: sub.id.toString(),
			label: sub.name,
		})) || []

	const handleCategoryChange = useCallback(
		async (categoryId: string) => {
			try {
				const category = categories?.find(
					cat => cat.id.toString() === categoryId
				)
				setSubCategories([])
				if (!category) {
					setActiveCategory(null)
					setSubCategories([])
					setSelectedSubCategory(null)
					setFields(null)
					setFormData(prev => ({ ...prev, fields: {} }))
					setSelectedDynamicFiles({})
					return
				}

				setActiveCategory(category)
				setFormData(prev => ({ ...prev, fields: {} }))
				setSelectedDynamicFiles({})

				const subCategoriesData = await getSubCategories({ id: category.id })
				setSubCategories(subCategoriesData)
				setSelectedSubCategory(null)
				setError('')
			} catch (error) {
				console.error('❌ Ошибка при получении подкатегорий:', error)
				setSubCategories([])
				setSelectedSubCategory(null)
				setFields(null)
				setFormData(prev => ({ ...prev, fields: {} }))
				setSelectedDynamicFiles({})
				setError('Не удалось загрузить подкатегории.')
			}
		},
		[
			categories,
			setActiveCategory,
			setSubCategories,
			setSelectedSubCategory,
			setFormData,
			setFields,
			setSelectedDynamicFiles,
		]
	)

	const handleSubCategoryChange = (subCategoryId: string) => {
		const subCategory = subCategories?.find(
			sub => sub.id.toString() === subCategoryId
		)
		setSelectedSubCategory(subCategory || null)
		setFormData(prev => ({ ...prev, fields: {} }))
		setSelectedDynamicFiles({})
		setError('')
	}

	const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
		if (event.target.files?.[0]) {
			setSelectedFile(event.target.files[0])
			setError('')
		}
	}

	const handleMultipleFileChange = (
		event: React.ChangeEvent<HTMLInputElement>
	) => {
		const files = event.target.files
		if (files) {
			setSelectedFiles(Array.from(files))
			setError('')
		}
	}

	const handleVideoChange = (event: React.ChangeEvent<HTMLInputElement>) => {
		if (event.target.files?.[0]) {
			setSelectedVideo(event.target.files[0])
			setError('')
		}
	}

	const handleDynamicFileChange = (
		fieldName: string,
		event: React.ChangeEvent<HTMLInputElement>
	) => {
		const file = event.target.files?.[0] || null
		setSelectedDynamicFiles(prev => ({
			...prev,
			[fieldName]: file,
		}))
		setError('')
	}

	const handleMultiSelectChange = (
		fieldName: string,
		option: string,
		checked: boolean
	) => {
		const currentRaw = formData.fields[fieldName]
		const currentValues = Array.isArray(currentRaw) ? currentRaw : []

		const updatedValues = checked
			? [...new Set([...currentValues, option])]
			: currentValues.filter((val: string) => val !== option)

		// eslint-disable-next-line @typescript-eslint/no-explicit-any
		setFormData((prev: any) => ({
			...prev,
			fields: {
				...prev.fields,
				[fieldName]: updatedValues,
			},
		}))
	}

	const handlePostListings = async () => {
		setError('')
		setIsSubmitting(true)

		if (selectedInvestType === null) {
			setError('Пожалуйста, выберите тип объявления.')
			setIsSubmitting(false)
			return
		}
		if (!activeCategory) {
			setError('Пожалуйста, выберите категорию.')
			setIsSubmitting(false)
			return
		}
		if (
			!formData.title ||
			!formData.description ||
			!formData.price ||
			!formData.country ||
			!formData.city ||
			!formData.fullAddress
		) {
			toast.error(
				'Пожалуйста, заполните все обязательные поля основной информации.'
			)
			setIsSubmitting(false)
			return
		}
		if (isNaN(Number(formData.price))) {
			setError('Цена должна быть числом.')
			toast.error('Цена должна быть числом.')
			setIsSubmitting(false)
			return
		}
		// if (!selectedFile) {
		//      toast.error("Пожалуйста, загрузите главную фотографию.");
		//      setIsSubmitting(false);
		//      return;
		// }

		if (fields?.fields) {
			for (const field of fields.fields) {
				const value = formData.fields[field.name]
				const file = selectedDynamicFiles[field.name]

				if (field.required) {
					if (field.type === 'Boolean') {
						if (value === undefined) {
							// toast.error(`Пожалуйста, заполните обязательное булевое поле: "${field.name}"`);
							// setIsSubmitting(false);
							return
						}
					} else if (field.type === 'File') {
						if (!file) {
							toast.error(
								`Пожалуйста, загрузите обязательный файл: "${field.name}"`
							)
							setIsSubmitting(false)
							return
						}
					} else {
						if (!value || (typeof value === 'string' && value.trim() === '')) {
							toast.error(
								`Пожалуйста, заполните обязательное поле: "${field.name}"`
							)
							setIsSubmitting(false)
							return
						}
					}
				}

				if (
					(field.type === 'Double' || field.type === 'Integer') &&
					value &&
					isNaN(Number(value))
				) {
					toast.error(`Поле "${field.name}" должно быть числом.`)
					setIsSubmitting(false)
					return
				}
			}
		}

		try {
			const uploadPromises: Promise<{ fieldName: string; url: string }>[] = []

			if (selectedFile) {
				uploadPromises.push(
					postImage(selectedFile).then(url => ({ fieldName: 'mainImage', url }))
				)
			}

			if (selectedVideo) {
				uploadPromises.push(
					postVideo(selectedVideo).then(url => ({ fieldName: 'videoUrl', url }))
				)
			}

			if (fields?.fields) {
				fields.fields.forEach(fieldDefinition => {
					if (fieldDefinition.type === 'File') {
						const fieldName = fieldDefinition.name
						const file = selectedDynamicFiles[fieldName]
						if (file) {
							uploadPromises.push(
								postDoc(file).then(url => ({ fieldName, url }))
							)
						}
					}
				})
			}

			const uploadResults = await Promise.all(uploadPromises)

			const uploadedUrls: Record<string, string> = {}
			const galleryUrls: string[] = []

			uploadResults.forEach(result => {
				uploadedUrls[result.fieldName] = result.url
			})
			if (selectedFiles.length > 0) {
				const savedUrls = await postImages(selectedFiles)
				galleryUrls.push(...savedUrls)
			}

			const price = Number(formData.price)

			const processedFields: Record<string, string | number | boolean> = {}

			if (fields?.fields && fields.fields.length > 0) {
				fields.fields.forEach(fieldDefinition => {
					const fieldName = fieldDefinition.name
					const fieldType = fieldDefinition.type

					let finalValue: string | number | boolean | undefined

					if (fieldType === 'File') {
						finalValue = uploadedUrls[fieldName] || ''
						if (finalValue) {
							processedFields[fieldName] = finalValue
						}
					} else if (fieldType === 'Boolean') {
						if (formData.fields[fieldName] !== undefined) {
							finalValue = formData.fields[fieldName] === 'true'
							processedFields[fieldName] = finalValue
						} else if (fieldDefinition.required) {
							processedFields[fieldName] = false
						}
					} else if (fieldType === 'Double' || fieldType === 'Integer') {
						const numValue = Number(formData.fields[fieldName])
						if (!isNaN(numValue)) {
							finalValue =
								fieldType === 'Double'
									? Number.isInteger(numValue)
										? numValue + 0.0001
										: numValue
									: numValue
							processedFields[fieldName] = finalValue
						} else if (fieldDefinition.required) {
							processedFields[fieldName] = fieldType === 'Double' ? 0.0 : 0
						}
					} else {
						finalValue = formData.fields[fieldName]
						if (finalValue !== undefined && finalValue !== '') {
							processedFields[fieldName] = finalValue
						} else if (fieldDefinition.required) {
							processedFields[fieldName] = ''
						}
					}
				})
			}

			const listingData: ICreateListing = {
				title: formData.title,
				categoryId: activeCategory!.id,
				subCategoryId: selectedSubCategory
					? Number(selectedSubCategory.id)
					: activeCategory!.id,
				description: formData.description,
				price: price,
				mainImage: uploadedUrls['mainImage'] || '',
				images: galleryUrls,
				videoUrl: uploadedUrls['videoUrl'] || '',
				invest: formData.invest,
				country: formData.country,
				city: formData.city,
				fullAddress: formData.fullAddress,
				premiumStartDate: null,
				premiumEndDate: null,
				fields: processedFields,
				status: 'INACTIVE',
			}

			const listing = await postListings(listingData)
			setListingId(listing.id || null)
			setIsCreated(true)

			setFormData({
				title: '',
				description: '',
				price: '',
				country: '',
				invest: false,
				city: '',
				fullAddress: '',
				mainImage: '',
				videoUrl: '',
				fields: {},
				status: 'INACTIVE',
			})
			setSelectedFile(null)
			setSelectedVideo(null)
			setSelectedDynamicFiles({})
			setSelectedInvestType(null)
			setActiveCategory(null)
			setSubCategories([])
			setSelectedSubCategory(null)
			setFields(null)
		} catch (error) {
			const err = error as AxiosError
			console.error('❌ Ошибка при создании листинга:', err.message || err)
		} finally {
			setIsSubmitting(false)
		}
	}

	return (
		<>
			<ListingWrapper>
				<ListingTitle>
					<ProfileTitle bgc='#CABDFF'>{t('listingCreate.title1')}</ProfileTitle>
				</ListingTitle>

				<ListingInvest>
					<div style={{ position: 'relative', display: 'inline-block' }}>
						<ListingButton
							onMouseEnter={() => setTooltip('invest')}
							onMouseLeave={() => setTooltip(null)}
							onClick={() => handleInvestChange('true')}
							style={{
								position: 'relative',
								border: `1px solid ${colors.btnMainColor}`,
								borderRadius: '5px',
								backgroundColor:
									selectedInvestType === 'true'
										? colors.btnMainColor
										: colors.mainWhiteTextColor,
								color:
									selectedInvestType === 'true'
										? colors.mainWhiteTextColor
										: colors.mainTextColor,
								cursor: 'pointer',
								transition:
									'background-color 0.3s, color 0.3s, border-color 0.3s',
								opacity: isSubmitting ? 0.6 : 1,
								pointerEvents: isSubmitting ? 'none' : 'auto',
							}}
							disabled={isSubmitting}
						>
							{t('listingCreate.investType')}
							{tooltip === 'invest' && (
								<div className='tooltip-popup'>
									{t('listingCreate.investDescribe')}
								</div>
							)}
						</ListingButton>
					</div>

					<div
						style={{
							position: 'relative',
							display: 'inline-block',
							marginLeft: '10px',
						}}
					>
						<ListingButton
							onMouseEnter={() => setTooltip('sell')}
							onMouseLeave={() => setTooltip(null)}
							onClick={() => handleInvestChange('false')}
							style={{
								border: `1px solid ${colors.btnMainColor}`,
								borderRadius: '5px',
								backgroundColor:
									selectedInvestType === 'false'
										? colors.btnMainColor
										: colors.mainWhiteTextColor,
								color:
									selectedInvestType === 'false'
										? colors.mainWhiteTextColor
										: colors.mainTextColor,
								cursor: 'pointer',
								transition:
									'background-color 0.3s, color 0.3s, border-color 0.3s',
								opacity: isSubmitting ? 0.6 : 1,
								pointerEvents: isSubmitting ? 'none' : 'auto',
							}}
							disabled={isSubmitting}
						>
							{t('listingCreate.businessSale')}
							{tooltip === 'sell' && (
								<div className='tooltip-popup'>
									{t('listingCreate.businessSaleDescribe')}
								</div>
							)}
						</ListingButton>
					</div>
				</ListingInvest>

				{selectedInvestType !== null && (
					<>
						<ListingTitle>
							<ProfileTitle bgc='#CABDFF'>
								{t('listingCreate.title2')}
							</ProfileTitle>
						</ListingTitle>
						<ListingTop>
							<DropDown
								options={dropDownOptions}
								placeholder={t('listingCreate.placeholderCat')}
								isRounded={true}
								width='fit-content'
								onChange={handleCategoryChange}
							/>

							{subCategories?.length > 0 ? (
								<DropDown
									options={dropDownOptionsSub}
									placeholder={t('listingCreate.placeholderSubCat')}
									isRounded={true}
									width='fit-content'
									onChange={handleSubCategoryChange}
								/>
							) : activeCategory && selectedInvestType !== null ? (
								<p>{t('listingCreate.noSubCatFound')}</p>
							) : (
								selectedInvestType !== null && (
									<p>{t('listingCreate.subCatPending')}</p>
								)
							)}
						</ListingTop>
					</>
				)}

				{selectedInvestType !== null && activeCategory && (
					<>
						<ListingTitle>
							<ProfileTitle bgc={colors.btnSecondColor}>
								{t('listingCreate.title3')}
							</ProfileTitle>
						</ListingTitle>
						<ListingMain>
							{error && <p style={{ color: 'red' }}>{error}</p>}

							{[
								{
									name: 'title',
									label: t('listingCreate.fieldTitle'),
									type: 'text',
								},
								{
									name: 'description',
									label: t('listingCreate.fieldDescription'),
									type: 'textarea',
								},
								{
									name: 'price',
									label:
										selectedInvestType === 'true'
											? t('listingCreate.fieldPriceBudget')
											: t('listingCreate.fieldPriceValue'),
									type: 'number',
								},
								{
									name: 'country',
									label: t('listingCreate.fieldCountry'),
									type: 'text',
								},
								{
									name: 'city',
									label: t('listingCreate.fieldCity'),
									type: 'text',
								},
								{
									name: 'fullAddress',
									label: t('listingCreate.fieldFullAddress'),
									type: 'text',
								},
							].map((field, index) => (
								<ListingField key={index}>
									<ListingLabel>
										{field.label}
										<span style={{ color: 'red' }}> *</span>
									</ListingLabel>

									{field.type === 'textarea' ? (
										<textarea
											name={field.name}
											className='textarea'
											rows={4}
											cols={35}
											value={
												formData[
													field.name as keyof Omit<
														typeof formData,
														'invest' | 'fields' | 'status'
													>
												] as string
											}
											onChange={e =>
												setFormData({
													...formData,
													[field.name]: e.target.value,
												})
											}
											placeholder={field.label}
										/>
									) : field.name === 'country' ? (
										<Select
											options={countryOptions}
											value={countryOptions.find(
												option => option.label === formData.country
											)}
											onChange={selectedOption =>
												setFormData({
													...formData,
													country: selectedOption?.label || '',
												})
											}
											placeholder={t('placeholders.chooseCountry')}
										/>
									) : (
										<input
											type={field.type}
											name={field.name}
											className={field.type}
											value={
												formData[
													field.name as keyof Omit<
														typeof formData,
														'invest' | 'fields' | 'status'
													>
												]?.toString() || ''
											}
											onChange={e =>
												setFormData({
													...formData,
													[field.name]: e.target.value,
												})
											}
											placeholder={field.label}
										/>
									)}
								</ListingField>
							))}

							<ListingField>
								<ListingLabel>{t('listingCreate.mainPhoto')}(4:3)</ListingLabel>
								<input
									type='file'
									accept='image/*'
									onChange={handleFileChange}
								/>
								{selectedFile && <span>{selectedFile.name}</span>}
							</ListingField>
							<ListingField>
								<ListingLabel>{t('listingCreate.galleryPhotos')}</ListingLabel>
								<input
									type='file'
									accept='image/*'
									multiple
									onChange={handleMultipleFileChange}
								/>
								{selectedFiles.length > 0 && (
									<ul>
										{selectedFiles.map((file, index) => (
											<li key={index}>{file.name}</li>
										))}
									</ul>
								)}
							</ListingField>

							<ListingField>
								<ListingLabel>{t('listingCreate.video')}</ListingLabel>
								<input
									type='file'
									accept='video/*'
									onChange={handleVideoChange}
								/>
								{selectedVideo && <span>{selectedVideo.name}</span>}
							</ListingField>
						</ListingMain>
					</>
				)}

				{selectedInvestType !== null &&
					activeCategory &&
					fields?.fields &&
					fields.fields.length > 0 && (
						<ListingTitle>
							<ProfileTitle bgc={colors.btnMainColor}>
								{t('listingCreate.additionalFieldsTitle')}
							</ProfileTitle>
						</ListingTitle>
					)}

				{selectedInvestType !== null && activeCategory && fields?.fields && (
					<ListingFields>
						{fields.fields.map(field => {
							let inputType = 'textarea'
							let stepValue = undefined
							let accept = undefined

							if (field.type === 'Double') {
								inputType = 'number'
								stepValue = '0.01'
							} else if (field.type === 'Boolean') {
								inputType = 'checkbox'
							} else if (field.type === 'Integer') {
								inputType = 'number'
							} else if (field.type === 'File') {
								inputType = 'file'
								accept = '*'
							}

							return (
								<ListingField key={field.name}>
									<ListingLabel>
										{field.name}
										{field.name === 'Роялти' && ' %'}
										{field?.required && (
											<span style={{ color: 'red' }}> *</span>
										)}
									</ListingLabel>

									{field.type === 'List' && listFieldOptions[field.name] ? (
										<>
											<FormGroup>
												{listFieldOptions[field.name].map(option => (
													<FormControlLabel
														key={option}
														control={
															<Checkbox
																checked={
																	Array.isArray(formData.fields[field.name]) &&
																	formData.fields[field.name].includes(option)
																}
																onChange={e =>
																	handleMultiSelectChange(
																		field.name,
																		option,
																		e.target.checked
																	)
																}
															/>
														}
														label={option}
													/>
												))}
											</FormGroup>
										</>
									) : inputType === 'textarea' ? (
										<textarea
											name={field.name}
											className='textarea'
											rows={4}
											cols={15}
											value={formData.fields[field.name] || ''}
											onChange={e =>
												handleFieldChange(field.name, e.target.value)
											}
											placeholder={`${field.name}${field?.required ? '*' : ''}`}
										/>
									) : inputType === 'checkbox' ? (
										<FormGroup>
											<FormControlLabel
												control={
													<Switch
														style={{
															display: 'flex',
															alignItems: 'flex-start',
															maxHeight: '20px',
														}}
														checked={formData.fields[field.name] === 'true'}
														onChange={e =>
															handleFieldChange(
																field.name,
																e.target.checked.toString()
															)
														}
													/>
												}
												label={`${field.name}${field?.required ? '*' : ''}`}
												sx={{
													color:
														formData.fields[field.name] === 'true'
															? colors.btnMainColor
															: colors.greyTextColor,
												}}
											/>
										</FormGroup>
									) : inputType === 'file' ? (
										<>
											<input
												type='file'
												accept={accept}
												onChange={e => handleDynamicFileChange(field.name, e)}
											/>
											{selectedDynamicFiles[field.name] && (
												<span>{selectedDynamicFiles[field.name]?.name}</span>
											)}
										</>
									) : (
										<div>
											<input
												type={inputType}
												step={stepValue}
												placeholder={`${field.name}${
													field?.required ? '*' : ''
												}`}
												value={formData.fields[field.name]?.toString() || ''}
												onChange={e =>
													handleFieldChange(field.name, e.target.value)
												}
											/>
										</div>
									)}
								</ListingField>
							)
						})}
					</ListingFields>
				)}

				{selectedInvestType !== null &&
					activeCategory &&
					(!fields?.fields || fields.fields.length === 0) && (
						<p>{t('listingCreate.noAdditionalFields')}</p>
					)}
				{selectedInvestType !== null && activeCategory && (
					<ListingButton onClick={handlePostListings} disabled={isSubmitting}>
						{isSubmitting
							? t('listingCreate.creatingLoading')
							: t('listingCreate.createListingButton')}
					</ListingButton>
				)}
			</ListingWrapper>

			{isCreated && (
				<CreateModal
					listingId={listingId}
					closeModal={() => setIsCreated(false)}
				/>
			)}
		</>
	)
}
